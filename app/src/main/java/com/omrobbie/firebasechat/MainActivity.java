package com.omrobbie.firebasechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.omrobbie.firebasechat.data.ChatMessage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;

    @BindView(R.id.chat_message)
    EditText chatMessage;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.list_of_messages)
    ListView listOfMessages;

    private FirebaseAnalytics firebaseAnalytics;
    private FirebaseListAdapter<ChatMessage> adapter;

    private String displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupEnv();
        setupList();
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
        ButterKnife.bind(this);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (isSignIn()) showMessage();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = chatMessage.getText().toString();
                if (messageText.isEmpty()) return;

                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(new ChatMessage(displayName, messageText));

                chatMessage.setText("");
            }
        });
    }

    private void setupList() {
        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
                .setLayout(R.layout.item_message)
                .setQuery(FirebaseDatabase.getInstance().getReference(), ChatMessage.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                messageUser.setText(model.getMessageUser());
                messageText.setText(model.getMessageText());
                messageTime.setText(DateFormat.format(
                        "dd-MMM-yyyy (HH:mm:ss)",
                        model.getMessageTime()
                ));
            }
        };
        listOfMessages.setAdapter(adapter);
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
        Toast.makeText(this, "Welcome " + displayName, Toast.LENGTH_SHORT).show();
    }
}
