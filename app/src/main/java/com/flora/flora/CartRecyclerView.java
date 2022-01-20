package com.flora.flora;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartRecyclerView extends RecyclerView.Adapter<CartRecyclerView.CartViewHolder> {

    private final ArrayList<CartProductData> cartProductData;
    FirebaseFirestore firestore;
    String uId;

    public CartRecyclerView(ArrayList<CartProductData> cartProductData) {
        this.cartProductData = cartProductData;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        assert mFirebaseUser != null;
        uId = mFirebaseUser.getUid();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder{
        private final TextView name;
        private final TextView price;
        private final TextView quantity;
        private final ImageView imageView;
        private final AppCompatButton edit, remove;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.cart_product_name);
            price = itemView.findViewById(R.id.cart_product_price);
            quantity = itemView.findViewById(R.id.cart_product_quantity);
            imageView = itemView.findViewById(R.id.cart_product_image);
            edit = itemView.findViewById(R.id.cart_product_edit);
            remove = itemView.findViewById(R.id.cart_product_remove);
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String itemName = cartProductData.get(position).getProductData().getName();
        String itemPrice = "$"+cartProductData.get(position).getProductData().getPrice().toString();
        String imageUrl = cartProductData.get(position).getProductData().getImage();
        String itemQuantity = "Quantity : "+cartProductData.get(position).getQuantity();

        holder.name.setText(itemName);
        holder.price.setText(itemPrice);
        holder.quantity.setText(itemQuantity);
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.gift)
                .error(R.drawable.flora)
                .into(holder.imageView);

        holder.edit.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ProductDetailScreen.class);
            intent.putExtra("ProductId", cartProductData.get(position).getProductData().getId());
            view.getContext().startActivity(intent);
        });
        holder.remove.setOnClickListener(view -> removeItemCart(cartProductData.get(position).getProductData().getId(), view.getContext()));

    }

    @Override
    public int getItemCount() {
        return cartProductData.size();
    }

    public void removeItemCart(String prductId, Context context){
        firestore.collection("users").document(uId).collection("cart").document(prductId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, "Item removed From Favourite successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, CartScreen.class);
                    context.startActivity(intent);
                })
                .addOnFailureListener(e -> Log.w("error", e.toString()));
    }


}
