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
import com.flora.flora.ProductItemData;
import com.flora.flora.R;

import java.util.ArrayList;

public class FlowerFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ArrayList<ProductItemData> productItemData = new ArrayList<>();
    private RecyclerView flowerPageRecyclerView;

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

        flowerPageRecyclerView = view.findViewById(R.id.flower_recyclerView);

        setProductsInfo();
        setAdapter();

        return view;
    }

    private void setAdapter() {
        HomePageCardRecyclerAdapter adapter = new HomePageCardRecyclerAdapter(productItemData);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        flowerPageRecyclerView.setLayoutManager(layoutManager);
        flowerPageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        flowerPageRecyclerView.setAdapter(adapter);
    }

    private void setProductsInfo() {
        for(int i = 0; i < 10; i++ ){
            productItemData.add(new ProductItemData("Flower "+i, "150$"));
        }
    }
}