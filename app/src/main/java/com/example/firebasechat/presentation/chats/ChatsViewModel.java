package com.example.firebasechat.presentation.chats;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;

import com.example.firebasechat.data.pojo.Chat;
import com.example.firebasechat.data.pojo.Message;
import com.example.firebasechat.firestore_constants.Chats;
import com.example.firebasechat.presentation.base.BaseViewModel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatsViewModel extends BaseViewModel {

    public MutableLiveData<Boolean> isProgress = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Chat>> chats = new MutableLiveData<>();


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void loadChats() {
        isProgress.postValue(true);
        database.collection(Chats.COLLECTION_PATH)
                .whereArrayContains(Chats.FIELD_MEMBERS, firebaseUser.getEmail())
                .get().addOnSuccessListener(snapshots -> {
            if (snapshots != null && !snapshots.isEmpty()) {
                ArrayList<Chat> list = new ArrayList<>();
                for (DocumentSnapshot snapshot : snapshots) {
                    list.add(new Chat(
                            (List<String>) snapshot.get(Chats.FIELD_MEMBERS),
                            (List<Message>) snapshot.get(Chats.FIELD_MESSAGES),
                            (String) snapshot.get("chatId")));
                }
                chats.postValue(list);
                isProgress.postValue(false);
            } else {
                isProgress.postValue(false);
            }
        });
    }

    public void createNewChat() {

    }

}
