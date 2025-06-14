package com.example.foodorderer.Activity;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderer.R;

public class BaseActivity extends AppCompatActivity {
    protected void setupBottomNavigation() {
        findViewById(R.id.homeBtn).setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        findViewById(R.id.ProfileBtn).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        findViewById(R.id.SettingsBtn).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        findViewById(R.id.cartBtn).setOnClickListener(v -> startActivity(new Intent(this, CartListActivity.class)));
        findViewById(R.id.SupportBtn).setOnClickListener(v -> Toast.makeText(this, "Support Clicked", Toast.LENGTH_SHORT).show());
    }
}

