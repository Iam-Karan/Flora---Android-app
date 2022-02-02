package com.flora.flora.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flora.flora.HomePageCardRecyclerAdapter;
import com.flora.flora.ProductData;
import com.flora.flora.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GiftFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ArrayList<ProductData> productItemData = new ArrayList<>();
    private ArrayList<ProductData> copyItemData = new ArrayList<>();
    private RecyclerView giftPageRecyclerView;
    SearchView giftSearchView;
    FirebaseFirestore firestore;
    HomePageCardRecyclerAdapter adapter;
    public GiftFragment() {
        // Required empty public constructor
    }

    public static GiftFragment newInstance(String param1, String param2) {
        GiftFragment fragment = new GiftFragment();
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
        View view = inflater.inflate(R.layout.fragment_gift, container, false);
        firestore = FirebaseFirestore.getInstance();
        giftPageRecyclerView = view.findViewById(R.id.gift_recyclerView);
        giftSearchView = view.findViewById(R.id.gift_searchView);
        productItemData = new ArrayList<ProductData>();
        setProductsInfo();
        giftSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.filter(s, copyItemData);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.filter(s, copyItemData);
                return true;
            }
        });
        return view;
    }

    private void setAdapter() {
        adapter = new HomePageCardRecyclerAdapter(productItemData);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        giftPageRecyclerView.setLayoutManager(layoutManager);
        giftPageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        giftPageRecyclerView.setAdapter(adapter);
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
                            if(type.equals("flower")  || type.equals("bouquet")){

                            }else {
                                productItemData.add(data);
                            }

                        }
                        copyItemData.addAll(productItemData);
                    } else {
                        Toast.makeText(getContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(getContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> setAdapter());
    }
}