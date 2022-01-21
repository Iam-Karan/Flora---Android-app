package com.flora.flora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CartScreen extends AppCompatActivity {


    Toolbar toolbar;
    ImageButton backImageButton;
    AppCompatButton checkOutButton;
    private final ArrayList<CartProductData> cartProductData = new ArrayList<>();
    FirebaseFirestore firestore;
    String uId;
    private RecyclerView cartRecyclerView;
    CartRecyclerView adapter;
    double subTotal = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_screen);
        CartScreen.this.setTitle("");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        assert mFirebaseUser != null;
        uId = mFirebaseUser.getUid();
        findId();
        setData();
        setAdapter();

        backImageButton.setOnClickListener(view -> onBackPressed());
        checkOutButton.setOnClickListener(view -> {
            Intent intent  = new Intent(getBaseContext(), ShipingActivity.class);
            intent.putExtra("SubTotal", subTotal);
            startActivity(intent);
        });
    }

    public void findId(){
        cartRecyclerView = findViewById(R.id.cart_recyclerview);
        backImageButton = findViewById(R.id.back_button);
        checkOutButton = findViewById(R.id.checkout_btn);
    }

    private void setAdapter() {
        adapter = new CartRecyclerView(cartProductData);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        cartRecyclerView.setLayoutManager(layoutManager);
        cartRecyclerView.setItemAnimator(new DefaultItemAnimator());
        cartRecyclerView.setAdapter(adapter);
    }

    public void setData(){
        firestore.collection("users").document(uId).collection("cart").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(Objects.requireNonNull(task.getResult()).size() > 0) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {

                                    String docId = document.getId();
                                    CartModel data = document.toObject(CartModel.class);

                                    firestore.collection("products").get()
                                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                                if (!queryDocumentSnapshots.isEmpty()) {

                                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                    for (DocumentSnapshot d : list) {
                                                        ProductData productData = d.toObject(ProductData.class);
                                                        assert productData != null;
                                                        String itemId = productData.getId();
                                                        Double price = Double.parseDouble(productData.getPrice().toString());
                                                        if(itemId.equals(docId)){
                                                            assert data != null;
                                                            int count = Integer.parseInt(data.getQuantity());
                                                            subTotal = subTotal + (price * count);
                                                            CartProductData cartData = new CartProductData(productData, data.getQuantity());
                                                            cartProductData.add(cartData);
                                                        }
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                } else {
                                                    Toast.makeText(getBaseContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(e -> Toast.makeText(getBaseContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show());
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Task Fails to get Cart products", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void reloadActivity(){
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}