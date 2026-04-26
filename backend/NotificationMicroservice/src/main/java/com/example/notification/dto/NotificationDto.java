package com.example.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    @NotNull(message = "receiver id cannot be null")
    private String receiverId;

    @NotNull(message = "Message cannot be null")
    private String message;
}
