package com.ecommerce.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.notification.entity.ProcessedEventEntity;

public interface ProcessedEventRepository
        extends JpaRepository<ProcessedEventEntity, String> {
}