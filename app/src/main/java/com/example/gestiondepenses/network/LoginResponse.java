package com.example.gestiondepenses.network;

import com.example.gestiondepenses.models.User;

public class LoginResponse {
    private String token;
    private User user;

    public String getToken() { return token; }
    public User getUser() { return user; }
}