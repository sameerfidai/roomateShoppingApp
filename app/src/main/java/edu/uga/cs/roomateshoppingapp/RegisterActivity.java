package edu.uga.cs.roomateshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private final static String TAG = "RegisterActivity";

    private EditText registerName, registerEmail, registerPassword;
    private Button registerButton;
    private ProgressBar progressBarRegister;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        registerName = findViewById(R.id.registerNameEditText);
        registerEmail = findViewById(R.id.registerEmailEditText);
        registerPassword = findViewById(R.id.registerPasswordEditText);
        registerButton = findViewById(R.id.registerBtn);
        progressBarRegister = findViewById(R.id.progressBarRegister);

        // register the user
        registerButton.setOnClickListener(v -> {
            String name = registerName.getText().toString();
            String email = registerEmail.getText().toString();
            String password = registerPassword.getText().toString();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(this, "Password should be atleast 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBarRegister.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBarRegister.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If registration fails, check if it's due to email collision
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(RegisterActivity.this, "Email already in use", Toast.LENGTH_SHORT).show();
                        } else {
                            // Other errors
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    }
                }
            });
        });
    }
}