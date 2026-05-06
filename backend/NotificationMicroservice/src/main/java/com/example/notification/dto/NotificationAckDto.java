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
public class NotificationAckDto {

    @NotNull(message = "Notification id cannot be null")
    private UUID notificationId;

    @NotNull(message = "Receiver id cannot be null")
    private UUID receiverId;
}
