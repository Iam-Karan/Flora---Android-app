package com.flora.flora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ProductDetailScreen extends AppCompatActivity {

    TextView productName, productDescrition, productPrice, itemCount;
    Toolbar toolbar;
    ImageButton backImageButton, addButton, removeButton, addToFavourite, removeFavourite;
    ImageView productImage;
    AppCompatButton addtoCart;
    String prductId;
    String uId;
    int count = 1;

    FirebaseFirestore firestore;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_screen);
        ProductDetailScreen.this.setTitle("");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        prductId = intent.getExtras().getString("ProductId");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        assert mFirebaseUser != null;
        uId = mFirebaseUser.getUid();

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
            setAddToFavourite();
            removeFavourite.setVisibility(View.VISIBLE);
            addToFavourite.setVisibility(View.GONE);
        });

        removeFavourite.setOnClickListener(view -> {
            setRemoveFavourite();
            addToFavourite.setVisibility(View.VISIBLE);
            removeFavourite.setVisibility(View.GONE);
        });

        addtoCart.setOnClickListener(view -> addToCart());

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

    @SuppressLint("SetTextI18n")
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


        firestore.collection("users").document(uId).collection("favourite").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(Objects.requireNonNull(task.getResult()).size() > 0) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    if(prductId.equals(document.getId())){
                                        removeFavourite.setVisibility(View.VISIBLE);
                                        addToFavourite.setVisibility(View.GONE);
                                    }
                                }

                            }
                        } else {
                            removeFavourite.setVisibility(View.GONE);
                            addToFavourite.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Task Fails to get Favourite products", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void addToCart(){

        firestore.collection("users").document(uId).collection("cart").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(Objects.requireNonNull(task.getResult()).size() > 0) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    CartModel data = document.toObject(CartModel.class);
                                    assert data != null;

                                    if(prductId.equals(data.getProductid())){
                                        String itemCountString = data.getQuantity();
                                        int itemCountInt = Integer.parseInt(itemCountString);
                                        int updatedItemCount = itemCountInt + count;

                                        Map<String, Object> product = new HashMap<>();
                                        product.put("productid", prductId);
                                        product.put("quantity", ""+updatedItemCount);
                                        firestore.collection("users").document(uId).collection("cart").document(prductId)
                                                .update(product)
                                                .addOnSuccessListener(unused -> {
                                                    count = 1;
                                                    itemCount.setText(""+count);
                                                    Toast.makeText(getApplicationContext(), "Item Added successfully!", Toast.LENGTH_SHORT).show();
                                                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
                                    }
                                }
                            }
                        } else {
                            addProductToCart();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Task Fails to get cart products", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void addProductToCart(){
        DocumentReference documentReference = firestore.collection("users").document(uId).collection("cart").document(prductId);

        Map<String, String> product = new HashMap<>();
        product.put("productid", prductId);
        product.put("quantity", ""+count);
        documentReference.set(product).addOnSuccessListener(unused -> {
            count = 1;
            itemCount.setText(""+count);
            Toast.makeText(getApplicationContext(), "Item Added successfully!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
    }

    public void setAddToFavourite(){
        DocumentReference documentReference = firestore.collection("users").document(uId).collection("favourite").document(prductId);

        Map<String, String> product = new HashMap<>();
        product.put("productid", prductId);
        documentReference.set(product).addOnSuccessListener(unused -> Toast.makeText(getApplicationContext(), "Item Added successfully!", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
    }

    public void setRemoveFavourite(){
        firestore.collection("users").document(uId).collection("favourite").document(prductId)
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), "Item removed From Favourite successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w("error", e.toString()));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}