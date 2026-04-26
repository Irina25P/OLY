package com.example.notification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Table(name = "users")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @Column(name = "id")
    private UUID id;

    @ElementCollection
    @CollectionTable(
            name = "user_notifications",
            joinColumns = @JoinColumn(name = "user_id")
    )
    private List<Notification> notifications;
}