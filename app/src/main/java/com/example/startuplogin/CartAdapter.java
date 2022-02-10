package com.example.startuplogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startuplogin.DB.AppDatabase;
import com.example.startuplogin.DB.Cart;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    List<Cart> cartList;
    Context context;
    AppDatabase db;

    public CartAdapter(List<Cart> cartList, Context context) {
        this.cartList = cartList;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        CartViewHolder viewHolder = new CartViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {
        db = AppDatabase.getDbInstance(context);
        final Cart cart = cartList.get(position);

        holder.cartItemImage.setImageResource(R.drawable.deal3);
        holder.cartItemName.setText(cart.getItemName());
        holder.cartItemVariation.setText(cart.getMeat() + "," + cart.getFries() + "," + cart.getDrink());
        holder.cartItemPrice.setText("Rs. "+cart.getItemPrice());
        holder.quantityTv.setText(cart.getQuantity());

        holder.plusIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(holder.quantityTv.getText().toString());
                quantity++;
                holder.quantityTv.setText(String.valueOf(quantity));
                AppDatabase db = AppDatabase.getDbInstance(context);
                db.cartDao().updateCartItem(String.valueOf(quantity), cart.getItemId());
                CartDetails.setAmount();
            }
        });
        holder.minusIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(holder.quantityTv.getText().toString());
                if (quantity > 1) {
                    quantity--;
                    holder.quantityTv.setText(String.valueOf(quantity));
                    AppDatabase db = AppDatabase.getDbInstance(context);
                    db.cartDao().updateCartItem(String.valueOf(quantity), cart.getItemId());
                    CartDetails.setAmount();
                }
            }
        });
        holder.deleteCartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.cartDao().deleteCartItem(cart.getItemId());
                CartDetails.setAmount();
                cartList.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
                notifyItemRangeChanged(position, cartList.size());

                if(cartList.isEmpty()){
                    CartDetails.hideAmountTv();
                    Intent restDetail=new Intent(context,RestaurantDetail.class);
                    restDetail.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(restDetail);
                    ((Activity) context).finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }
}
