package com.flora.flora;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final ArrayList<String> orderName;
    private final ArrayList<String> orderAddress;
    private final ArrayList<String> orderid;

    public OrderAdapter(ArrayList<String> orderName, ArrayList<String> orderAddress, ArrayList<String> orderid) {
        this.orderName = orderName;
        this.orderAddress = orderAddress;
        this.orderid = orderid;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_card, parent, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.nameTextView.setText(orderName.get(position));
        holder.addressTextView.setText(orderAddress.get(position));

        holder.orderCard.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), OrderTrackActivity.class);
            intent.putExtra("orderid", orderid.get(position));
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderid.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView;
        CardView orderCard;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.order_name);
            addressTextView = itemView.findViewById(R.id.order_address);
            orderCard = itemView.findViewById(R.id.order_card);
        }
    }
}
