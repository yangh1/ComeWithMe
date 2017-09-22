package edu.rosehulman.ComeWithMe.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public class User {

    @JsonIgnore
    private String key;

    private String username;
    private String email;
    private Map<String, User> friends;

    public User() {}

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Map<String, User> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, User> friends) {
        this.friends = friends;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
