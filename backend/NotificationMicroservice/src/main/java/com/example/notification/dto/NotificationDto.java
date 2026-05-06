package com.example.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private UUID id;

    @NotNull(message = "receiver id cannot be null")
    private UUID receiverId;

    @NotNull(message = "Message cannot be null")
    private String message;
}
