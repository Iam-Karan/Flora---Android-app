package com.flora.flora;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BoquetRecyclerViewAdapter extends RecyclerView.Adapter<BoquetRecyclerViewAdapter.BoquetViewHolder> {

    private ArrayList<CartProductData> productItemData;
    int flowerCount = 0;

    public BoquetRecyclerViewAdapter(ArrayList<CartProductData> productItemData) {
        this.productItemData = productItemData;
    }

    public class BoquetViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, count;
        ImageView image;
        ImageButton add, remove;
        public BoquetViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.flower_name);
            price = itemView.findViewById(R.id.flower_price);
            image = itemView.findViewById(R.id.flower_image);
            add = itemView.findViewById(R.id.flower_add);
            remove = itemView.findViewById(R.id.flower_remove);
            count = itemView.findViewById(R.id.flower_count);
        }
    }

    @NonNull
    @Override
    public BoquetRecyclerViewAdapter.BoquetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.boquet_card, parent, false);
        return new BoquetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BoquetRecyclerViewAdapter.BoquetViewHolder holder, int position) {
        String flowerName = productItemData.get(position).getProductData().getName();
        String flowerPrice = "$ "+productItemData.get(position).getProductData().getPrice();
        String imageUrl = productItemData.get(position).getProductData().getImage();
        flowerCount = Integer.parseInt(productItemData.get(position).getQuantity());
        holder.name.setText(flowerName);
        holder.price.setText(flowerPrice);
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.gift)
                .error(R.drawable.flora)
                .into(holder.image);
        holder.count.setText(""+flowerCount);
        holder.add.setOnClickListener(view -> {
            flowerCount = Integer.parseInt(productItemData.get(position).getQuantity());
            flowerCount++;
            productItemData.get(position).setQuantity(""+flowerCount);
            holder.count.setText(""+flowerCount);
        });

        holder.remove.setOnClickListener(view -> {
            flowerCount = Integer.parseInt(productItemData.get(position).getQuantity());
            if(flowerCount > 0){
                flowerCount--;
                productItemData.get(position).setQuantity(""+flowerCount);
            }
            holder.count.setText(""+flowerCount);
        });
    }

    @Override
    public int getItemCount() {
        return productItemData.size();
    }
}
