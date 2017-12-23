package com.omrobbie.firebasechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;

    private FirebaseAnalytics firebaseAnalytics;

    private String displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupEnv();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Successfully signed in!", Toast.LENGTH_SHORT).show();
                showMessage();
            } else {
                Toast.makeText(this, "We couldn't sign you in. Please try again later.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mn_sign_out:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this, "You have been sign out.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupEnv() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (isSignIn()) showMessage();
    }

    private boolean isSignIn() {
        boolean isSigned;

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            isSigned = false;
            displayName = "";
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(), REQUEST_CODE
            );
        } else {
            isSigned = true;
            displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }

        return isSigned;
    }

    private void showMessage() {

    }
}
