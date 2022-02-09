package com.example.startuplogin;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CatItemViewHolder extends RecyclerView.ViewHolder {

    ImageView catItemIV;
    TextView catItemNameTv;
    TextView catItemDesc;
    TextView catItemPriceTv;

    public CatItemViewHolder(@NonNull View itemView) {
        super(itemView);

        catItemIV=itemView.findViewById(R.id.catItemImage);
        catItemNameTv=itemView.findViewById(R.id.catItemName);
        catItemDesc=itemView.findViewById(R.id.catItemDesc);
        catItemPriceTv=itemView.findViewById(R.id.catItemPrice);
    }
}
