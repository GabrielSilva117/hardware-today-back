package com.hardware_today.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Forwards the current request's X-Request-Id (set by RequestIdFilter) onto outgoing
 * Feign calls, so ht_payment_ms's logs for the same request correlate in Loki.
 */
@Component
public class FeignRequestIdInterceptor implements RequestInterceptor {

    private static final String HEADER = "X-Request-Id";

    @Override
    public void apply(RequestTemplate template) {
        String requestId = MDC.get("requestId");
        if (requestId != null && !requestId.isBlank()) {
            template.header(HEADER, requestId);
        }
    }
}
