# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

`hardware-today-back` is the **monolith REST API** of the "Hardware Today" e-commerce system: a
Spring Boot 3.3.2 / Java 21 / Maven app. It is one node in a microservice ecosystem — a payment
service lives behind an API gateway (`ht.services.gateway.url`, default `http://localhost:8087`)
and is called over OpenFeign. (A log service, `ht-log-ms`, was planned but decommissioned — never
integrated, now removed; log aggregation instead goes through Loki/Grafana, see `../services/`.)

Maven coordinates are still the Spring Initializr defaults (`com.example:demo`), so the build
artifact is `target/demo-0.0.1-SNAPSHOT.jar`. The main class is
`com.hardware_today.EcommerceApplication`.

## Commands

```bash
./mvnw clean package            # build jar (add -DskipTests to skip tests)
./mvnw spring-boot:run          # run the API (port 8080)
./mvnw test                     # run tests
./mvnw test -Dtest=DemoApplicationTests#contextLoads   # single test
```

Only test is `DemoApplicationTests.contextLoads` — a full `@SpringBootTest` that boots the whole
context, so it needs PostgreSQL and RabbitMQ reachable or it fails.

## Local infrastructure

`docker-compose.yml` and `Dockerfile` are empty placeholders — bring services up yourself:

- **PostgreSQL** `hardware_today` on `localhost:5432` (see `spring.datasource.*`)
- **RabbitMQ** on `localhost:5672`
- **MinIO** on `localhost:9000` (S3-compatible object storage for product images)
- the **gateway + payment service** for any checkout/payment path

`src/main/resources/application.properties` holds all connection settings and secrets and is
**gitignored** — it must exist locally and is never committed.

## Architecture

### Configuration packages (two of them)

- `config/` — RabbitMQ (`MQConfig`, `MQMessageConfig`) and STOMP WebSocket (`WebSocketConfig`)
- `configuration/` — Spring Security, JWT, object storage, cache, `GlobalExceptionHandler`

### Auth — stateless JWT in cookies

`SecurityAutoConfiguration` is excluded in properties; `configuration/SecurityConfig` defines the
chain (CSRF off, `STATELESS`). `JwtAuthFilter` runs before `UsernamePasswordAuthenticationFilter`:
it reads `access_token` / `refresh_token` **cookies** (not headers), and when only the refresh
token is valid it mints a new access token and writes the cookie onto the response mid-request.
Public paths: `/user/login`, `/user/register`, `/user/refresh-token`, `/ws/**`; everything else
requires auth. `CustomUserDetails.getAuthorities()` returns an empty list — there is **no
role-based authorization yet**. `jwt.*` config in `utils/JwtUtil.java`.

CORS (`WebConfig`) allows `http://localhost:8081` with credentials.

### Database — Flyway owns the schema

`spring.jpa.hibernate.ddl-auto=validate`. Hibernate never creates or alters tables; every schema
change is a new `V<n>__*.sql` in `src/main/resources/db/migration` (`V1__baseline_schema.sql` is
the baseline snapshot of the entities). `spring.flyway.clean-disabled=true`. Product search relies
on `pg_trgm` GIN indexes created in the migration.

Entity conventions: table name comes from `@Entity(name = "...")`, not `@Table`; Lombok
`@Getter/@Setter/@NoArgsConstructor` throughout. `purchase_order_item` deliberately stores a
denormalized snapshot (`product_id`, `product_name`, `unit_price`) with **no FK** to `products` —
order history must not change when a product does. `product_assets` is 1:1 with `products` and
holds object-storage keys in `miniature` / `gallery` / `detail`.

### Product query stack

Three generations coexist; only the newest is wired up:

1. **Current** — `specifications/ProductSpecification` builds a JPA `Specification` from
   `ProductFilterModel` (term search + brand/category/price filters). `ProductService` runs it via
   `productRepository.findBy(spec, q -> q.as(ProductProjection.class).page(pageable))`, returning a
   `Page` of the `ProductProjection` interface projection. Endpoint: `POST /products` with the
   filter in the body plus `page` / `size` query params.
2. Unused — `repository/Product/ProductCustomRepository(+Impl)`, a hand-rolled `CriteriaBuilder`
   query. Kept, not called.
3. Unused — commented-out `List<Product>` methods in the controller/service.

`ProductFilterCacheService` + `ProductFilterCacheConfig`: a Caffeine cache keyed by username
remembers each user's **last applied filter** for 5 minutes (creation-anchored TTL — updates
refresh the value but not the expiry). `ProductFilterModel.hasCriteria()` decides whether an
incoming request stores a new filter or replays the cached one, so the client can send an empty
body on follow-up page requests.

Pagination responses use `@EnableSpringDataWebSupport(PageSerializationMode.VIA_DTO)` — `Page` is
serialized through a stable DTO, not the raw `PageImpl`.

### Messaging & real-time

- **RabbitMQ** (`MQConfig`): `payment.exchange` (topic) binds `payment.#` → `payment_dispatch`;
  `notification.exchange` (direct) binds `email.create` → `email_dispatch`. Producers live in
  `publishers/`.
- **Payment flow is dual-path**: `PaymentPublisher` both calls the payment service synchronously
  over Feign (`callPaymentMicroservice`) and publishes to the exchange; the caller then persists a
  `PurchaseOrder` from the cart and broadcasts the result over WebSocket to
  `/topic/payment/{cartId}`.
- **STOMP WebSocket** (`WebSocketConfig`): endpoint `/ws` (SockJS), broker prefix `/topic`,
  app-destination prefix `/app`.

### External services

`@EnableFeignClients`. All Feign clients target `${ht.services.gateway.url}` and prefix the path
with the service name (e.g. `PaymentFeign` → `POST /ht-payment-ms/payment`). Per-client timeouts
in `feign.client.config.*`.

### Object storage — AWS S3 → MinIO migration in progress

`configuration/MinioConfig` provides the **active** `S3Client` and `S3Presigner` beans
(path-style access, endpoint override to MinIO). `configuration/AWSConfig`'s S3 bean is commented
out. `AWSService` / `AWSController` (`GET /files/{fileKey}`) still serve bytes by key from the
configured bucket and inject whichever `S3Client` bean is present.

### Error handling — inconsistent, know both styles

`GlobalExceptionHandler` (`@RestControllerAdvice`) maps `custom/controller_exceptions/*`
(`NotFoundException`, `BadRequestException`, `UnauthorizedException`, `ForbiddenException`) to
status codes, with a catch-all `Exception` → 500. But many controller methods predate it and still
`try/catch (Exception) { e.printStackTrace(); return null; }` or return a bare 500. Prefer throwing
the custom exceptions in new code.

### Deprecated

`CartCookieService` is `@Deprecated` — cart identity was moved out of HTTP-only cookies (see the
`cart_storage_refactor` history).

## Frontend

`hardware_today_front_angular/` is a separate Angular app, gitignored, not part of this Maven
build. Ignore it unless explicitly asked.
