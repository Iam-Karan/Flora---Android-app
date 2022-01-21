package com.flora.flora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class OrdersActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton backImageButton;
    RecyclerView orderRecyclerView;

    private ArrayList<String> orderName = new ArrayList<>();
    private ArrayList<String> orderAddress = new ArrayList<>();
    private ArrayList<String> orderid = new ArrayList<>();
    LinearLayout orderData, noOrderData;
    FirebaseFirestore firestore;
    String uId;
    OrderAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        OrdersActivity.this.setTitle("");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        findId();
        if(mFirebaseUser != null){
            orderData.setVisibility(View.VISIBLE);
            noOrderData.setVisibility(View.GONE);
            uId = mFirebaseUser.getUid();
            setData();
        }else {
            orderData.setVisibility(View.GONE);
            noOrderData.setVisibility(View.VISIBLE);
        }
        backImageButton.setOnClickListener(view -> onBackPressed());

    }

    public void findId(){
        orderData = findViewById(R.id.order_list);
        noOrderData = findViewById(R.id.no_item_found);
        orderRecyclerView = findViewById(R.id.order_recyclerView);
        backImageButton = findViewById(R.id.back_button);
    }

    private void setAdapter() {
        adapter = new OrderAdapter(orderName, orderAddress, orderid);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        orderRecyclerView.setLayoutManager(layoutManager);
        orderRecyclerView.setItemAnimator(new DefaultItemAnimator());
        orderRecyclerView.setAdapter(adapter);
    }

    public void setData(){

        firestore.collection("users").document(uId).collection("order").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(Objects.requireNonNull(task.getResult()).size() > 0) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {

                                    String docId = document.getId();

                                    firestore.collection("users").document(uId).collection("order").document(docId).get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            DocumentSnapshot doc = task1.getResult();
                                            assert doc != null;
                                            if (doc.exists()) {
                                                String name = doc.getData().get("name").toString();
                                                String address = doc.getData().get("address").toString();
                                                String id = doc.getData().get("orderid").toString();
                                                Log.d("name", address+name+id);
                                                orderName.add(name);
                                                orderAddress.add(address);
                                                orderid.add(id);
                                            } else {
                                                Log.d("document Not Found", "No such document");
                                            }
                                            setAdapter();
                                        } else {
                                            Log.d("error", "get failed with ", task1.getException());
                                        }
                                    }).addOnFailureListener(e -> Log.d("error", e.toString()));

                                }else {
                                    orderData.setVisibility(View.GONE);
                                    noOrderData.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Task Fails to get Cart products", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}