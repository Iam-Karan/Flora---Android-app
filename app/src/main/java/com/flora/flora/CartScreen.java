package com.flora.flora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class CartScreen extends AppCompatActivity {


    Toolbar toolbar;
    ImageButton backImageButton;
    AppCompatButton checkOutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_screen);
        CartScreen.this.setTitle("");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findId();

        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(getBaseContext(), ShipingActivity.class);
                startActivity(intent);
            }
        });
    }

    public void findId(){
        backImageButton = findViewById(R.id.back_button);
        checkOutButton = findViewById(R.id.checkout_btn);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}