package com.example.firebasechat;

import android.app.Application;

import com.example.firebasechat.data.SharedPreferecesManager;

public class App extends Application {

    private NotificationHelper notificationHelper;
    private SharedPreferecesManager sharedPreferecesManager;

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferecesManager = new SharedPreferecesManager(this);
        notificationHelper = new NotificationHelper(this);
    }

    public NotificationHelper getNotificationHelper() {
        return notificationHelper;
    }

    public SharedPreferecesManager getSharedPreferecesManager() {
        return sharedPreferecesManager;
    }
}
