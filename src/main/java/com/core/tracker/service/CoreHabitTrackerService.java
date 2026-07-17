package com.core.tracker.service;

import com.core.tracker.model.HabitGoal;
import com.core.tracker.model.HabitRecord;
import com.core.tracker.model.User;
import com.core.tracker.repository.HabitGoalRepository;
import com.core.tracker.repository.HabitRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class CoreHabitTrackerService implements HabitOperationService, GoalConfigurationService {

    private final HabitRecordRepository habitRepository;
    private final HabitGoalRepository goalRepository;
    
    private final Map<String, Integer> DEFAULT_HABIT_GOALS = Map.ofEntries(
    	    Map.entry("wakeup", 30),
    	    Map.entry("nosnooze", 30),
    	    Map.entry("water", 30),
    	    Map.entry("gym", 20),
    	    Map.entry("stretching", 30),
    	    Map.entry("read", 30),
    	    Map.entry("meditation", 30),
    	    Map.entry("study", 25),
    	    Map.entry("skincare", 30),
    	    Map.entry("socialmedia", 30),
    	    Map.entry("noalcohol", 30),
    	    Map.entry("expenses", 30)
    	);

    public CoreHabitTrackerService(HabitRecordRepository habitRepository, HabitGoalRepository goalRepository) {
        this.habitRepository = habitRepository;
        this.goalRepository = goalRepository;
    }

    @Override
    public Map<String, List<Boolean>> getUserHabitsMatrix(User user) {
        List<HabitRecord> records = habitRepository.findByUser(user);
        Map<String, List<Boolean>> matrix = new HashMap<>();
        
        for (String habitKey : DEFAULT_HABIT_GOALS.keySet()) {
            Boolean[] initialYear = new Boolean[365];
            Arrays.fill(initialYear, false);
            matrix.put(habitKey, new ArrayList<>(Arrays.asList(initialYear)));
        }

        for (HabitRecord record : records) {
            if (matrix.containsKey(record.getHabitType())) {
                int index = record.getDayNumber() - 1;
                if (index >= 0 && index < 365) {
                    matrix.get(record.getHabitType()).set(index, true);
                }
            }
        }
        return matrix;
    }

    @Override
    public Map<String, Integer> getUserHabitGoals(User user) {
        List<HabitGoal> savedGoals = goalRepository.findByUser(user);
        Map<String, Integer> goalsMap = new HashMap<>(DEFAULT_HABIT_GOALS);
        
        for (HabitGoal goal : savedGoals) {
            if (goalsMap.containsKey(goal.getHabitType())) {
                goalsMap.put(goal.getHabitType(), goal.getTargetGoal());
            }
        }
        return goalsMap;
    }

    @Override
    @Transactional
    public void toggleHabitDay(User user, String habitType, Integer dayNumber) {
        if (!DEFAULT_HABIT_GOALS.containsKey(habitType) || dayNumber < 1 || dayNumber > 365) {
            throw new IllegalArgumentException("Invalid tracking matrix index.");
        }

        Optional<HabitRecord> existingRecord = habitRepository
                .findByUserAndHabitTypeAndDayNumber(user, habitType, dayNumber);

        if (existingRecord.isPresent()) {
            habitRepository.delete(existingRecord.get());
        } else {
            HabitRecord newRecord = HabitRecord.builder()
                    .user(user)
                    .habitType(habitType)
                    .dayNumber(dayNumber)
                    .build();
            habitRepository.save(newRecord);
        }
    }

    @Override
    @Transactional
    public void updateHabitGoal(User user, String habitType, Integer targetGoal) {
        if (!DEFAULT_HABIT_GOALS.containsKey(habitType) || targetGoal < 0) {
            throw new IllegalArgumentException("Target criteria out of minimal bounds.");
        }

        Optional<HabitGoal> existingGoal = goalRepository.findByUserAndHabitType(user, habitType);
        if (existingGoal.isPresent()) {
            existingGoal.get().setTargetGoal(targetGoal);
            goalRepository.save(existingGoal.get());
        } else {
            HabitGoal newGoal = HabitGoal.builder()
                    .user(user)
                    .habitType(habitType)
                    .targetGoal(targetGoal)
                    .build();
            goalRepository.save(newGoal);
        }
    }
}