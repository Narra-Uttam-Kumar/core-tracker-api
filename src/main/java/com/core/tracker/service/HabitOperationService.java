package com.core.tracker.service;

import com.core.tracker.model.User;
import java.util.List;
import java.util.Map;

public interface HabitOperationService {
    Map<String, List<Boolean>> getUserHabitsMatrix(User user);
    void toggleHabitDay(User user, String habitType, Integer dayNumber);
}