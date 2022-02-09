package com.example.startuplogin;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class CartViewHolder extends RecyclerView.ViewHolder {

    ImageView cartItemImage;
    TextView cartItemName;
    TextView cartItemVariation;
    TextView cartItemPrice;
    ImageView plusIm;
    ImageView minusIm;
    TextView quantityTv;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        cartItemImage=itemView.findViewById(R.id.cartItemImage);
        cartItemName=itemView.findViewById(R.id.cartItemName);
        cartItemVariation=itemView.findViewById(R.id.cartItemVariation);
        cartItemPrice=itemView.findViewById(R.id.cartItemPrice);
        plusIm=itemView.findViewById(R.id.plusQuantity);
        minusIm=itemView.findViewById(R.id.minusQuantity);
        quantityTv=itemView.findViewById(R.id.quantityTv);
    }
}
