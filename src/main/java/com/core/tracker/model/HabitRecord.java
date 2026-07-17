package com.core.tracker.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "habit_records", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "habit_type", "day_number"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "habit_type", nullable = false)
    private String habitType;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;
}