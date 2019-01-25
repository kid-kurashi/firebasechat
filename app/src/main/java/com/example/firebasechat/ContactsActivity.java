package com.example.firebasechat;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.firebasechat.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class ContactsActivity extends AppCompatActivity {

    private static final String DATABASE_TAG = "DATABASE";
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private BehaviorSubject<Boolean> isPushTokenSent = BehaviorSubject.create();
    private Disposable isPushTokenSentDisposable;
    private FirebaseFirestore database;
    private String pushToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseFirestore.getInstance();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {

        });

        connectToFirestore();
    }

    private void connectToFirestore() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                pushToken = task.getResult().getToken();
                DocumentReference userReference = getCurrentUserReference();
                userReference.addSnapshotListener((documentSnapshot, e) -> {
                    if (documentSnapshot != null && documentSnapshot.get("deviceToken") != null) {
                        updateDeviceToken(userReference);
                    } else {
                        createNewUser();
                    }
                });
            }
        });
    }

    private void createNewUser() {
        User user = new User(
                firebaseUser.getDisplayName(),
                firebaseUser.getUid(),
                pushToken, firebaseUser.getEmail());
        user.getContacts().add("new.email@gmail.com");
        getCurrentUserReference().set(user);
    }

    private void updateDeviceToken(DocumentReference userReference) {
        HashMap<String, Object> updateTokenMap = new HashMap<>();
        updateTokenMap.put("deviceToken", pushToken);
        userReference.update(updateTokenMap);
    }

    private DocumentReference getCurrentUserReference() {
        return database.collection("users").document(firebaseUser.getUid());
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
        isPushTokenSentDisposable = isPushTokenSent
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result) {
                        Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Cant put pushtoken into base", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (isPushTokenSentDisposable != null && !isPushTokenSentDisposable.isDisposed()) {
            isPushTokenSentDisposable.dispose();
        }
        super.onDestroy();
    }
}
