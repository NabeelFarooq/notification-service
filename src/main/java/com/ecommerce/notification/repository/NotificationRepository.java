package com.ecommerce.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.notification.entity.NotificationEntity;

public interface NotificationRepository
        extends JpaRepository<NotificationEntity, Long> {
}