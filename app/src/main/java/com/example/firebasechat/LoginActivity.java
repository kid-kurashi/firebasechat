package com.example.firebasechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.firebasechat.pojo.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class LoginActivity extends AppCompatActivity {

    private BehaviorSubject<Boolean> isPushTokenSent = BehaviorSubject.create();
    private Disposable isPushTokenSentDisposable;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser firebaseUser;

    private ConstraintLayout content;
    private int RC_SIGN_IN = 333;
    private String TAG = "@@@";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        content = findViewById(R.id.content);
        findViewById(R.id.sign_in_button).setOnClickListener(v -> signInWithGoogle());

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            updateUI(null);
            signInWithGoogle();
        } else {
            updateUI(mAuth.getCurrentUser());
            sendPushTokenToFirebase();
        }
    }

    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void sendPushTokenToFirebase() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if (firebaseUser != null) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    isPushTokenSent.onNext(false);
                }
                if (task.getResult() != null) {
                    String token = task.getResult().getToken();

                    DatabaseReference currentUserReference = mDatabase
                            .child(Database.PATH_USERS)
                            .child(firebaseUser.getUid());

                    currentUserReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user;
                            user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                currentUserReference.child(Database.FIELD_DEVICETOKEN).setValue(token);
                            } else {
                                currentUserReference.setValue(new User(
                                        firebaseUser.getDisplayName(),
                                        firebaseUser.getUid(),
                                        token));
                            }
                            isPushTokenSent.onNext(true);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, databaseError.getMessage());
                        }
                    });
                }
            });
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
    protected void onPause() {
        if (isPushTokenSentDisposable != null && !isPushTokenSentDisposable.isDisposed())
            isPushTokenSentDisposable.dispose();
        super.onPause();
    }

    @Override
    protected void onStart() {
        isPushTokenSentDisposable = isPushTokenSent.subscribe(sent -> {
            if (sent) {
                startActivity(new Intent(this, ContactsActivity.class));
            } else {
                Toast.makeText(this, "Something goes wrong. Closing app...", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInWithCredential:success");
                        firebaseUser = mAuth.getCurrentUser();
                        updateUI(firebaseUser);
                        sendPushTokenToFirebase();
                    } else {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            content.setVisibility(View.VISIBLE);
        } else {
            content.setVisibility(View.GONE);
        }
    }
}
