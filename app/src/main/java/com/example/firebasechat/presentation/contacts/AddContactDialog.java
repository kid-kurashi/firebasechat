package com.example.firebasechat.presentation.contacts;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import com.example.firebasechat.R;
import com.example.firebasechat.firestore_constants.Users;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class AddContactDialog extends AlertDialog {

    private TextInputLayout inputLayout;
    private TextInputEditText inputEditText;
    private Button addButton;

    private boolean progress = false;
    private BehaviorSubject<String> inputBehavior = BehaviorSubject.create();
    private Disposable behaviorDisposable;
    private FirebaseFirestore database;
    private AddButtonCallback callback;

    protected AddContactDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStart() {
        super.onStart();
        inputEditText.setText("");
        isEmptyField();
        behaviorDisposable = inputBehavior
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribe(text -> {
                    if (!text.isEmpty()) {
                        database.collection(Users.COLLECTION_PATH)
                                .whereEqualTo(Users.FIELD_LOGIN, text)
                                .get().addOnSuccessListener(snapshots -> {
                            if (snapshots != null && !snapshots.isEmpty())
                                for (DocumentSnapshot snapshot : snapshots) {
                                    if (snapshot.get(Users.FIELD_LOGIN) != null
                                            && Objects.equals(snapshot.get(Users.FIELD_LOGIN), text))
                                        userFound();
                                }
                            else
                                userNotFound();
                        });
                    } else
                        isEmptyField();
                });

        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    search();
                }
                inputBehavior.onNext(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onStop() {
        if (behaviorDisposable != null && !behaviorDisposable.isDisposed())
            behaviorDisposable.dispose();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact_layout);

        database = FirebaseFirestore.getInstance();

        addButton = findViewById(R.id.add_contact_button);
        inputLayout = findViewById(R.id.add_contact_layout);
        inputEditText = findViewById(R.id.add_contact_edit_text);

        addButton.setEnabled(false);
        addButton.setOnClickListener(v -> {
            if (inputEditText.getText() != null) {
                callback.onClick(inputEditText.getText().toString());
            }
        });
    }

    private void search() {
        AndroidSchedulers.mainThread().scheduleDirect(() -> {
            isEmptyField();
            addButton.setEnabled(false);
        });
    }

    private void isEmptyField() {
        AndroidSchedulers.mainThread().scheduleDirect(() -> {
            addButton.setEnabled(false);
            inputLayout.setErrorEnabled(false);
        });
    }

    private void userNotFound() {
        AndroidSchedulers.mainThread().scheduleDirect(() -> {
            addButton.setEnabled(false);
            inputLayout.setErrorTextAppearance(R.style.ErrorTextRed);
            inputLayout.setError(getContext().getString(R.string.user_not_found));
        });
    }

    private void userFound() {
        AndroidSchedulers.mainThread().scheduleDirect(() -> {
            addButton.setEnabled(true);
            inputLayout.setErrorTextAppearance(R.style.ErrorTextGreen);
            inputLayout.setError(getContext().getString(R.string.user_found));
        });
    }

    public void setButtonCallback(AddButtonCallback callback) {
        this.callback = callback;
    }

    public interface AddButtonCallback {
        void onClick(String text);
    }
}
