package com.flora.flora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;

import com.google.android.material.button.MaterialButton;

public class UserProfile extends AppCompatActivity {

    Toolbar toolbar;
    GridLayout userProfileGridLayout;
    GridLayout editProfileGridLayout;
    MaterialButton editProfileMaterialButton;
    MaterialButton saveProfileMaterialButton;
    ImageButton backImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        UserProfile.this.setTitle("");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findId();

        editProfileMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userProfileGridLayout.setVisibility(View.GONE);
                editProfileGridLayout.setVisibility(View.VISIBLE);
            }
        });

        saveProfileMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userProfileGridLayout.setVisibility(View.VISIBLE);
                editProfileGridLayout.setVisibility(View.GONE);
            }
        });

        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void findId(){
        userProfileGridLayout = findViewById(R.id.user_profile);
        editProfileGridLayout = findViewById(R.id.edit_profile);
        editProfileMaterialButton = findViewById(R.id.edit_profile_btn);
        saveProfileMaterialButton = findViewById(R.id.save_profile_btn);
        backImageButton = findViewById(R.id.back_button);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}