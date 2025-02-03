package com.example.matala3.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.matala3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class register extends Fragment {

    private EditText etUsername, etPhone, etEmail, etPassword, etRepeatPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore ffdb;
    private NavController navController;

    public register() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize Firebase Auth & Firestore
        mAuth = FirebaseAuth.getInstance();
        ffdb = FirebaseFirestore.getInstance();

        // Initialize UI elements
        etUsername = view.findViewById(R.id.etUsername);
        etPhone = view.findViewById(R.id.etPhone);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etRepeatPassword = view.findViewById(R.id.etRepeatPassword);
        btnRegister = view.findViewById(R.id.btnRegister);

        // ✅ Fix: Use NavHostFragment to find NavController
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        } else {
            throw new IllegalStateException("NavController not found. Check activity_main.xml.");
        }

        // Handle Register Button Click
        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String repeatPassword = etRepeatPassword.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(password) || TextUtils.isEmpty(repeatPassword)) {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(repeatPassword)) {
                Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user in Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // ✅ Store user data in Firestore
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("username", username);
                                userData.put("phone", phone);
                                userData.put("email", email);

                                ffdb.collection("users").document(user.getUid())
                                        .set(userData)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getActivity(), "Registration Successful!", Toast.LENGTH_SHORT).show();
                                            navController.navigate(R.id.login2); // ✅ Navigate to Login
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(getActivity(), "Registration Failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        return view;
    }
}


