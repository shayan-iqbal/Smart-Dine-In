package com.example.startuplogin;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView itemImageIm;
    public TextView itemNameTv;
    public TextView itemPrice;
    ImageView moreOptionMenuIv;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        itemImageIm = itemView.findViewById(R.id.itemImage);
        itemNameTv = itemView.findViewById(R.id.itemName);
        itemPrice = itemView.findViewById(R.id.itemPrice);
        moreOptionMenuIv = itemView.findViewById(R.id.more_option);
    }
}
