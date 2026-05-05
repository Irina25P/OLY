package com.example.notification.model;

import com.example.notification.model.enums.Topic;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Embeddable // No longer an Entity, no separate identity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Notification {

    @Column(name = "id")
    private UUID id = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    @Column(name = "topic")
    private Topic topic;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "send_at")
    private Timestamp sendAt;
}