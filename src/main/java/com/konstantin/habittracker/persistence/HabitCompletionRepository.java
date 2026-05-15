package com.konstantin.habittracker.persistence;

import com.konstantin.habittracker.model.HabitCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {

    List<HabitCompletion> findByHabitUserIdAndCompletionDate(Long userId, LocalDate completionDate);

    Optional<HabitCompletion> findByHabitIdAndHabitUserIdAndCompletionDate(
            Long habitId,
            Long userId,
            LocalDate completionDate
    );
}