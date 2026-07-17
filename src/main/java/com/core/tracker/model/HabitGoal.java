package com.core.tracker.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "habit_goals", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "habit_type"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "habit_type", nullable = false)
    private String habitType;

    @Column(name = "target_goal", nullable = false)
    private Integer targetGoal;
}