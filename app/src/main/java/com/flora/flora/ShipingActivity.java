package com.flora.flora;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class ShipingActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton backImageButton;
    double subTotalValue;
    private double taxValue = 15.0;
    TextInputEditText firstName,lastName, addressLine, city, zipcode, state, country, note;
    TextView subtotal, discount, tax, total;
    String firstNameString, lastNameString, adddressLineString, cityString, zipcodeString, stateString, countryString, noteString;
    String date, day, documentId;
    int hour, minute;
    ArrayList<CartModel> cartData = new ArrayList<>();
    FirebaseFirestore firestore;
    String uId;
    AppCompatButton buy;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiping);
        ShipingActivity.this.setTitle("");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        subTotalValue = (double) intent.getExtras().get("SubTotal") * 100;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        assert mFirebaseUser != null;
        uId = mFirebaseUser.getUid();
        Log.d("subtotal", ""+subTotalValue);
        findId();
        setData();
        buy.setOnClickListener(view -> {
            if(fatchAndValidate()){
                submitOrder();
            }
        });
        backImageButton.setOnClickListener(view -> onBackPressed());
    }

    public void findId(){
        firstName = findViewById(R.id.shiping_first_name);
        lastName = findViewById(R.id.shiping_last_name);
        addressLine = findViewById(R.id.shiping_address_line);
        city = findViewById(R.id.shiping_city);
        zipcode = findViewById(R.id.shiping_zipcode);
        state = findViewById(R.id.shiping_state);
        country = findViewById(R.id.shiping_country);
        note = findViewById(R.id.shiping_note);
        subtotal = findViewById(R.id.shiping_subtotal);
        discount = findViewById(R.id.shiping_discount);
        tax = findViewById(R.id.shiping_tax);
        total = findViewById(R.id.shiping_total);
        buy = findViewById(R.id.shiping_buy);
        backImageButton = findViewById(R.id.back_button);
    }

    @SuppressLint("SetTextI18n")
    public void setData(){
        Random r = new Random();
        int random = r.nextInt(30);
        double discountValue = ((random * 1.0) * subTotalValue) / 100;
        taxValue = (taxValue * subTotalValue) / 100;
        double totalValue = subTotalValue + taxValue - discountValue;

        double subValue = Math.round(subTotalValue) / 100;
        double disValue = Math.round(discountValue) / 100;
        double tValue = Math.round(taxValue) / 100;
        double totValue = Math.round(totalValue) / 100;


        subtotal.setText("$"+subValue);
        discount.setText("-$"+ disValue);
        tax.setText("$"+tValue);
        total.setText("$"+ totValue);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Boolean fatchAndValidate(){

        boolean validate = true;

        firstNameString = firstName.getText().toString();
        lastNameString = lastName.getText().toString();
        adddressLineString = addressLine.getText().toString();
        cityString = city.getText().toString();
        zipcodeString = zipcode.getText().toString();
        stateString = state.getText().toString();
        countryString = country.getText().toString();
        noteString = note.getText().toString();

        Calendar rightNow = Calendar.getInstance();
        hour = rightNow.get(Calendar.HOUR_OF_DAY);
        minute = rightNow.get(Calendar.MINUTE);
        LocalDate currentdate = LocalDate.now();
        date = currentdate.toString();
        day = currentdate.getDayOfWeek().name();
        Random random = new Random();

        documentId = random.ints(97, 122 + 1)
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        if(firstNameString.isEmpty()){
            firstName.setError("First Name required");
            validate = false;
        }
        if(lastNameString.isEmpty()){
            lastName.setError("Last Name required");
            validate = false;
        }
        if(adddressLineString.isEmpty()){
            addressLine.setError("Line should not empty");
            validate = false;
        }
        if(cityString.isEmpty()){
            city.setError("City require");
            validate = false;
        }
        if(stateString.isEmpty()){
            state.setError("State name required");
            validate = false;
        }
        if(zipcodeString.isEmpty()){
            zipcode.setError("Zipcode require");
            validate = false;
        }
        if(countryString.isEmpty()){
            country.setError("Country require");
            validate = false;
        }
        if(noteString.isEmpty()){
            noteString = "Default";
        }


        firestore.collection("users").document(uId).collection("cart").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(Objects.requireNonNull(task.getResult()).size() > 0) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    CartModel data = document.toObject(CartModel.class);
                                    cartData.add(data);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Task Fails to get Cart products", Toast.LENGTH_SHORT).show();
                    }
                });

        return validate;
    }

    public void submitOrder(){
        String name = firstNameString+lastNameString;
        String address = adddressLineString+", "+cityString+", "+stateString+", "+countryString+", "+zipcodeString;
        String time = hour+":"+minute+", "+day+", "+date;
        DocumentReference documentReference = firestore.collection("users").document(uId).collection("order").document(documentId);

        Map<String, String> orderData = new HashMap<>();
        orderData.put("orderid", documentId);
        orderData.put("name", name);
        orderData.put("address", address);
        orderData.put("note", noteString);
        orderData.put("placed", time);
        orderData.put("confirmed", "");
        orderData.put("prepared", "");
        orderData.put("deliverd", "");
        orderData.put("hour", ""+hour);
        orderData.put("minute", ""+minute);
        documentReference.set(orderData).addOnSuccessListener(unused -> {
            for(int i = 0; i < cartData.size(); i++){
                String productId = cartData.get(i).getProductid();
                DocumentReference docReference = firestore.collection("users").document(uId).collection("order").document(documentId).collection("products").document(productId);
                Map<String, String> products = new HashMap<>();
                products.put("productid", productId);
                products.put("quantity", cartData.get(i).getQuantity());
                docReference.set(products).addOnSuccessListener(unused1 -> firestore.collection("users").document(uId).collection("cart").document(productId)
                        .delete()
                        .addOnSuccessListener(unused2 -> {
                            Toast.makeText(getApplicationContext(), "Item Added successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> Log.w("error", e.toString()))).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
            }
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CartScreen.class);
        startActivity(intent);
    }
}