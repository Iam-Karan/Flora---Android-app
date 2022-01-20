package com.flora.flora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserProfile extends AppCompatActivity {

    private TextView nameTextView, emailTextView, phoneTextView, allergiesTextView, passwordTextView;
    private EditText nameEditText, emailEditText, phoneEditText, allergiesEditText, passwordEditText, confirmPasswordEditText;
    private ImageButton editName, saveName, editEmail, saveEmail, editPhone, savePhone, editAllergies, saveAllergies, editPassword, savePassword;
    private LinearLayout confirmPasswordLayout;
    private ImageButton backImageButton;
    private Button signout;
    private FirebaseFirestore firestore;
    private String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        UserProfile.this.setTitle("");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            uId = mFirebaseUser.getUid();
            setData();
        }else {
            Intent intent =  new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(intent);
        }
        findId();


        editName.setOnClickListener(view -> {
            editName.setVisibility(View.GONE);
            nameTextView.setVisibility(View.GONE);
            nameEditText.setVisibility(View.VISIBLE);
            saveName.setVisibility(View.VISIBLE);
        });

        editEmail.setOnClickListener(view -> {
            editEmail.setVisibility(View.GONE);
            emailTextView.setVisibility(View.GONE);
            emailEditText.setVisibility(View.VISIBLE);
            saveEmail.setVisibility(View.VISIBLE);
        });

        editPhone.setOnClickListener(view -> {
            editPhone.setVisibility(View.GONE);
            phoneTextView.setVisibility(View.GONE);
            phoneEditText.setVisibility(View.VISIBLE);
            savePhone.setVisibility(View.VISIBLE);
        });

        editAllergies.setOnClickListener(view -> {
            editAllergies.setVisibility(View.GONE);
            allergiesTextView.setVisibility(View.GONE);
            allergiesEditText.setVisibility(View.VISIBLE);
            saveAllergies.setVisibility(View.VISIBLE);
        });

        editPassword.setOnClickListener(view -> {
            editPassword.setVisibility(View.GONE);
            passwordTextView.setVisibility(View.GONE);
            passwordEditText.setVisibility(View.VISIBLE);
            confirmPasswordLayout.setVisibility(View.VISIBLE);
        });

        saveName.setOnClickListener(view -> updateName());

        saveEmail.setOnClickListener(view -> updateEmail());

        savePhone.setOnClickListener(view -> updatePhone());

        saveAllergies.setOnClickListener(view -> updateAllergies());

        savePassword.setOnClickListener(view -> updatePassword());

        backImageButton.setOnClickListener(view -> onBackPressed());

        signout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

    }

    public void findId(){
        nameTextView = findViewById(R.id.user_profile_name);
        emailTextView = findViewById(R.id.user_profile_email);
        phoneTextView = findViewById(R.id.user_profile_phone);
        allergiesTextView = findViewById(R.id.user_profile_allergies);
        passwordTextView = findViewById(R.id.user_profile_password);

        nameEditText = findViewById(R.id.edit_user_name);
        emailEditText = findViewById(R.id.edit_user_email);
        phoneEditText = findViewById(R.id.edit_user_phone);
        allergiesEditText = findViewById(R.id.edit_user_allergies);
        passwordEditText = findViewById(R.id.edit_user_password);
        confirmPasswordEditText = findViewById(R.id.edit_user_confirmpassword);

        confirmPasswordLayout = findViewById(R.id.user_profile_confirmpassword);

        editName = findViewById(R.id.user_name_edit);
        editEmail = findViewById(R.id.user_email_edit);
        editPhone = findViewById(R.id.user_phone_edit);
        editAllergies = findViewById(R.id.user_allergies_edit);
        editPassword = findViewById(R.id.user_password_edit);

        saveName = findViewById(R.id.user_name_save);
        saveEmail = findViewById(R.id.user_email_save);
        savePhone = findViewById(R.id.user_phone_save);
        saveAllergies = findViewById(R.id.user_allergies_save);
        savePassword = findViewById(R.id.user_password_save);

        signout = findViewById(R.id.signout_btn);
        backImageButton = findViewById(R.id.back_button);
    }

    public void setData(){
        firestore.collection("users").document(uId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    String name = Objects.requireNonNull(document.getData().get("name")).toString();
                    String email = Objects.requireNonNull(document.getData().get("email")).toString();
                    String phone = Objects.requireNonNull(document.getData().get("phone")).toString();
                    String allergies = Objects.requireNonNull(document.getData().get("allergies")).toString();

                    nameTextView.setText(name);
                    emailTextView.setText(email);
                    phoneTextView.setText(phone);
                    allergiesTextView.setText(allergies);

                    nameEditText.setHint(name);
                    emailEditText.setHint(email);
                    phoneEditText.setHint(phone);
                    allergiesEditText.setHint(allergies);
                } else {
                    Log.d("document Not Found", "No such document");
                }
            } else {
                Log.d("error", "get failed with ", task.getException());
            }
        }).addOnFailureListener(e -> Log.d("error", e.toString()));

    }
    public void updateName(){

        String upadatedName = nameEditText.getText().toString();
        if(!upadatedName.isEmpty()){
            firestore.collection("users").document(uId)
                    .update("name", upadatedName)
                    .addOnSuccessListener(unused -> {
                        editName.setVisibility(View.VISIBLE);
                        nameTextView.setVisibility(View.VISIBLE);
                        nameEditText.setVisibility(View.GONE);
                        saveName.setVisibility(View.GONE);
                        setData();
                        setToast("Name change Successfully!");
                    }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
        }else {
            setToast("Name Should not be empty");
        }

    }
    public void updateEmail(){

        String updatedEmail = emailEditText.getText().toString();

        if(validateEmail(updatedEmail)){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            user.updateEmail(updatedEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            editEmail.setVisibility(View.VISIBLE);
                            emailTextView.setVisibility(View.VISIBLE);
                            emailEditText.setVisibility(View.GONE);
                            saveEmail.setVisibility(View.GONE);
                            setToast("Email Change Successfully!");
                            setData();
                        }
                    });
        }else {
            setToast("Invalid email id");
        }

    }
    public void updatePhone(){

        String upadatedPhone = phoneEditText.getText().toString();
        if(validatePhone(upadatedPhone)){
            firestore.collection("users").document(uId)
                    .update("phone", upadatedPhone)
                    .addOnSuccessListener(unused -> {
                        editPhone.setVisibility(View.VISIBLE);
                        phoneTextView.setVisibility(View.VISIBLE);
                        phoneEditText.setVisibility(View.GONE);
                        savePhone.setVisibility(View.GONE);
                        setData();
                        setToast("Phone change Successfully!");
                    }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
        }else {
            setToast("Phone no is not valid");
        }
    }
    public void updateAllergies(){
        String upadatedAllergies = allergiesEditText.getText().toString();
        if(!upadatedAllergies.isEmpty()){
            firestore.collection("users").document(uId)
                    .update("allergies", upadatedAllergies)
                    .addOnSuccessListener(unused -> {
                        editAllergies.setVisibility(View.VISIBLE);
                        allergiesTextView.setVisibility(View.VISIBLE);
                        allergiesEditText.setVisibility(View.GONE);
                        saveAllergies.setVisibility(View.GONE);
                        setData();
                        setToast("Name change Successfully!");
                    }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
        }else {
            setToast("Name Should not be empty");
        }

    }
    public void updatePassword(){

        String updatedPassword = passwordEditText.getText().toString();
        String updatedConfirmPasword = confirmPasswordEditText.getText().toString();

        if(validatePassword(updatedPassword)){
            if(updatedPassword.equals(updatedConfirmPasword)){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                user.updatePassword(updatedPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                editPassword.setVisibility(View.VISIBLE);
                                passwordTextView.setVisibility(View.VISIBLE);
                                passwordEditText.setVisibility(View.GONE);
                                confirmPasswordLayout.setVisibility(View.GONE);
                                setToast("Password updated successfully!");
                                setData();
                            }
                        });
            }else{
                setToast("Password and confirm password is not match.");
            }
        }else {
            setToast("Use strong password.");
        }


    }

    public boolean validateEmail(String emailID){
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = java.util.regex.Pattern.compile(ePattern);
        Matcher m = p.matcher(emailID);
        if(!emailID.isEmpty()){
            return m.matches();
        }
        return false;
    }
    public boolean validatePassword(String password){
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    public boolean validatePhone(String phoneNumber){
        String regex = "[0-9*#+() -]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        if(!phoneNumber.isEmpty()){
            return matcher.matches();
        }
        return false;
    }
    public void setToast(String msg){
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}