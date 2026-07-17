package com.core.tracker.controller;

import com.core.tracker.dto.Records.ToggleRequest;
import com.core.tracker.dto.Records.GoalUpdateRequest;
import com.core.tracker.model.User;
import com.core.tracker.repository.UserRepository;
import com.core.tracker.service.HabitOperationService;
import com.core.tracker.service.GoalConfigurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitOperationService habitOperationService;
    private final GoalConfigurationService goalConfigurationService;
    private final UserRepository userRepository;

    public HabitController(HabitOperationService habitOperationService, 
                           GoalConfigurationService goalConfigurationService, 
                           UserRepository userRepository) {
        this.habitOperationService = habitOperationService;
        this.goalConfigurationService = goalConfigurationService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Context context tracking error."));
    }

    @GetMapping
    public ResponseEntity<Map<String, List<Boolean>>> getHabits() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(habitOperationService.getUserHabitsMatrix(user));
    }

    @GetMapping("/goals")
    public ResponseEntity<Map<String, Integer>> getGoals() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(goalConfigurationService.getUserHabitGoals(user));
    }

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleHabit(@RequestBody ToggleRequest request) {
        User user = getAuthenticatedUser();
        try {
            habitOperationService.toggleHabitDay(user, request.habitType(), request.dayNumber());
            return ResponseEntity.ok(Map.of("message", "Metric tracking level modified safely."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/goals")
    public ResponseEntity<?> updateGoal(@RequestBody GoalUpdateRequest request) {
        User user = getAuthenticatedUser();
        try {
            goalConfigurationService.updateHabitGoal(user, request.habitType(), request.targetGoal());
            return ResponseEntity.ok(Map.of("message", "System tracking goals adjusted."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}