package com.konstantin.habittracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.konstantin.habittracker.TestEntity;
import com.konstantin.habittracker.TestRepository;

@RestController
public class TestController {

    private final TestRepository repository;

    public TestController(TestRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/db-test")
    public String testDatabase() {

        TestEntity entity = new TestEntity();
        entity.setMessage("Database connection works");

        repository.save(entity);

        return "Saved to database";
    }

    @GetMapping("/")
    public String home() {
        return "Backend is running";
    }
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
