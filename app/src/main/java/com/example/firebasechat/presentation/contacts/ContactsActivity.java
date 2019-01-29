package com.example.firebasechat.presentation.contacts;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;

import com.example.firebasechat.App;
import com.example.firebasechat.R;
import com.example.firebasechat.databinding.ActivityContactsBinding;
import com.example.firebasechat.presentation.base.ModelFactory;

import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private ContactsViewModel viewModel;
    private ActivityContactsBinding binding;
    private AddContactDialog addContactDialog;
    private ContactsAdapter adapter;
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = ((App) getApplication());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts);
        viewModel = ViewModelProviders.of(this, new ModelFactory(app)).get(ContactsViewModel.class);
        getLifecycle().addObserver(viewModel);

        addContactDialog = new AddContactDialog(this);
        addContactDialog.setButtonCallback(text -> viewModel.addContact(text));
        binding.fabAddContact.setOnClickListener(v -> showDialog());

        viewModel.isProgress.observe(this, progress -> binding.setModel(viewModel));
        viewModel.dismissDialog.observe(this, dismiss -> {
            if (dismiss) closeDialog();
        });

        binding.contactsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.contactsRecyclerView.setLayoutManager(layoutManager);
        adapter = new ContactsAdapter();
        binding.contactsRecyclerView.setAdapter(adapter);

        viewModel.listContacts.observe(this, this::updateContacts);
    }

    private void updateContacts(List<String> contacts) {

        viewModel.writeContacts(contacts);

        ContactsDiffUtilCallback contactsDiffUtilCallback =
                new ContactsDiffUtilCallback(adapter.getItems(), contacts);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(contactsDiffUtilCallback);

        adapter.setItems(contacts);
        diffResult.dispatchUpdatesTo(adapter);
    }

    private void showDialog() {
        if (!addContactDialog.isShowing()) {
            addContactDialog.show();
        }
    }

    private void closeDialog() {
        if (addContactDialog.isShowing()) {
            addContactDialog.dismiss();
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
