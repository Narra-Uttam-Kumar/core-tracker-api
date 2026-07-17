package com.core.tracker.service;

import com.core.tracker.model.User;
import java.util.Map;

public interface GoalConfigurationService {
    Map<String, Integer> getUserHabitGoals(User user);
    void updateHabitGoal(User user, String habitType, Integer targetGoal);
}