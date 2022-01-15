package com.flora.flora.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flora.flora.HomePageCardRecyclerAdapter;
import com.flora.flora.ProductData;
import com.flora.flora.ProductItemData;
import com.flora.flora.R;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ArrayList<ProductData> productItemData = new ArrayList<>();
    private RecyclerView favoritePageRecyclerView;

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

        favoritePageRecyclerView = view.findViewById(R.id.favorite_recyclerView);

        setProductsInfo();
        setAdapter();

        return view;
    }

    private void setAdapter() {
        HomePageCardRecyclerAdapter adapter = new HomePageCardRecyclerAdapter(productItemData);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        favoritePageRecyclerView.setLayoutManager(layoutManager);
        favoritePageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        favoritePageRecyclerView.setAdapter(adapter);
    }

    private void setProductsInfo() {
        for(int i = 0; i < 10; i++ ){
            productItemData.add(new ProductData("Flower "+i, "Flower", "150$", "flower", 150.0, "https://firebasestorage.googleapis.com/v0/b/flora-giftdeliveryapp.appspot.com/o/witherroses.jpg?alt=media&token=d6f6a5e4-ebb5-49ab-8386-d071ace41a18"));
        }
    }
}