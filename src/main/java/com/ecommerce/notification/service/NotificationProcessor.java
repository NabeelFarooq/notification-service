package com.ecommerce.notification.service;

import java.time.LocalDateTime;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

import com.ecommerce.notification.entity.NotificationEntity;
import com.ecommerce.notification.entity.ProcessedEventEntity;

import com.ecommerce.notification.event.CommonEvent;

import com.ecommerce.notification.repository.NotificationRepository;
import com.ecommerce.notification.repository.ProcessedEventRepository;

@Service
public class NotificationProcessor {

    private final ObjectMapper objectMapper;

    private final ProcessedEventRepository
            processedEventRepository;

    private final NotificationRepository
            notificationRepository;

    public NotificationProcessor(
            ObjectMapper objectMapper,
            ProcessedEventRepository processedEventRepository,
            NotificationRepository notificationRepository) {

        this.objectMapper = objectMapper;

        this.processedEventRepository =
                processedEventRepository;

        this.notificationRepository =
                notificationRepository;
    }

    @Transactional
    public void process(String payload) {

        try {

            CommonEvent event =
                    objectMapper.readValue(
                            payload,
                            CommonEvent.class);

            if (event.getEventId() == null
                    || event.getEventId().isBlank()) {

                throw new IllegalArgumentException(
                        "Missing eventId");
            }

            // Idempotency
            if (processedEventRepository
                    .existsById(event.getEventId())) {

                System.out.println(
                        "Duplicate notification ignored: "
                        + event.getEventId());

                return;
            }

            String message =
                    createMessage(event);

            NotificationEntity notification =
                    new NotificationEntity();

            notification.setEventId(
                    event.getEventId());

            notification.setEventType(
                    event.getEventType());

            notification.setOrderId(
                    event.getOrderId());

            notification.setCustomerId(
                    event.getCustomerId());

            notification.setChannel("SMS");

            notification.setMessage(message);

            notification.setCreatedAt(
                    LocalDateTime.now());

            notificationRepository.save(
                    notification);

            processedEventRepository.save(
                    new ProcessedEventEntity(
                            event.getEventId(),
                            LocalDateTime.now()));

            System.out.println(
                    "SMS SENT"
                    + " | Customer: "
                    + event.getCustomerId()
                    + " | Order: "
                    + event.getOrderId()
                    + " | "
                    + message);

        } catch (Exception ex) {

            throw new IllegalArgumentException(
                    "Notification processing failed",
                    ex);
        }
    }

    private String createMessage(
            CommonEvent event) {

        return switch (event.getEventType()) {

            case "ORDER_CREATED" ->
                    "Your order has been created.";

            case "PAYMENT_SUCCESS" ->
                    "Your payment of Rs."
                    + event.getAmount()
                    + " was successful.";

            case "PAYMENT_FAILED" ->
                    "Your payment failed. Reason: "
                    + event.getReason();

            case "DELIVERY_CREATED" ->
                    "Your delivery was created. "
                    + "Tracking number: "
                    + event.getTrackingNumber();

            case "DELIVERY_OUT_FOR_DELIVERY" ->
                    "Your order is out for delivery. "
                    + "Tracking number: "
                    + event.getTrackingNumber();

            case "ORDER_DELIVERED" ->
                    "Your order has been delivered.";

            default ->
                    "Event received: "
                    + event.getEventType();
        };
    }
}