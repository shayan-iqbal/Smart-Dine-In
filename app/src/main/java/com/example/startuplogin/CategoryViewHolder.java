package com.example.startuplogin;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    TextView catNameTv;
    CardView cardView;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        catNameTv = itemView.findViewById(R.id.catName);
        cardView = itemView.findViewById(R.id.itemCardview);
    }

}
