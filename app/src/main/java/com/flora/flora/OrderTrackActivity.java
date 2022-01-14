package com.flora.flora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class OrderTrackActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton backImageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_track);
        OrderTrackActivity.this.setTitle("");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findId();
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void findId(){
        backImageButton = findViewById(R.id.back_button);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, OrdersActivity.class);
        startActivity(intent);
    }
}