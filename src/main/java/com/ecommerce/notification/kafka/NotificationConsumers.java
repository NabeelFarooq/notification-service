package com.ecommerce.notification.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecommerce.notification.service.NotificationProcessor;

@Component
public class NotificationConsumers {

    private final NotificationProcessor processor;

    public NotificationConsumers(
            NotificationProcessor processor) {

        this.processor = processor;
    }

    @KafkaListener(
            topics = "order-created",
            groupId = "notification-service-group")
    public void orderCreated(String payload) {

        processor.process(payload);
    }

    @KafkaListener(
            topics = "payment-success",
            groupId = "notification-service-group")
    public void paymentSuccess(String payload) {

        processor.process(payload);
    }

    @KafkaListener(
            topics = "payment-failed",
            groupId = "notification-service-group")
    public void paymentFailed(String payload) {

        processor.process(payload);
    }

    @KafkaListener(
            topics = "delivery-created",
            groupId = "notification-service-group")
    public void deliveryCreated(String payload) {

        processor.process(payload);
    }

    @KafkaListener(
            topics = "delivery-out-for-delivery",
            groupId = "notification-service-group")
    public void outForDelivery(String payload) {

        processor.process(payload);
    }

    @KafkaListener(
            topics = "order-delivered",
            groupId = "notification-service-group")
    public void delivered(String payload) {

        processor.process(payload);
    }
}