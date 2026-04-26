package com.example.notification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Embeddable // No longer an Entity, no separate identity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Notification {

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "send_at")
    private Timestamp sendAt;
}