package com.flora.flora.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flora.flora.HomePageCardRecyclerAdapter;
import com.flora.flora.ProductData;
import com.flora.flora.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavoriteFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private final ArrayList<ProductData> productItemData = new ArrayList<>();
    private RecyclerView favoritePageRecyclerView;
    private HomePageCardRecyclerAdapter adapter;
    private LinearLayout favouriteList, noItemFound;
    FirebaseFirestore firestore;
    String uId;

    private final ArrayList<String> productId = new ArrayList<>();
    public FavoriteFragment() {
        // Required empty public constructor
    }


    public static FavoriteFragment newInstance(String param1, String param2) {
        FavoriteFragment fragment = new FavoriteFragment();
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
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        favoritePageRecyclerView = view.findViewById(R.id.favorite_recyclerView);
        favouriteList = view.findViewById(R.id.favorite_items);
        noItemFound = view.findViewById(R.id.no_item_found);
        assert mFirebaseUser != null;
        uId = mFirebaseUser.getUid();

        setProductsInfo();
        setAdapter();

        return view;
    }

    private void setAdapter() {
        adapter = new HomePageCardRecyclerAdapter(productItemData);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        favoritePageRecyclerView.setLayoutManager(layoutManager);
        favoritePageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        favoritePageRecyclerView.setAdapter(adapter);
    }

    private void setProductsInfo() {
        firestore.collection("users").document(uId).collection("favourite").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(Objects.requireNonNull(task.getResult()).size() > 0) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    productId.add(document.getId());
                                    favouriteList.setVisibility(View.VISIBLE);
                                    noItemFound.setVisibility(View.GONE);
                                }
                                else {
                                    noItemFound.setVisibility(View.VISIBLE);
                                    favouriteList.setVisibility(View.GONE);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "Task Fails to get Favourite products", Toast.LENGTH_SHORT).show();
                    }
                });
        getProducts();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getProducts(){
        firestore.collection("products").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            ProductData data = d.toObject(ProductData.class);
                            assert data != null;
                            String id = data.getId();
                            if(productId.contains(id)){
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