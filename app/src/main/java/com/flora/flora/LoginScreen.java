package com.flora.flora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import java.util.Objects;

public class LoginScreen extends AppCompatActivity {

    private static final int RC_SIGN_IN = 120;
    private TextInputEditText email, password;
    private AppCompatButton loginBtn;
    private TextView signUpBtn, invalidText;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInButton mGoogleSignInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        mAuth = FirebaseAuth.getInstance();
        findId();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginBtn.setOnClickListener(view -> validate());

        signUpBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), SignupScreen.class);
            startActivity(intent);
        });
        mGoogleSignInButton.setOnClickListener(view -> signIn());
    }

    public void findId(){
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        invalidText = findViewById(R.id.login_invalid_login);
        loginBtn = findViewById(R.id.login_btn);
        signUpBtn = findViewById(R.id.signup_btn);
        mGoogleSignInButton = findViewById(R.id.login_with_google);
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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            onBackPressed();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            //updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}