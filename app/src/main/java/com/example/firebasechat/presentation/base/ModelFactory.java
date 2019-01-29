package com.example.firebasechat.presentation.base;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.firebasechat.App;
import com.example.firebasechat.data.FirebaseRepository;
import com.example.firebasechat.presentation.chats.ChatsViewModel;
import com.example.firebasechat.presentation.contacts.ContactsViewModel;

public class ModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final App app;
    private final FirebaseRepository repository;

    public ModelFactory(App app) {
        this.app = app;
        this.repository = app.getFirebaseRepository();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == ContactsViewModel.class) {
            return (T) new ContactsViewModel(repository, app.getSharedPreferecesManager());
        }
        if (modelClass == ChatsViewModel.class) {
            return (T) new ChatsViewModel(repository);
        }
        return null;
    }
}
