package com.flora.flora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CustomizeBoquetActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton backImageButton;
    private final ArrayList<CartProductData> productItemData = new ArrayList<>();
    private final ArrayList<CartProductData> cartProductData = new ArrayList<>();
    private RecyclerView flowerRecyclerView;
    ArrayList<String> productIds = new ArrayList<>();
    ArrayList<String> productCount = new ArrayList<>();
    FirebaseFirestore firestore;
    BoquetRecyclerViewAdapter adapter;
    AppCompatButton createBoquet;
    String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_boquet);
        CustomizeBoquetActivity.this.setTitle("");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firestore = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        assert mFirebaseUser != null;
        uId = mFirebaseUser.getUid();
        findId();

        setProductsInfo();
        setAdapter();

        createBoquet.setOnClickListener(view -> {
            for(int i = 0; i < productItemData.size(); i++){
                String productId = productItemData.get(i).getProductData().getId();
                int count = Integer.parseInt(productItemData.get(i).getQuantity());
                if(count > 0) {
                    addToCart(productId, count);
                }
            }
        });
        backImageButton.setOnClickListener(view -> onBackPressed());
    }
    public void findId(){
        flowerRecyclerView = findViewById(R.id.boquet_recyclerView);
        createBoquet = findViewById(R.id.create_boquet);
        backImageButton = findViewById(R.id.back_button);
    }

    private void setAdapter() {
        adapter = new BoquetRecyclerViewAdapter(productItemData);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        flowerRecyclerView.setLayoutManager(layoutManager);
        flowerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        flowerRecyclerView.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setProductsInfo() {
        firestore.collection("products").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {

                            ProductData productData = d.toObject(ProductData.class);
                            assert productData != null;
                            String type = productData.getType();
                            if(type.equals("flower")){
                                CartProductData data = new CartProductData(productData, "0");
                                productItemData.add(data);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show());
    }


    @SuppressLint("SetTextI18n")
    public void addToCart(String prductId, int count){

        firestore.collection("users").document(uId).collection("cart").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(Objects.requireNonNull(task.getResult()).size() > 0) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    CartModel data = document.toObject(CartModel.class);
                                    assert data != null;
                                    productIds.add(data.getProductid());
                                    productCount.add(data.getQuantity());
                                }
                            }
                            if(productIds.contains(prductId)){
                                for(int i = 0; i < productIds.size(); i++){
                                    if (prductId.equals(productIds.get(i))){
                                        updateCart(prductId, productCount.get(i), count);
                                    }
                                }
                            }
                            else {
                                addProductToCart(prductId, count);
                            }
                        } else {
                            addProductToCart(prductId, count);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Task Fails to get cart products", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @SuppressLint("SetTextI18n")
    public void addProductToCart(String prductId, int count){
        DocumentReference documentReference = firestore.collection("users").document(uId).collection("cart").document(prductId);

        Map<String, String> product = new HashMap<>();
        product.put("productid", prductId);
        product.put("quantity", ""+count);
        documentReference.set(product).addOnSuccessListener(unused -> {
            Toast.makeText(getApplicationContext(), "Item Added successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), CartScreen.class);
            startActivity(intent);
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
    }

    public void updateCart(String prductId, String quntity, int count){
        int itemCountInt = Integer.parseInt(quntity);
        int updatedItemCount = itemCountInt + count;

        Map<String, Object> product = new HashMap<>();
        product.put("productid", prductId);
        product.put("quantity", ""+updatedItemCount);
        firestore.collection("users").document(uId).collection("cart").document(prductId)
                .update(product)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getApplicationContext(), "Item Added successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), CartScreen.class);
                    startActivity(intent);
                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}