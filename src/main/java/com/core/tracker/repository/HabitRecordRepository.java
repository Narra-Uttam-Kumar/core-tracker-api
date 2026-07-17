package com.core.tracker.repository;

import com.core.tracker.model.HabitRecord;
import com.core.tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HabitRecordRepository extends JpaRepository<HabitRecord, Long> {
    List<HabitRecord> findByUser(User user);
    Optional<HabitRecord> findByUserAndHabitTypeAndDayNumber(User user, String habitType, Integer dayNumber);
}