package com.core.tracker.repository;

import com.core.tracker.model.HabitGoal;
import com.core.tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HabitGoalRepository extends JpaRepository<HabitGoal, Long> {
    List<HabitGoal> findByUser(User user);
    Optional<HabitGoal> findByUserAndHabitType(User user, String habitType);
}