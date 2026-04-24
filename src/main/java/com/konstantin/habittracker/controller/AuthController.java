package com.konstantin.habittracker.controller;


import com.konstantin.habittracker.dto.request.RegisterRequest;
import com.konstantin.habittracker.dto.response.RegisterResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){

        //temporary
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new RegisterResponse(
                        "jwt-token-here",
                        3600,
                        "User successfully registered"
                )
        );
    }
}
