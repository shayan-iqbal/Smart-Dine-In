package com.example.startuplogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    ArrayList<Restaurant> restaurants = new ArrayList<>();
    Restaurant restaurant;
    Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference restRef = database.getReference("Restaurant");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static String restId;
    boolean check;
    int max;
    int max1;
    private RecyclerView mRecyclerView;

    public RestaurantAdapter(ArrayList<Restaurant> restaurants, Context context) {
        this.restaurants = restaurants;
        this.context = context;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rest_item, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onBindViewHolder(@NonNull final RestaurantViewHolder holder, final int position) {
        final Restaurant restaurant = restaurants.get(position);

        final String currentUId = mAuth.getCurrentUser().getEmail();
        restId = restaurant.getRestId();

        restRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                        String email = restaurant.getRestEmail();
                        if (email != null)
                            if (email.equals(currentUId)) {
                                check = true;
                                break;
                            }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (MainActivity.userType != null) {

            if (MainActivity.userType.equals("admin")) {

                holder.restImageIm.setImageResource(R.drawable.rest_image);
                holder.restNameTv.setText(restaurant.getRestName());
                holder.restTypeTv.setText(restaurant.getRestType());
                holder.moreOptionMenuIv.setVisibility(View.VISIBLE);

            } else if (MainActivity.userType.equals("manager")) {

                if(restaurant.getRestEmail().equals(currentUId)) {

                    holder.restImageIm.setImageResource(R.drawable.rest_image);
                    holder.restNameTv.setText(restaurant.getRestName());
                    holder.restTypeTv.setText(restaurant.getRestType());
                    holder.moreOptionMenuIv.setVisibility(View.INVISIBLE);
                    holder.seatAvailable.setVisibility(View.VISIBLE);
                    holder.tableIconIv.setVisibility(View.VISIBLE);
                    holder.seatAvailable.setText(String.valueOf(max1));

                    countSeats(position, restId);
                    Log.e("position rest ", String.valueOf(position));
                }
                else{
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }

            } else if (MainActivity.userType.equals("user")) {

                holder.restImageIm.setImageResource(R.drawable.rest_image);
                holder.restNameTv.setText(restaurant.getRestName());
                holder.restTypeTv.setText(restaurant.getRestType());
                holder.moreOptionMenuIv.setVisibility(View.INVISIBLE);
                holder.seatAvailable.setVisibility(View.VISIBLE);
                holder.tableIconIv.setVisibility(View.VISIBLE);
                holder.seatAvailable.setText(String.valueOf(max1));

                countSeats(position, restId);
                Log.e("position rest ", String.valueOf(position));

            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (RestaurantList.loginType != null) {
                    if (RestaurantList.loginType.equals("manager")) {
                        Intent itemList = new Intent(context, ItemList.class);
                        context.startActivity(itemList);
                    }
                } else if (!currentUId.equals("smartadmin@gmail.com")) {
                    Intent restDetailIntent = new Intent(context, RestaurantDetail.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("restId", restaurant.getRestId());
                    bundle.putString("seats", String.valueOf(max));
                    bundle.putString("bundle", "detail");
                    restDetailIntent.putExtras(bundle);
                    context.startActivity(restDetailIntent);
                }
            }
        });

//        if (!check) {
//
//            holder.restImageIm.setImageResource(R.drawable.rest_image);
//            holder.restNameTv.setText(restaurant.getRestName());
//            holder.restTypeTv.setText(restaurant.getRestType());
//            holder.seatAvailable.setText(String.valueOf(max1));
//
////            if (RestaurantList.loginType != null) {
////                if (RestaurantList.loginType.equals("manager")) {
////                    if (!(restaurant.getRestEmail().equals(currentUId))) {
////
////                        mRecyclerView.post(new Runnable() {
////                            @Override
////                            public void run() {
////                               // holder.itemView.setVisibility(View.GONE);
////                                restaurants.remove(position);
////                                notifyItemRemoved(position);
////                                notifyItemRangeChanged(position,restaurants.size());
////                                notifyItemChanged(position);
////                            }
////                        });
////                    }
////                }
////            }
//
//        } else {
//
//            holder.restImageIm.setImageResource(R.drawable.rest_image);
//            holder.restNameTv.setText(restaurant.getRestName());
//            holder.restTypeTv.setText(restaurant.getRestType());
//            holder.seatAvailable.setText(String.valueOf(max1));
//        }

//        if ((currentUId.equals("smartadmin@gmail.com"))) {
//            holder.moreOptionMenuIv.setVisibility(View.VISIBLE);
//        } else {
//            holder.moreOptionMenuIv.setVisibility(View.INVISIBLE);
//            if (MainActivity.userType != null) {
//                if ((!MainActivity.userType.equals("manager"))) {
//                    holder.seatAvailable.setVisibility(View.VISIBLE);
//                    holder.tableIconIv.setVisibility(View.VISIBLE);
//
//                    countSeats(position, restId);
//                    Log.e("position rest ", String.valueOf(position));
//
//                }
//            }
//
//        }

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

    private void countSeats(final int position, final String restId) {
        restRef.child(restId).child("Table").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                max = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int seat = Integer.parseInt(dataSnapshot.child("tableSeat").getValue().toString());
                    String status = dataSnapshot.child("tableStatus").getValue().toString();
                    Log.e("rst id ", restId);
                    Log.e("seat ", String.valueOf(seat));
                    Log.e("status ", status);
                    if (seat > max && status.equals("Free")) {
                        max = seat;

                    }

                }
                getmax(max, position);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private int getmax(int max, int position) {

        max1 = max;
        Log.e("max method ", String.valueOf(max1));
        Log.e("max method position  ", String.valueOf(position));
        View view = RestaurantList.restListRv.findViewHolderForAdapterPosition(position).itemView;
        TextView seat = view.findViewById(R.id.seatAvailable);
        seat.setText(String.valueOf(max1));
        max1 = 0;
        return max1;
    }

    private void showDialog(final int position, final String option) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (option.equals("edit")) {
            builder.setMessage("Are you sure,you want to Edit restaurant");
        } else {
            builder.setMessage("Are you sure,you want to Delete restaurant");
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
                    deleteRest(position);
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

    private void deleteRest(int position) {
        restaurant = restaurants.get(position);
        restaurants.remove(position);
        restRef.child(restaurant.getRestId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Restaurant Deleted", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        notifyItemRemoved(position);
        notifyDataSetChanged();
        notifyItemRangeChanged(position, restaurants.size());
    }

    private void makeBundle(int position) {
        restaurant = restaurants.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("id", restaurant.getRestId());
        bundle.putString("name", restaurant.getRestName());
        bundle.putString("email", restaurant.getRestEmail());
        bundle.putString("contact", restaurant.getRestContact());
        bundle.putString("location", restaurant.getRestLocation());
        bundle.putString("type", restaurant.getRestType());
        bundle.putString("branch_code", restaurant.getRestBranchCode());
        bundle.putString("image", restaurant.getRestImage());
        bundle.putString("pass", restaurant.getRestPasscode());
        Intent intent = new Intent(context, AddRestaurant.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void setFilter(ArrayList<Restaurant> filter) {
        restaurants = new ArrayList<>();
        restaurants.addAll(filter);
        notifyDataSetChanged();
    }
}
