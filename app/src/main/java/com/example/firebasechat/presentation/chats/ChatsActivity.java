package com.example.firebasechat.presentation.chats;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.firebasechat.R;
import com.example.firebasechat.databinding.ActivityChatsBinding;
import com.example.firebasechat.presentation.contacts.ContactsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class ChatsActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityChatsBinding binding;
    private ChatsViewModel viewModel;

    private Drawer drawer;
    public static final int CONTACTS_ID = 100;
    private PrimaryDrawerItem item1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chats);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewModel = ViewModelProviders.of(this).get(ChatsViewModel.class);
        getLifecycle().addObserver(viewModel);


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

    private boolean itemClicked(IDrawerItem drawerItem) {
        drawer.closeDrawer();
        switch ((int)drawerItem.getIdentifier()){
            case CONTACTS_ID: { startActivity(new Intent(this, ContactsActivity.class));} return true;
            default: return false;
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
        switch (view.getId()){
            case R.id.fab_new_chat: { viewModel.createNewChat();}break;
            default: break;
        }
    }
}
