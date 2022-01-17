package com.flora.flora;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class ProductDetailScreen extends AppCompatActivity {


    TextView productName, productDescrition, productPrice, itemCount;
    Toolbar toolbar;
    ImageButton backImageButton, addButton, removeButton, addToFavourite, removeFavourite;
    ImageView productImage;
    AppCompatButton addtoCart;
    String prductId;
    int count = 1;

    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_screen);
        ProductDetailScreen.this.setTitle("");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        prductId = intent.getExtras().getString("ProductId");

        firestore = FirebaseFirestore.getInstance();

        findId();
        setData();

        addButton.setOnClickListener(view -> {
            count = count + 1;
            itemCount.setText(""+count);
        });

        removeButton.setOnClickListener(view -> {
            if(count > 1){
                count = count - 1;
                itemCount.setText(""+count);
            }
        });

        addToFavourite.setOnClickListener(view -> {
            removeFavourite.setVisibility(View.VISIBLE);
            addToFavourite.setVisibility(View.GONE);
        });

        removeFavourite.setOnClickListener(view -> {
            addToFavourite.setVisibility(View.VISIBLE);
            removeFavourite.setVisibility(View.GONE);
        });

        backImageButton.setOnClickListener(view -> onBackPressed());

    }

    public void findId(){
        backImageButton = findViewById(R.id.back_button);
        productName = findViewById(R.id.product_name);
        productDescrition = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        itemCount = findViewById(R.id.item_count);
        addButton = findViewById(R.id.item_count_add);
        removeButton = findViewById(R.id.item_count_remove);
        addToFavourite = findViewById(R.id.product_favourite);
        addtoCart = findViewById(R.id.add_to_cart);
        productImage = findViewById(R.id.product_image);
        removeFavourite = findViewById(R.id.product_is_favourite);
    }

    public void setData(){
        DocumentReference docRef = firestore.collection("products").document(prductId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    ProductData data = document.toObject(ProductData.class);

                    assert data != null;
                    productName.setText(data.getName());
                    productPrice.setText("$"+data.getPrice());
                    productDescrition.setText(data.getDescription());
                    Picasso.get()
                            .load(data.getImage())
                            .placeholder(R.drawable.gift)
                            .error(R.drawable.flora)
                            .into(productImage);
                } else {
                    Log.d("document Not Found", "No such document");
                }
            } else {
                Log.d("error", "get failed with ", task.getException());
            }
        }).addOnFailureListener(e -> Log.d("error", e.toString()));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}