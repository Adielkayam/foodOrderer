package com.example.foodorderer.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
// Unused Button import removed
// Unused ConstraintLayout import removed
// Unused Insets, ViewCompat, WindowInsetsCompat imports removed if EdgeToEdge.enable(this) handles it.
// Keeping EdgeToEdge for now.
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
// Unused R import removed as binding is used
import com.example.foodorderer.databinding.ActivityIntroBinding;
// Unused Inflater import removed
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
// EditText import is not directly used if accessing via binding.editTextEmail

public class IntroActivity extends AppCompatActivity {
    private ActivityIntroBinding binding;
    private FirebaseAuth mAuth;
    // private ConstraintLayout startBtn; // This variable is not used with view binding approach

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this); // Assuming this handles window insets

        mAuth = FirebaseAuth.getInstance();

        binding.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.editTextEmail.getText().toString().trim();
                String password = binding.editTextPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(IntroActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(IntroActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, try to sign up the user
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(IntroActivity.this, signupTask -> {
                                            if (signupTask.isSuccessful()) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(IntroActivity.this, "Authentication and sign-up failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Prevent returning to IntroActivity if already signed in
        }
    }
}