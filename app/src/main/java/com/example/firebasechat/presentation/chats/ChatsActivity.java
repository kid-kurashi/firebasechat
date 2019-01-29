package com.example.firebasechat.presentation.chats;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.firebasechat.App;
import com.example.firebasechat.R;
import com.example.firebasechat.databinding.ActivityChatsBinding;
import com.example.firebasechat.firestore_constants.Chats;
import com.example.firebasechat.presentation.base.ModelFactory;
import com.example.firebasechat.presentation.chats.chat.ChatActivity;
import com.example.firebasechat.presentation.contacts.ContactsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ActivityChatsBinding binding;
    private ChatsViewModel viewModel;

    private Drawer drawer;
    public static final int CONTACTS_ID = 100;
    private PrimaryDrawerItem item1;
    private ChatsAdapter adapter;
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (App) getApplication();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chats);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewModel = ViewModelProviders.of(this, new ModelFactory(app)).get(ChatsViewModel.class);
        getLifecycle().addObserver(viewModel);

        viewModel.isProgress.observe(this, progress -> {
            binding.setModel(viewModel);
            binding.chatsSwipeRefresh.setRefreshing(progress);
        });

        binding.chatsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.chatsRecyclerView.setLayoutManager(layoutManager);
        adapter = new ChatsAdapter();
        adapter.setOnClickCallback(this::openChatScreen);
        binding.chatsRecyclerView.setAdapter(adapter);
        viewModel.chats.observe(this, chats -> {
            ChatsDiffUtilCallback chatsDiffUtilCallback =
                    new ChatsDiffUtilCallback(adapter.getItems(), chats);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(chatsDiffUtilCallback);
            adapter.setItems(chats);
            diffResult.dispatchUpdatesTo(adapter);
        });

        viewModel.chatCreated.observe(this, chatId -> new Handler().post(() -> openChatScreen(chatId)));

        binding.chatsSwipeRefresh.setOnRefreshListener(this);
        binding.chatsSwipeRefresh.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        item1 = new PrimaryDrawerItem()
                .withIdentifier(CONTACTS_ID)
                .withName(getString(R.string.title_contacts))
                .withIcon(R.drawable.contacts);

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withToolbar(toolbar)
                .withSliderBackgroundColorRes(R.color.md_white_1000)
                .addDrawerItems(item1)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> itemClicked(drawerItem))
                .withCloseOnClick(true)
                .build();
    }

    private void openChatScreen(String chatId) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Chats.FIELD_CHAT_ID, chatId);
        startActivity(intent);
    }

    private boolean itemClicked(IDrawerItem drawerItem) {
        drawer.closeDrawer();
        switch ((int) drawerItem.getIdentifier()) {
            case CONTACTS_ID: {
                startActivity(new Intent(this, ContactsActivity.class));
            }
            return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_signout) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_new_chat: {
                pickContacts();
            }
            break;
            default:
                break;
        }
    }

    private void pickContacts() {
        List<String> mSelectedItems = new ArrayList<>();
        CharSequence[] contacts = new CharSequence[app.getSharedPreferecesManager().readContacts().size()];

        int i = 0;
        for (String contact : app.getSharedPreferecesManager().readContacts()) {
            contacts[i] = contact;
            i++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pick_contacts)
                .setMultiChoiceItems(contacts, null,
                        (dialog, which, isChecked) -> {
                            if (isChecked) {
                                mSelectedItems.add(contacts[which].toString());
                            } else if (mSelectedItems.contains(contacts[which].toString())) {
                                mSelectedItems.remove(which);
                            }
                        })
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    viewModel.createNewChat(mSelectedItems);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());

        builder.create().show();
    }

    @Override
    public void onRefresh() {
        viewModel.loadChats();
    }
}
