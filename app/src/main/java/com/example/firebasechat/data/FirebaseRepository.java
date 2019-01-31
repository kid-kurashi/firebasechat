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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;

public class FirebaseRepository {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore database;
    private String deviceToken;

    private ReplaySubject<DocumentSnapshot> currentUserObservableDocument = ReplaySubject.create();
    private ReplaySubject<List<Map<String, Object>>> userChatsObservable = ReplaySubject.create();
    private ReplaySubject<String> createNewChatObservable = ReplaySubject.create();
    private ReplaySubject<Boolean> connectToFirestoreObservable = ReplaySubject.create();
    private ReplaySubject<List<String>> getContactsObservable = ReplaySubject.create();
    private ReplaySubject<Boolean> addContactObservable = ReplaySubject.create();
    private ReplaySubject<Boolean> findUserByEmailObservable = ReplaySubject.create();
    private ReplaySubject<ArrayList<HashMap<String, String>>> chatMessagesObservable = ReplaySubject.create();

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

    public Observable<List<Map<String, Object>>> getUserChats() {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null && firebaseUser.getEmail() != null) {
            String email = firebaseUser.getEmail();
            database.collection(Chats.COLLECTION_PATH)
                    .whereArrayContains(Chats.FIELD_MEMBERS, email)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot != null && !snapshot.getDocuments().isEmpty()) {
                            ArrayList<Map<String, Object>> list = new ArrayList<>();
                            for (DocumentSnapshot item : snapshot.getDocuments()) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put(Chats.FIELD_MEMBERS, item.get(Chats.FIELD_MEMBERS));
                                map.put(Chats.FIELD_MESSAGES, item.get(Chats.FIELD_MESSAGES));
                                map.put(Chats.FIELD_CHAT_ID, item.get(Chats.FIELD_CHAT_ID));
                                list.add(map);
                            }
                            userChatsObservable.onNext(list);
                        } else
                            userChatsObservable.onNext(new ArrayList<>());
                    })
                    .addOnFailureListener(e -> userChatsObservable.onError(e));
        } else {
            userChatsObservable.onError(new Throwable("firebaseUser.getEmail() == null"));
        }
        return userChatsObservable;
    }

    public Observable<String> createNewChat(List<String> chatMembers) {
        chatMembers.add(firebaseUser.getEmail());
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
                try {
                    List<String> contacts = (List<String>) documentSnapshot.get(Users.FIELD_CONTACTS);
                    if (contacts != null) {
                        getContactsObservable.onNext(contacts);
                    } else {
                        getContactsObservable.onNext(new ArrayList<>());
                    }
                } catch (Exception e1) {
                    getContactsObservable.onError(e1);
                }
            }
        });
        return getContactsObservable;
    }

    public Observable<Boolean> addContact(String contact) {
        HashMap<String, Object> user = new HashMap<>();
        getCurrentUserReference()
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null) {
                        try {
                            ArrayList<String> contacts = ((ArrayList<String>) snapshot.get(Users.FIELD_CONTACTS));
                            if (contacts != null) {
                                contacts.add(contact);
                                user.put(Users.FIELD_CONTACTS, contacts);
                                getCurrentUserReference().update(user);
                            }
                        } catch (Exception e) {
                            addContactObservable.onError(e);
                        }
                    }
                }).addOnFailureListener(e -> addContactObservable.onError(e));
        return addContactObservable;
    }

    public Observable<Boolean> findUserByEmail(String text) {
        if (text != null && !text.isEmpty())
            database.collection(Users.COLLECTION_PATH)
                    .whereEqualTo(Users.FIELD_LOGIN, text)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot != null && !snapshot.getDocuments().isEmpty()) {
                            for (DocumentSnapshot snap : snapshot.getDocuments()) {
                                if (snap.get(Users.FIELD_LOGIN) != null && Objects.equals(snap.get(Users.FIELD_LOGIN), text))
                                    findUserByEmailObservable.onNext(true);
                            }
                        } else {
                            findUserByEmailObservable.onNext(false);
                        }
                    })
                    .addOnFailureListener(e -> findUserByEmailObservable.onError(e));
        return findUserByEmailObservable;
    }

    public Observable<ArrayList<HashMap<String, String>>> getMessages(String chatId) {
        database.collection(Chats.COLLECTION_PATH)
                .document(chatId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null)
                        chatMessagesObservable.onError(e);
                    else {
                        try {
                            ArrayList<HashMap<String, Object>> receivedMessages =
                                    (ArrayList<HashMap<String, Object>>) documentSnapshot.get(Chats.FIELD_MESSAGES);
                            if (receivedMessages != null) {
                                ArrayList<HashMap<String, String>> messages = new ArrayList<>();
                                for (int i = 0; i < receivedMessages.size(); i++) {
                                    LinkedHashMap<String, String> newObject = new LinkedHashMap<>();
                                    newObject.put(Chats.FIELD_MESSAGE_TEXT, (String) receivedMessages.get(i).get(Chats.FIELD_MESSAGE_TEXT));
                                    newObject.put(Chats.FIELD_MESSAGE_TIME, (String) receivedMessages.get(i).get(Chats.FIELD_MESSAGE_TIME));
                                    newObject.put(Chats.FIELD_MESSAGE_OWNER, (String) receivedMessages.get(i).get(Chats.FIELD_MESSAGE_OWNER));
                                    messages.add(newObject);
                                }
                                chatMessagesObservable.onNext(messages);
                            }
                        } catch (Exception e1) {
                            chatMessagesObservable.onError(e1);
                        }
                    }
                });
        return chatMessagesObservable;
    }

    public String getUserEmail() {
        return firebaseUser.getEmail();
    }

    public void sendMessage(String text, String chatId) {
        database.collection(Chats.COLLECTION_PATH)
                .document(chatId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        ArrayList<HashMap<String, String>> messages =
                                (ArrayList<HashMap<String, String>>) documentSnapshot.get(Chats.FIELD_MESSAGES);
                        HashMap<String, String> message = new HashMap<>();
                        message.put(Chats.FIELD_MESSAGE_TEXT, text);
                        message.put(Chats.FIELD_MESSAGE_OWNER, firebaseUser.getEmail());
                        message.put(Chats.FIELD_MESSAGE_TIME, String.valueOf(new Date().getTime()));

                        messages.add(message);
                        database.collection(Chats.COLLECTION_PATH)
                                .document(chatId)
                                .update(Chats.FIELD_MESSAGES, messages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
