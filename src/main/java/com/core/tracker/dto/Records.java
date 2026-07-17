package com.core.tracker.dto;

public final class Records {
    private Records() {}

    public record AuthRequest(String username, String password) {}
    public record ToggleRequest(String habitType, Integer dayNumber) {}
    public record GoalUpdateRequest(String habitType, Integer targetGoal) {}
}