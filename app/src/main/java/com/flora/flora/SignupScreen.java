package com.flora.flora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupScreen extends AppCompatActivity {

    private TextInputEditText name, email, phoneno, password, confirmPassword;
    private AppCompatButton signUpBtn;
    private TextView loginBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String uId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        findId();

        signUpBtn.setOnClickListener(view -> {
            Validate();
        });

        loginBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), LoginScreen.class);
            startActivity(intent);
        });
    }

    @SuppressLint("CutPasteId")
    private  void findId(){
        name = findViewById(R.id.signup_name);
        email = findViewById(R.id.signup_email);
        phoneno = findViewById(R.id.signup_phoneno);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.signup_confirm_password);

        signUpBtn = findViewById(R.id.signup_btn);
        loginBtn = findViewById(R.id.login_btn);
    }

    private void Validate(){
        String nameToast = "Name must not be empty.";
        String emailToast = "Invalid email.";
        String phoneToast = "Invalid phone number.";
        String emptyPasswordToast = "Password is not valid.";
        String passwordToast = "Password and confirm Password is not match";

        String nameText = Objects.requireNonNull(name.getText()).toString();
        String emailText = Objects.requireNonNull(email.getText()).toString();
        String phoneText = Objects.requireNonNull(phoneno.getText()).toString();
        String passwordText = Objects.requireNonNull(password.getText()).toString();
        String confirmPasswordText = Objects.requireNonNull(confirmPassword.getText()).toString();
        String allergies = "none";

        if (!nameText.isEmpty()){
            if(isValidEmailAddress(emailText)){
                if(isMobileNumberValid(phoneText)){
                    if(isValidPassword(passwordText)){
                        if(passwordText.equals(confirmPasswordText)){
                            creatUser(nameText, emailText, phoneText, allergies, passwordText);
                        }
                        else {
                            setToast(passwordText+" "+confirmPasswordText);
                        }
                    }
                    else {
                        setToast(emptyPasswordToast);
                    }
                }
                else {
                    setToast(phoneToast);
                }
            }
            else {
                setToast(emailToast);
            }
        } else {
            setToast(nameToast);
        }
    }
    public boolean isValidEmailAddress(String emailID) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = java.util.regex.Pattern.compile(ePattern);
        Matcher m = p.matcher(emailID);
        if(!Objects.requireNonNull(email.getText()).toString().isEmpty()){
            return m.matches();
        }
        return false;
    }

    public boolean isMobileNumberValid(String phoneNumber) {
        String regex = "[0-9*#+() -]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        if(!Objects.requireNonNull(phoneno.getText()).toString().isEmpty()){
            return matcher.matches();
        }
        return false;
    }

    public static boolean isValidPassword(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public void setToast(String msg){
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void creatUser(String name, String email, String phone,String allergies, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
                if(mFirebaseUser != null) {
                    uId = mFirebaseUser.getUid();
                    DocumentReference documentReference = firestore.collection("users").document(uId);

                    Map<String, String> user = new HashMap<>();
                    user.put("name", name);
                    user.put("email", email);
                    user.put("phone", phone);
                    user.put("password", password);
                    user.put("allergies", allergies);

                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            setToast(e.toString());
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                setToast(e.toString());
            }
        });
    }
}