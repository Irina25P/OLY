package org.example.usermicroservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "interests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interest implements Serializable {
    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "interest_type", nullable = false)
    private InterestType interestType;
}