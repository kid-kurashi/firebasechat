package com.example.firebasechat.presentation.contacts;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.example.firebasechat.App;
import com.example.firebasechat.R;
import com.example.firebasechat.databinding.ActivityContactsBinding;
import com.example.firebasechat.presentation.base.ModelFactory;

public class ContactsActivity extends AppCompatActivity implements View.OnClickListener {

    private ContactsViewModel viewModel;
    private ActivityContactsBinding binding;
    private AddContactDialog addContactDialog;
    private ContactsAdapter adapter;
    private RecyclerView recyclerView;
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = ((App) getApplication());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts);
        ContactsViewModel viewModel = ViewModelProviders.of(this, new ModelFactory(app.getSharedPreferecesManager()))
                .get(ContactsViewModel.class);
        getLifecycle().addObserver(viewModel);

        viewModel.isProgress.observe(this, progress -> binding.setModel(viewModel));

        recyclerView = findViewById(R.id.contacts_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ContactsAdapter();
        recyclerView.setAdapter(adapter);

        viewModel.listContacts.observe(this, contacts -> {
            ContactsDiffUtilCallback contactsDiffUtilCallback =
                    new ContactsDiffUtilCallback(adapter.getItems(), contacts);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(contactsDiffUtilCallback);

            adapter.setItems(contacts);
            diffResult.dispatchUpdatesTo(adapter);
        });

        addContactDialog = new AddContactDialog(this);
        addContactDialog.setButtonCallback(text -> viewModel.addContact(text, addContactDialog));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_contact: {
                showDialog();
            }
            break;
            default:
                break;
        }
    }

    private void showDialog() {
        if (!addContactDialog.isShowing()) {
            addContactDialog.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
