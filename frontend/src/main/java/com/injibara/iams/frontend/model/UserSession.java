package com.injibara.iams.frontend.model;

public class UserSession {
    private static UserSession instance;
    private String token;
    private String username;
    private String fullName;
    private String role;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    public void set(String token, String username, String fullName, String role) {
        this.token = token;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public void clear() {
        token = null; username = null; fullName = null; role = null;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public boolean isLoggedIn() { return token != null; }
}
