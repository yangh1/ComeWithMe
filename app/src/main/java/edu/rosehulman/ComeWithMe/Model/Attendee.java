package edu.rosehulman.ComeWithMe.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class Attendee {
    @JsonIgnore
    private String key;

    private String username;
    private String email;
    private boolean status;

    public Attendee(){}

    public Attendee(String username,String email,boolean status){
        this.username = username;
        this.email = email;
        this.status = status;
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

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
