package edu.rosehulman.ComeWithMe.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;


public class Event {
    @JsonIgnore
    private String key;

    private String title;
    private String host;
    private String date;
    private String time;
    private String location;
    private String eventKey;
    private Map<String, Attendee> attendees;

    public Event(){}

    public Event(String title, String host, String date, String time, String location, String eventKey){
        this.title = title;
        this.host = host;
        this.date = date;
        this.time = time;
        this.location = location;
        this.eventKey = eventKey;
    }

    public Map<String, Attendee> getAttendees() {
        return attendees;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public void setAttendees(Map<String, Attendee> attendees) {
        this.attendees = attendees;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addAttendee(String key,Attendee attendee){
        attendees.put(key,attendee);
    }
}
