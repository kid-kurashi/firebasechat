package com.example.firebasechat.pojo;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String username;
    private String uId;
    private String deviceToken;
    private Map<String, String> contacts;

    public User(String username, String uId, String deviceToken) {
        this.username = username;
        this.uId = uId;
        this.deviceToken = deviceToken;
        contacts = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public String getuId() {
        return uId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public Map<String, String> getContacts() {
        return contacts;
    }
}
