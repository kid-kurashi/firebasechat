package com.example.firebasechat;

import android.app.Application;

public class App extends Application {

    private NotificationHelper notificationHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        notificationHelper = new NotificationHelper(this);
    }

    public NotificationHelper getNotificationHelper() {
        return notificationHelper;
    }
}
