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

public class login extends Fragment {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;
    private NavController navController;

    public login() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        mAuth = FirebaseAuth.getInstance();

        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnRegister = view.findViewById(R.id.btnRegister);

        // ✅ Fix: Use NavHostFragment to find NavController
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        } else {
            throw new IllegalStateException("NavController Error.");
        }

        // Handle Login Button Click
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(getActivity(), "Enter mail and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Sign in with Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Toast.makeText(getActivity(), "Wellcome", Toast.LENGTH_SHORT).show();
                                navController.navigate(R.id.home_page);
                            }
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(getActivity(), "Login Failed: check your mail and password " , Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Navigate to RegisterFragment
        btnRegister.setOnClickListener(v -> navController.navigate(R.id.register));

        return view;
    }
}

