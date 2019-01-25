package com.example.firebasechat.presentation.contacts;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.firebasechat.R;
import com.example.firebasechat.databinding.ActivityContactsBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ContactsActivity extends AppCompatActivity implements View.OnClickListener {

    private ContactsViewModel viewModel;
    private ActivityContactsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewModel = ViewModelProviders.of(this).get(ContactsViewModel.class);
        getLifecycle().addObserver(viewModel);
        viewModel.isProgress.observe(this, aBoolean -> binding.setModel(viewModel));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab: {
                viewModel.addContact();
            }
            break;
            default:
                break;
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
    protected void onStart() {
        super.onStart();
    }
}
