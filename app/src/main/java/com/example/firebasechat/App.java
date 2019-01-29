package com.example.firebasechat;

import android.app.Application;

import com.example.firebasechat.data.FirebaseRepository;
import com.example.firebasechat.data.SharedPreferecesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class App extends Application {

    private NotificationHelper notificationHelper;
    private SharedPreferecesManager sharedPreferecesManager;
    private FirebaseRepository firebaseRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        firebaseRepository = new FirebaseRepository(
                FirebaseAuth.getInstance(),
                FirebaseFirestore.getInstance());

        sharedPreferecesManager = new SharedPreferecesManager(this);
        notificationHelper = new NotificationHelper(this);
    }

    public NotificationHelper getNotificationHelper() {
        return notificationHelper;
    }

    public SharedPreferecesManager getSharedPreferecesManager() {
        return sharedPreferecesManager;
    }

    public FirebaseRepository getFirebaseRepository() {
        return firebaseRepository;
    }
}
