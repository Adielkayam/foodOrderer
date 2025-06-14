package com.example.foodorderer.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderer.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends BaseActivity {

    private Button buttonLogOut;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupBottomNavigation();

        mAuth = FirebaseAuth.getInstance();
        buttonLogOut = findViewById(R.id.buttonLogOut);

        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutUser();
            }
        });
    }

    private void logOutUser() {
        mAuth.signOut();
        Toast.makeText(SettingsActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SettingsActivity.this, IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finish SettingsActivity
    }
}
