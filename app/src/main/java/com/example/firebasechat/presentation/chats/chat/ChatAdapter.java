package com.example.firebasechat.presentation.chats.chat;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.firebasechat.R;
import com.example.firebasechat.firestore_constants.Chats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private ArrayList<HashMap<String, String>> items = new ArrayList<>();
    private ChatAdapter.OnClickCallback onClickCallback;
    private String owner;


    public ChatAdapter(String owner) {
        this.owner = owner;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item, viewGroup, false);
        return new ChatAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(items.get(i));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnClickCallback {
        void onClick(String chatId);
    }

    void setItems(ArrayList<HashMap<String, String>> items) {
        this.items = items;
    }

    public ArrayList<HashMap<String, String>> getItems() {
        return items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardView;
        private final TextView messageText;
        private final TextView messageTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.chat_card_view);
            messageText = itemView.findViewById(R.id.chat_message_text);
            messageTime = itemView.findViewById(R.id.chat_message_time);
        }

        public void bind(HashMap<String, String> messageMap) {
            Log.e("MAP", messageMap.toString());
            messageText.setText(messageMap.get(Chats.FIELD_MESSAGE_TEXT));
            messageTime.setText(messageMap.get(Chats.FIELD_MESSAGE_TIME));
            if (messageMap.get(Chats.FIELD_MESSAGE_OWNER).equals(owner)) {
                int color = super.itemView.getContext().getResources().getColor(R.color.colorPrimary);
                cardView.setCardBackgroundColor(color);
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                params.setMarginStart(300);
                cardView.setLayoutParams(params);
            } else {
                int color = super.itemView.getContext().getResources().getColor(R.color.material_green);
                cardView.setCardBackgroundColor(color);
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                params.setMarginEnd(300);
                cardView.setLayoutParams(params);
            }
        }
    }
}