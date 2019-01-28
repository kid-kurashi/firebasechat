package com.example.firebasechat.presentation.chats.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.firebasechat.R;

public class ChatActivity extends AppCompatActivity {

    private BroadcastReceiver bReceiver;
    public static final String RECEIVE_MESSAGE = "com.example.firebasechat.RECEIVE_MESSAGE";
    private LocalBroadcastManager bManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if ((intent != null)
                        && (intent.getAction() != null)
                        && intent.getAction().equals(RECEIVE_MESSAGE)) {
                    onReceivedNewIntent(intent);
                }
            }
        };

        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_MESSAGE);
        bManager.registerReceiver(bReceiver, intentFilter);
    }

    private void onReceivedNewIntent(Intent intent) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
