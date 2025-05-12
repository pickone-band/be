package com.PickOne.domain.messaging.model.domain;

/**
 * Enum for Message Status
 */
public enum MessageStatus {
    SENT,       // Message has been sent by the sender
    DELIVERED,  // Message has been delivered to the recipient
    READ        // Message has been read by the recipient
}