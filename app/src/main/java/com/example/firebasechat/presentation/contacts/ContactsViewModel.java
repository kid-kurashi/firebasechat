package com.example.firebasechat.presentation.contacts;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;

import com.example.firebasechat.data.SharedPreferecesManager;
import com.example.firebasechat.data.pojo.User;
import com.example.firebasechat.firestore_constants.Users;
import com.example.firebasechat.presentation.base.BaseViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ContactsViewModel extends BaseViewModel {

    public MutableLiveData<Boolean> isProgress = new MutableLiveData<>();
    public MutableLiveData<List<String>> listContacts = new MutableLiveData<>();

    private String pushToken;
    private final SharedPreferecesManager sharedPreferecesManager;

    public ContactsViewModel(SharedPreferecesManager sharedPreferecesManager) {
        this.sharedPreferecesManager = sharedPreferecesManager;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void connectToFirestore() {
        isProgress.postValue(true);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                pushToken = task.getResult().getToken();
                DocumentReference userReference = getCurrentUserReference();
                userReference.addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
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

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void getContacts() {
        isProgress.postValue(true);
        getCurrentUserReference().addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                e.printStackTrace();
                return;
            }
            if (documentSnapshot != null) {
                HashSet<String> contactsSet = new HashSet<>();

                List<String> contactsList = (List<String>) documentSnapshot.get(Users.FIELD_CONTACTS);
                for (String contact : contactsList) {
                    contactsSet.add(contact);
                }
                sharedPreferecesManager.writeContacts(contactsSet);
                listContacts.postValue(contactsList);
                isProgress.postValue(false);
            }
        });
    }

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

    public void addContact(String contact, AddContactDialog addContactDialog) {
        HashMap<String, Object> user = new HashMap<>();
        getCurrentUserReference().get().addOnCompleteListener(task -> {
            DocumentSnapshot snapshot = task.getResult();
            if (snapshot != null) {
                ArrayList<String> contacts = ((ArrayList<String>) snapshot.get(Users.FIELD_CONTACTS));
                if (contacts != null) {
                    contacts.add(contact);
                    user.put(Users.FIELD_CONTACTS, contacts);
                    getCurrentUserReference().update(user);
                    addContactDialog.dismiss();
                }
            }
        });
    }
}
