package com.example.firebasechat.presentation.contacts;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;

import com.example.firebasechat.data.pojo.User;
import com.example.firebasechat.firestore_constants.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactsViewModel extends ViewModel implements LifecycleObserver {

    public MutableLiveData<Boolean> isProgress = new MutableLiveData<>();
    public MutableLiveData<ArrayList<String>> listContacts;

    private String pushToken;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore database;

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void initServices() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseFirestore.getInstance();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void connectToFirestore() {
        isProgress.postValue(true);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                pushToken = task.getResult().getToken();
                DocumentReference userReference = getCurrentUserReference();
                userReference.addSnapshotListener((documentSnapshot, e) -> {
                    if(e != null){
                        e.printStackTrace();
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.get(Users.FIELD_DEVICE_TOKEN) != null) {
                        updateDeviceToken(userReference);
                    } else {
                        createNewUser();
                    }
                    isProgress.postValue(false);
                });
            }
        });
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    public void getContacts() {
//        isProgress.postValue(true);
//        getCurrentUserReference().addSnapshotListener((document, e) -> {
//            if(e != null){
//                e.printStackTrace();
//                return;
//            }
//            if(document != null) {
//                listContacts.postValue((ArrayList<String>)document.getData().get(Users.FIELD_CONTACTS));
//                isProgress.postValue(false);
//            }
//        });
//    }

    private void createNewUser() {
        User user = new User(
                firebaseUser.getDisplayName(),
                firebaseUser.getUid(),
                pushToken, firebaseUser.getEmail());
        getCurrentUserReference().set(user);
    }

    private void updateDeviceToken(DocumentReference userReference) {
        HashMap<String, Object> updateTokenMap = new HashMap<>();
        updateTokenMap.put(Users.FIELD_DEVICE_TOKEN, pushToken);
        userReference.update(updateTokenMap);
    }

    private DocumentReference getCurrentUserReference() {
        return database.collection(Users.COLLECTION_PATH).document(firebaseUser.getUid());
    }

    public void addContact() {

    }
}
