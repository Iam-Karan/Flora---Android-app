package com.flora.flora;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomePageCardRecyclerAdapter extends RecyclerView.Adapter<HomePageCardRecyclerAdapter.MyViewHolder> {

    private ArrayList<ProductItemData> productItemData;

    public HomePageCardRecyclerAdapter(ArrayList<ProductItemData> productItemData) {
        this.productItemData = productItemData;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView itemImage;
        private TextView itemName;
        private TextView itemPrice;

        public MyViewHolder(@NonNull View view) {
            super(view);

            itemImage = view.findViewById(R.id.homepage_card_image);
            itemName = view.findViewById(R.id.homepage_card_item_name);
            itemPrice = view.findViewById(R.id.homepage_card_item_price);
        }
    }

    @NonNull
    @Override
    public HomePageCardRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_card, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parent.getContext(), ProductDetailScreen.class);
                parent.getContext().startActivity(intent);
            }
        });
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomePageCardRecyclerAdapter.MyViewHolder holder, int position) {
        String itemName = productItemData.get(position).getItemName();
        String itemPrice = productItemData.get(position).getItemPrice();
        //Drawable itemImage = productItemData.get(position).getItemImage();

        holder.itemName.setText(itemName);
        holder.itemPrice.setText(itemPrice);
        //holder.itemImage.setImageDrawable(itemImage);
    }

    @Override
    public int getItemCount() {
        return productItemData.size();
    }
}

