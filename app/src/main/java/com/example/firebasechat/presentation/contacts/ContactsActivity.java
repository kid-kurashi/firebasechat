package com.example.firebasechat.presentation.contacts;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.firebasechat.R;
import com.example.firebasechat.databinding.ActivityContactsBinding;

public class ContactsActivity extends AppCompatActivity implements View.OnClickListener {

    private ContactsViewModel viewModel;
    private ActivityContactsBinding binding;
    private AddContactDialog addContactDialog;
    private ContactsAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts);
        viewModel = ViewModelProviders.of(this).get(ContactsViewModel.class);
        getLifecycle().addObserver(viewModel);

        viewModel.isProgress.observe(this, aBoolean -> binding.setModel(viewModel));

        recyclerView = findViewById(R.id.contacts_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ContactsAdapter();
        recyclerView.setAdapter(adapter);

        viewModel.listContacts.observe(this, contacts -> {
            ContactsDiffUtilCallback productDiffUtilCallback =
                    new ContactsDiffUtilCallback(adapter.getItems(), contacts);
            DiffUtil.DiffResult productDiffResult = DiffUtil.calculateDiff(productDiffUtilCallback);

            adapter.setItems(contacts);
            productDiffResult.dispatchUpdatesTo(adapter);
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
}
