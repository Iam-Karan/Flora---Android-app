package com.flora.flora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginScreen extends AppCompatActivity {

    private TextInputEditText email, password;
    private AppCompatButton loginBtn;
    private TextView signUpBtn, invalidText;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        mAuth = FirebaseAuth.getInstance();
        findId();

        loginBtn.setOnClickListener(view -> validate());

        signUpBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), SignupScreen.class);
            startActivity(intent);
        });
    }

    public void findId(){
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        invalidText = findViewById(R.id.login_invalid_login);
        loginBtn = findViewById(R.id.login_btn);
        signUpBtn = findViewById(R.id.signup_btn);
    }

    public void validate(){
        String emailText = Objects.requireNonNull(email.getText()).toString();
        String passwordText = Objects.requireNonNull(password.getText()).toString();
        if(emailText.isEmpty() || passwordText.isEmpty()){
            invalidText.setVisibility(View.VISIBLE);
            return;
        }
        loginUser(emailText, passwordText);
        invalidText.setVisibility(View.GONE);

    }

    public void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            invalidText.setVisibility(View.GONE);
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }).addOnFailureListener(e -> invalidText.setVisibility(View.VISIBLE));
    }
}