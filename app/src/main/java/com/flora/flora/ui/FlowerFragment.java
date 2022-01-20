package com.flora.flora.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flora.flora.CustomizeBoquetActivity;
import com.flora.flora.HomePageCardRecyclerAdapter;
import com.flora.flora.ProductData;
import com.flora.flora.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FlowerFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private final ArrayList<ProductData> productItemData = new ArrayList<>();
    private RecyclerView flowerPageRecyclerView;
    HomePageCardRecyclerAdapter adapter;
    private AppCompatButton costomizeBoquetButton;

    FirebaseFirestore firestore;
    public FlowerFragment() {
        // Required empty public constructor
    }

    public static FlowerFragment newInstance(String param1, String param2) {
        FlowerFragment fragment = new FlowerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flower, container, false);
        firestore = FirebaseFirestore.getInstance();
        flowerPageRecyclerView = view.findViewById(R.id.flower_recyclerView);
        costomizeBoquetButton = view.findViewById(R.id.customize_boquet_btn);
        setProductsInfo();
        setAdapter();

        costomizeBoquetButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), CustomizeBoquetActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void setAdapter() {
        adapter = new HomePageCardRecyclerAdapter(productItemData);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        flowerPageRecyclerView.setLayoutManager(layoutManager);
        flowerPageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        flowerPageRecyclerView.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setProductsInfo() {
        firestore.collection("products").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {

                            ProductData data = d.toObject(ProductData.class);
                            assert data != null;
                            String type = data.getType();
                            if(type.equals("bouquet")){
                                productItemData.add(data);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(getContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show());
    }
}