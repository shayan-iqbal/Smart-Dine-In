package com.example.startuplogin;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    public ImageView restImageIm;
    public TextView restNameTv;
    public TextView restTypeTv;
    ImageView moreOptionMenuIv;
    ImageView tableIconIv;
    static TextView seatAvailable;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);

        restImageIm = itemView.findViewById(R.id.restImage);
        restNameTv = itemView.findViewById(R.id.restName);
        restTypeTv = itemView.findViewById(R.id.restType);
        moreOptionMenuIv = itemView.findViewById(R.id.more_option);
        tableIconIv = itemView.findViewById(R.id.seatIcon);
        seatAvailable = itemView.findViewById(R.id.seatAvailable);
    }
}
