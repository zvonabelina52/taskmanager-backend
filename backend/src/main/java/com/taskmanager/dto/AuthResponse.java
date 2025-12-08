package com.taskmanager.dto;

public class AuthResponse {
    public String token;
    public String username;
    public String email;

    public AuthResponse(String token, String username, String email) {
        this.token = token;
        this.username = username;
        this.email = email;
    }
}