package com.flora.flora;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class HomePageCardRecyclerAdapter extends RecyclerView.Adapter<HomePageCardRecyclerAdapter.MyViewHolder> {

    private ArrayList<ProductData> productItemData;

    public HomePageCardRecyclerAdapter(ArrayList<ProductData> productItemData) {
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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String itemName = productItemData.get(position).getName();
        String itemPrice = "$"+productItemData.get(position).getPrice().toString();
        //Drawable itemImage = productItemData.get(position).getItemImage();

        Picasso.get()
                .load(productItemData.get(position).getImage())
                .placeholder(R.drawable.gift)
                .error(R.drawable.flora)
                .into(holder.itemImage);
        holder.itemName.setText(itemName);
        holder.itemPrice.setText(itemPrice);
        //holder.itemImage.setImageDrawable(itemImage);
    }

    @Override
    public int getItemCount() {
        return productItemData.size();
    }
}

