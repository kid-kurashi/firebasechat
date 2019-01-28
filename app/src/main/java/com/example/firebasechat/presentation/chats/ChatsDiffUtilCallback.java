package com.example.firebasechat.presentation.chats;

import android.support.v7.util.DiffUtil;

import java.util.ArrayList;
import java.util.Map;

public class ChatsDiffUtilCallback extends DiffUtil.Callback {

    private final ArrayList<Map<String, Object>> oldList;
    private final ArrayList<Map<String, Object>> newList;

    public ChatsDiffUtilCallback(ArrayList<Map<String, Object>> oldList, ArrayList<Map<String, Object>> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Map<String, Object> oldChat = oldList.get(oldItemPosition);
        Map<String, Object> newChat = newList.get(newItemPosition);
        return oldChat.equals(newChat);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Map<String, Object> oldChat = oldList.get(oldItemPosition);
        Map<String, Object> newChat = newList.get(newItemPosition);
        return oldChat.equals(newChat);
    }
}
