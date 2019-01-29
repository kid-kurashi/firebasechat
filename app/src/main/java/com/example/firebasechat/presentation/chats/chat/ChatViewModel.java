package com.example.firebasechat.presentation.chats.chat;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;

import com.example.firebasechat.data.FirebaseRepository;
import com.example.firebasechat.presentation.base.BaseViewModel;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChatViewModel extends BaseViewModel {

    private Disposable getMessagesDisposable;

    public MutableLiveData<ArrayList<HashMap<String, String>>> messages = new MutableLiveData<>();
    public MutableLiveData<Boolean> isProgress = new MutableLiveData<>();
    private String chatId;

    public ChatViewModel(FirebaseRepository firebaseRepository) {
        super(firebaseRepository);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void loadMessages() {
        isProgress.postValue(true);
        getMessagesDisposable = firebaseRepository
                .getMessages(chatId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(this::onReceivedMessages, this::onErrorReceived);
    }

    private void onReceivedMessages(ArrayList<HashMap<String, String>> receivedMessages) {
        isProgress.postValue(false);
        messages.postValue(receivedMessages);
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getOwner() {
        return firebaseRepository.getUserEmail();
    }

    public void sendMessage(String message) {
        if (message != null && !message.isEmpty()) {
            firebaseRepository.sendMessage(message, chatId);
        }
    }
}
