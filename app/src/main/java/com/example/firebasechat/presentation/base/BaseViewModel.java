package com.example.firebasechat.presentation.base;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;

import com.example.firebasechat.firestore_constants.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseViewModel extends ViewModel implements LifecycleObserver {

    protected FirebaseAuth firebaseAuth;
    protected FirebaseUser firebaseUser;
    protected FirebaseFirestore database;

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void initServices() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseFirestore.getInstance();
    }

    protected DocumentReference getCurrentUserReference() {
        return database.collection(Users.COLLECTION_PATH).document(firebaseUser.getUid());
    }
}
