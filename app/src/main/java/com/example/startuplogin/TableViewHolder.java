package com.example.startuplogin;

import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TableViewHolder extends RecyclerView.ViewHolder {

    TextView tableNumTv;
    TextView tableSeatTv;
    static Spinner tableStatusSp;
    ImageView tableDelete;
    ImageView tableOrder;

    public TableViewHolder(@NonNull View itemView) {
        super(itemView);

        tableNumTv=itemView.findViewById(R.id.tableNum);
        tableSeatTv=itemView.findViewById(R.id.tableSeats);
        tableStatusSp=itemView.findViewById(R.id.tableSeatSp);
        tableDelete=itemView.findViewById(R.id.tableDelete);
        tableOrder=itemView.findViewById(R.id.viewTableOrder);

    }
}
