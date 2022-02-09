package com.example.startuplogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    ArrayList<Item> items = new ArrayList<>();
    Item item;
    Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public ItemAdapter(ArrayList<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        final Item item = items.get(position);

        //Glide.with(context).load(item.getItemImage()).into(holder.itemImageIm);
        holder.itemImageIm.setImageResource(R.drawable.rest_image);
        holder.itemNameTv.setText(item.getItemName());
        holder.itemPrice.setText(item.getItemPrice());
        holder.moreOptionMenuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.more_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.edit: {
                                showDialog(position, "edit");
                                break;
                            }
                            case R.id.delete: {
                                showDialog(position, "delete");
                                break;
                            }
                        }
                        return true;
                    }
                });

            }
        });

    }

    private void showDialog(final int position, final String option) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (option.equals("edit")) {
            builder.setMessage("Are you sure,you want to Edit Item");
        } else {
            builder.setMessage("Are you sure,you want to Delete Item");
        }
        builder.setTitle("Warning");
        builder.setIcon(R.drawable.ic_warning);
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (option.equals("edit")) {
                    makeBundle(position);
                } else if (option.equals("delete")) {
                    deleteItem(position);
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem(int position) {
        item = items.get(position);
        items.remove(position);
        DatabaseReference itemRef = database.getReference("Restaurant").child(ItemList.currentRestId).child("Item");
        itemRef.child(item.getItemId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
    }

    private void makeBundle(int position) {
        item = items.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("id", item.getItemId());
        bundle.putString("name", item.getItemName());
        bundle.putString("category", item.getItemCategory());
        bundle.putString("price", item.getItemPrice());
        bundle.putString("desc", item.getItemDesc());
        Intent intent = new Intent(context, AddItem.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ((Activity)context).finish();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setFilter(ArrayList<Item> filter) {
        items = new ArrayList<>();
        items.addAll(filter);
        notifyDataSetChanged();
    }
}
