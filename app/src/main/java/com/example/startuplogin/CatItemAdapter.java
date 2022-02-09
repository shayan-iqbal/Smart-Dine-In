package com.example.startuplogin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CatItemAdapter extends RecyclerView.Adapter<CatItemViewHolder> {

    Context context;
    ArrayList<Item> items;

    public CatItemAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public CatItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_list, parent, false);
        CatItemViewHolder viewHolder = new CatItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CatItemViewHolder holder, int position) {

        final Item item = items.get(position);

        holder.catItemNameTv.setText(item.getItemName());
        holder.catItemDesc.setText(item.getItemDesc());
        holder.catItemPriceTv.setText("Rs. " + item.getItemPrice());
        holder.catItemIV.setImageResource(R.drawable.deal2);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent itemDetailIntent = new Intent(context, ItemDetail.class);
                itemDetailIntent.putExtra("itemBundle", item);
                context.startActivity(itemDetailIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
