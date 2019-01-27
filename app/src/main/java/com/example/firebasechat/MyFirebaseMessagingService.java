package com.example.firebasechat;

import android.content.Intent;
import android.util.Log;

import com.example.firebasechat.presentation.chats.chat.ChatActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "@@@FCM@@@";
    private Intent intent;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            ((App)getApplication()).getNotificationHelper().remoteMessageReceived(remoteMessage);
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    private void handleNow(Map<String, String> data) {
        intent = new Intent(ChatActivity.RECEIVE_MESSAGE);
        for (String key : data.keySet()) {
            intent.putExtra(key, data.get(key));
        }
        sendBroadcast(intent);
    }

    @Override
    public void onMessageSent(String s) {
        sendBroadcast(intent);
        super.onMessageSent(s);
    }

}
