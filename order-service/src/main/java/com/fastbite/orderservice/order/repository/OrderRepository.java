package com.fastbite.orderservice.order.repository;

import com.fastbite.orderservice.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
