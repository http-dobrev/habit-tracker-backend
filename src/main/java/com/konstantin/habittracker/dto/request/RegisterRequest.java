package com.konstantin.habittracker.dto.request;

public class RegisterRequest {
    public String name;
    public String email;
    public String password;

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
}
