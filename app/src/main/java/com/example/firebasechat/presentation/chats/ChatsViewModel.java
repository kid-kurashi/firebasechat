package com.example.firebasechat.presentation.chats;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;

import com.example.firebasechat.data.pojo.Chat;
import com.example.firebasechat.firestore_constants.Chats;
import com.example.firebasechat.presentation.base.BaseViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsViewModel extends BaseViewModel {

    public MutableLiveData<Boolean> isProgress = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Map<String, Object>>> chats = new MutableLiveData<>();
    public MutableLiveData<String> chatCreated = new MutableLiveData<>();

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void loadChats() {
        isProgress.postValue(true);
        database.collection(Chats.COLLECTION_PATH)
                .whereArrayContains(Chats.FIELD_MEMBERS, firebaseUser.getEmail())
                .get().addOnSuccessListener(snapshots -> {
            if (snapshots != null && !snapshots.isEmpty()) {
                ArrayList<Map<String, Object>> list = new ArrayList<>();
                for (DocumentSnapshot snapshot : snapshots) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put(Chats.FIELD_MEMBERS, snapshot.get(Chats.FIELD_MEMBERS));
                    map.put(Chats.FIELD_MESSAGES, snapshot.get(Chats.FIELD_MESSAGES));
                    map.put(Chats.FIELD_CHAT_ID, snapshot.get(Chats.FIELD_CHAT_ID));
                    list.add(map);
                }
                chats.postValue(list);
                isProgress.postValue(false);
            } else {
                isProgress.postValue(false);
            }
        });
    }

    public void createNewChat(List<String> chatMembers) {
        DocumentReference chatDocument = database.collection(Chats.COLLECTION_PATH).document();
        String chatId = chatDocument.getId();
        chatDocument.set(new Chat(chatMembers, new ArrayList<>(), chatId)).addOnCompleteListener(task -> chatCreated.postValue(chatId));
    }

}
