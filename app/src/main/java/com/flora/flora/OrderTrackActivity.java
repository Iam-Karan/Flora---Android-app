package com.flora.flora;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class OrderTrackActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView orderidTextView, orderPlacedTextView, orderconfirmedTextView, orderPreparedTextView, orderDeliverdTextView;
    RadioButton orderPlacedRadioButton, orderConfirmedRadioButton, orderPreparedRadioButton, orderDeliverdRadioButton;
    ImageButton backImageButton;
    String orderId;
    String uId;
    FirebaseFirestore firestore;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_track);
        OrderTrackActivity.this.setTitle("");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        orderId = intent.getExtras().getString("orderid");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        assert mFirebaseUser != null;
        uId = mFirebaseUser.getUid();
        findId();
        setData();

        Timer timer = new Timer();

        timer.schedule( new TimerTask() {
            public void run() {
                setData();
            }
        }, 0, 60*1000);
        if(!orderDeliverdTextView.getText().toString().equals("Oredr is on the way")){
            timer.cancel();
        }

        backImageButton.setOnClickListener(view -> onBackPressed());
    }

    public void findId(){
        orderidTextView = findViewById(R.id.order_id);
        orderPlacedTextView = findViewById(R.id.order_placed);
        orderconfirmedTextView = findViewById(R.id.order_confirmed);
        orderPreparedTextView = findViewById(R.id.order_prepared);
        orderDeliverdTextView = findViewById(R.id.order_deliverd);

        orderPlacedRadioButton = findViewById(R.id.order_placed_radio);
        orderConfirmedRadioButton = findViewById(R.id.order_confirmed_radio);
        orderPreparedRadioButton = findViewById(R.id.order_prepared_radio);
        orderDeliverdRadioButton = findViewById(R.id.order_deliverd_radio);
        backImageButton = findViewById(R.id.back_button);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setData(){
        String preparing = "Preparing order";
        String confirming = "Confirming Order";
        String delevering = "Oredr is on the way";
        String waiting = "Waiting";
        LocalDate currentdate = LocalDate.now();
        String date = currentdate.toString();
        String day = currentdate.getDayOfWeek().name();

        firestore.collection("users").document(uId).collection("order").document(orderId).get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                DocumentSnapshot doc = task1.getResult();
                assert doc != null;
                if (doc.exists()) {
                   String oid = doc.getData().get("orderid").toString();
                   String placed = doc.getData().get("placed").toString();
                   String prepared = doc.getData().get("prepared").toString();
                   String confiremed = doc.getData().get("confirmed").toString();
                   String deliverd = doc.getData().get("deliverd").toString();
                   int hour = Integer.parseInt(doc.getData().get("hour").toString());
                   int minute = Integer.parseInt(doc.getData().get("minute").toString());

                   orderidTextView.setText(oid);
                   orderPlacedTextView.setText(placed);
                   orderPlacedRadioButton.toggle();
                   if(confiremed.isEmpty()){
                       orderconfirmedTextView.setText(confirming);
                       orderPreparedTextView.setText(waiting);
                       orderDeliverdTextView.setText(waiting);
                       if(checkTime(hour, minute, 5)){
                           if((minute + 5) >= 60){
                               minute = (minute + 5) - 60;
                               hour = hour + 1;
                               updateConfirmOrder(hour, minute, day , date);
                           }else{
                               updateConfirmOrder(hour, (minute + 5) , day , date);
                           }

                       }
                   } else {
                       orderConfirmedRadioButton.setChecked(true);
                       orderconfirmedTextView.setText(confiremed);
                       if(prepared.isEmpty()){
                           orderPreparedTextView.setText(preparing);
                           orderDeliverdTextView.setText(waiting);
                           if(checkTime(hour, minute, 20)){
                               if( (minute + 20) >= 60){
                                   minute = (minute + 20) - 60;
                                   hour = hour + 1;
                                   upadtePreparedOrder(hour, minute, day , date);
                               }else{
                                   upadtePreparedOrder(hour, (minute + 20), day , date);
                               }

                           }
                       }else {
                           orderPreparedRadioButton.setChecked(true);
                           orderPreparedTextView.setText(prepared);
                           if(deliverd.isEmpty()){
                               orderDeliverdTextView.setText(delevering);
                               if(checkTime(hour, minute, 59)){
                                   if( (minute + 59) >= 60){
                                       minute = (minute + 59) - 60;
                                       hour = hour + 1;
                                       updateDeliverdOrder(hour, minute, day , date);
                                   }else{
                                       updateDeliverdOrder(hour, (minute + 59), day , date);
                                   }

                               }
                           }else {
                               orderDeliverdRadioButton.setChecked(true);
                               orderDeliverdTextView.setText(deliverd);
                           }
                       }
                   }


                } else {
                    Log.d("document Not Found", "No such document");
                }

            } else {
                Log.d("error", "get failed with ", task1.getException());
            }
        }).addOnFailureListener(e -> Log.d("error", e.toString()));
    }

    public boolean checkTime(int hour, int minute, int time){
        boolean validateTime = false;
        Calendar rightNow = Calendar.getInstance();
        int curruntHour = rightNow.get(Calendar.HOUR_OF_DAY);
        int curruntMinute = rightNow.get(Calendar.MINUTE);

        if(curruntHour > hour){
            curruntMinute = curruntMinute + 60;
            if(curruntMinute >=  (minute + time)){
                validateTime = true;
            }
        }else {
            if(curruntMinute >=  (minute + time)){
                validateTime = true;
            }
        }
        return validateTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateConfirmOrder(int hour, int mint, String day, String date){
        String time = hour+":"+mint+", "+day+", "+date;
        firestore.collection("users").document(uId).collection("order").document(orderId)
                .update("confirmed", time)
                .addOnSuccessListener(unused -> setData()).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void upadtePreparedOrder(int hour, int mint, String day, String date){
        String time = hour+":"+mint+", "+day+", "+date;
        firestore.collection("users").document(uId).collection("order").document(orderId)
                .update("prepared", time)
                .addOnSuccessListener(unused -> setData()).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateDeliverdOrder(int hour, int mint, String day, String date){
        String time = hour+":"+mint+", "+day+", "+date;
        firestore.collection("users").document(uId).collection("order").document(orderId)
                .update("deliverd", time)
                .addOnSuccessListener(unused -> setData()).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, OrdersActivity.class);
        startActivity(intent);
    }
}