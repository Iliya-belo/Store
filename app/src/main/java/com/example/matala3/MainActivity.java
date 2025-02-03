package com.example.matala3;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();

    }

    public void reg(String email, String password) {

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Registration successful
                        Toast.makeText(MainActivity.this, "Registration Successful!", Toast.LENGTH_LONG).show();

                        // Navigate to LoginFragment
                        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.home_page);
                        if (navHostFragment != null) {
                            NavController navController = navHostFragment.getNavController();
                            navController.navigate(R.id.login2);
                        }

                    } else {
                        // Registration failed
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }



    public void login (){

        String email = ((EditText)findViewById(R.id.etEmail)).getText().toString();
        String password = ((EditText)findViewById(R.id.etPassword)).getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_LONG).show();

                        } else {

                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }
}

