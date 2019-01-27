package com.example.firebasechat.presentation.contacts;

public class Usermodel {
    private String avatarUrl;
    private String displayName;
    private String login;

    public Usermodel(String avatarUrl, String displayName, String login) {
        this.avatarUrl = avatarUrl;
        this.displayName = displayName;
        this.login = login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLogin() {
        return login;
    }
}
