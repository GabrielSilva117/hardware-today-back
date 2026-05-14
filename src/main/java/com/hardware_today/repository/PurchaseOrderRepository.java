package com.hardware_today.repository;

import com.hardware_today.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    @Query("SELECT DISTINCT po FROM PurchaseOrder po LEFT JOIN FETCH po.items WHERE po.user.id = :userId ORDER BY po.placedAt DESC")
    List<PurchaseOrder> findAllByUserIdWithItems(@Param("userId") UUID userId);

    @Query("SELECT DISTINCT po FROM PurchaseOrder po LEFT JOIN FETCH po.items WHERE po.id = :id AND po.user.id = :userId")
    Optional<PurchaseOrder> findByIdAndUserIdWithItems(@Param("id") UUID id, @Param("userId") UUID userId);
}
