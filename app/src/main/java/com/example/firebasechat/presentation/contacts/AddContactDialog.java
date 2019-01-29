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
import com.example.firebasechat.data.FirebaseRepository;

import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class AddContactDialog extends AlertDialog {

    private FirebaseRepository firebaseRepository;
    private TextInputLayout inputLayout;
    private TextInputEditText inputEditText;
    private Button addButton;

    private BehaviorSubject<String> inputBehavior = BehaviorSubject.create();
    private Disposable behaviorDisposable;
    private AddButtonCallback callback;

    protected AddContactDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact_layout);

        addButton = findViewById(R.id.add_contact_button);
        inputLayout = findViewById(R.id.add_contact_layout);
        inputEditText = findViewById(R.id.add_contact_edit_text);

        addButton.setEnabled(false);
        addButton.setOnClickListener(v -> {
            if (inputEditText.getText() != null) {
                callback.onClick(inputEditText.getText().toString());
            }
        });

        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search();
                if (s != null && !s.toString().isEmpty()) {
                    inputBehavior.onNext(s.toString());
                } else
                    isEmptyField();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        inputEditText.setText("");
        inputEditText.setSelection(0,0);
        behaviorDisposable = inputBehavior
                .debounce(1000, TimeUnit.MILLISECONDS)
                .map(text -> firebaseRepository
                        .findUserByEmail(text)
                        .debounce(500, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::isUserFound, this::onErrorReceived)
                ).subscribe();
    }

    @Override
    protected void onStop() {
        if (behaviorDisposable != null && !behaviorDisposable.isDisposed())
            behaviorDisposable.dispose();
        super.onStop();
    }

    private void onErrorReceived(Throwable throwable) {
        throwable.printStackTrace();
    }

    private void isUserFound(Boolean isFound) {
        if (inputEditText.getText() != null && !inputEditText.getText().toString().isEmpty())
            if (isFound)
                userFound();
            else
                userNotFound();
    }

    private void search() {
        isEmptyField();
        addButton.setEnabled(false);
    }

    private void isEmptyField() {
        addButton.setEnabled(false);
        inputLayout.setErrorEnabled(false);
    }

    private void userNotFound() {
        addButton.setEnabled(false);
        inputLayout.setErrorTextAppearance(R.style.ErrorTextRed);
        inputLayout.setError(getContext().getString(R.string.user_not_found));
    }

    private void userFound() {
        addButton.setEnabled(true);
        inputLayout.setErrorTextAppearance(R.style.ErrorTextGreen);
        inputLayout.setError(getContext().getString(R.string.user_found));
    }

    public void setButtonCallback(AddButtonCallback callback) {
        this.callback = callback;
    }

    public void setRepository(FirebaseRepository firebaseRepository) {
        this.firebaseRepository = firebaseRepository;
    }

    public interface AddButtonCallback {
        void onClick(String text);
    }
}
