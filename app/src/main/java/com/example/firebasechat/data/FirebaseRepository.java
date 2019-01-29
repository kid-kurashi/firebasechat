package com.example.firebasechat.data;

import com.example.firebasechat.data.pojo.Chat;
import com.example.firebasechat.data.pojo.User;
import com.example.firebasechat.firestore_constants.Chats;
import com.example.firebasechat.firestore_constants.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;

public class FirebaseRepository {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore database;
    private String deviceToken;

    private ReplaySubject<DocumentSnapshot> currentUserObservableDocument = ReplaySubject.create();
    private ReplaySubject<List<DocumentSnapshot>> userChatsObservable = ReplaySubject.create();
    private ReplaySubject<String> createNewChatObservable = ReplaySubject.create();
    private ReplaySubject<Boolean> connectToFirestoreObservable = ReplaySubject.create();
    private ReplaySubject<List<String>> getContactsObservable = ReplaySubject.create();
    private ReplaySubject<Boolean> addContactObservable = ReplaySubject.create();
    private ReplaySubject<Boolean> findUserByEmailObservable = ReplaySubject.create();

    public FirebaseRepository(FirebaseAuth firebaseAuth, FirebaseFirestore database) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseUser = firebaseAuth.getCurrentUser();
        this.database = database;
    }

    private DocumentReference getCurrentUserReference() {
        return database.collection(Users.COLLECTION_PATH)
                .document(firebaseUser.getUid());
    }

    public Observable<DocumentSnapshot> getUser() {
        database.collection(Users.COLLECTION_PATH)
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot ->
                        currentUserObservableDocument.onNext(documentSnapshot))
                .addOnFailureListener(e -> currentUserObservableDocument.onError(e));
        return currentUserObservableDocument;
    }

    public Observable<List<DocumentSnapshot>> getUserChats() {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null && firebaseUser.getEmail() != null) {
            String email = firebaseUser.getEmail();
            database.collection(Chats.COLLECTION_PATH)
                    .whereArrayContains(Chats.FIELD_MEMBERS, email)
                    .get()
                    .addOnSuccessListener(snapshots -> {
                        if (snapshots != null && !snapshots.isEmpty())
                            userChatsObservable.onNext(snapshots.getDocuments());
                    })
                    .addOnFailureListener(e -> userChatsObservable.onError(e));
        } else {
            userChatsObservable.onError(new Throwable("firebaseUser.getEmail() == null"));
        }
        return userChatsObservable;
    }

    public Observable<String> createNewChat(List<String> chatMembers) {
        DocumentReference chatDocument = database.collection(Chats.COLLECTION_PATH).document();
        String chatId = chatDocument.getId();
        chatDocument
                .set(new Chat(chatMembers, new ArrayList<>(), chatId))
                .addOnCompleteListener(task -> createNewChatObservable.onNext(chatId))
                .addOnFailureListener(e -> createNewChatObservable.onError(e));
        return createNewChatObservable;
    }

    public Observable<Boolean> connectToFirestore() {
        FirebaseInstanceId
                .getInstance()
                .getInstanceId()
                .addOnSuccessListener(instanceIdResult -> {
                    deviceToken = instanceIdResult.getToken();
                    getCurrentUserReference()
                            .addSnapshotListener(this::updateDeviceTokenOrCreateNewUser);
                })
                .addOnFailureListener(e -> connectToFirestoreObservable.onError(e));
        return connectToFirestoreObservable;
    }

    private void updateDeviceTokenOrCreateNewUser(DocumentSnapshot snapshot, Throwable e) {
        if (e != null) {
            connectToFirestoreObservable.onError(e);
        } else {
            if (snapshot != null && snapshot.get(Users.FIELD_DEVICE_TOKEN) != null) {
                HashMap<String, Object> updateTokenMap = new HashMap<>();
                updateTokenMap.put(Users.FIELD_DEVICE_TOKEN, deviceToken);
                getCurrentUserReference()
                        .update(updateTokenMap)
                        .addOnSuccessListener(aVoid -> connectToFirestoreObservable.onNext(true))
                        .addOnFailureListener(exception -> connectToFirestoreObservable.onError(exception));

            } else {
                User user = new User(
                        firebaseUser.getDisplayName(),
                        firebaseUser.getUid(),
                        deviceToken, firebaseUser.getEmail());
                getCurrentUserReference()
                        .set(user)
                        .addOnSuccessListener(aVoid -> connectToFirestoreObservable.onNext(true))
                        .addOnFailureListener(exception -> connectToFirestoreObservable.onError(exception));
            }
        }
    }

    public Observable<List<String>> getUserContacts() {
        getCurrentUserReference().addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                getContactsObservable.onError(e);
            } else {
                List<String> contacts = (List<String>) documentSnapshot.get(Users.FIELD_CONTACTS);
                if(contacts != null) {
                    getContactsObservable.onNext(contacts);
                }else{
                    getContactsObservable.onNext(new ArrayList<>());
                }
            }
        });
        return getContactsObservable;
    }

    public Observable<Boolean> addContact(String contact) {
        HashMap<String, Object> user = new HashMap<>();
        getCurrentUserReference().get().addOnCompleteListener(task -> {
            DocumentSnapshot snapshot = task.getResult();
            if (snapshot != null) {
                ArrayList<String> contacts = ((ArrayList<String>) snapshot.get(Users.FIELD_CONTACTS));
                if (contacts != null) {
                    contacts.add(contact);
                    user.put(Users.FIELD_CONTACTS, contacts);
                    getCurrentUserReference().update(user);
                }
            }
        });
        return addContactObservable;
    }

    public Observable<Boolean> findUserByEmail(String text) {
        database.collection(Users.COLLECTION_PATH)
                .whereEqualTo(Users.FIELD_LOGIN, text)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentSnapshot snap : snapshot.getDocuments()) {
                            if (snap.get(Users.FIELD_LOGIN) != null
                                    && Objects.equals(snap.get(Users.FIELD_LOGIN), text))
                                findUserByEmailObservable.onNext(true);
                        }
                    }
                    findUserByEmailObservable.onNext(false);
                })
                .addOnFailureListener(e -> findUserByEmailObservable.onError(e));
        return findUserByEmailObservable;
    }
}
