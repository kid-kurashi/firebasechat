package com.example.firebasechat.presentation.base;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.firebasechat.data.SharedPreferecesManager;
import com.example.firebasechat.presentation.contacts.ContactsViewModel;

public class ModelFactory extends ViewModelProvider.NewInstanceFactory {

    private  SharedPreferecesManager sharedPreferecesManager;

    public ModelFactory(SharedPreferecesManager sharedPreferecesManager) {
        this.sharedPreferecesManager = sharedPreferecesManager;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == ContactsViewModel.class) {
            return (T) new ContactsViewModel(sharedPreferecesManager);
        }
        return null;
    }
}
