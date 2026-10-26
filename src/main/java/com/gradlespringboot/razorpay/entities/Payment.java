package com.gradlespringboot.razorpay.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false, unique = true)
    private int paymentId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "amount", nullable = false)
    private float amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "mode")
    private String mode;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt; // Payment time

}
