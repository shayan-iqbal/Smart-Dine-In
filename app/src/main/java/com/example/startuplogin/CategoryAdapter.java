package com.example.startuplogin;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.startuplogin.RestaurantDetail.catItemListRec;
import static com.example.startuplogin.RestaurantDetail.restId;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    Context context;
    ArrayList<String> catList;
    ArrayList<Item> catItemList = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference restRef = database.getReference("Restaurant");
    private int row_index = -1;
    int pos = 0;

    public CategoryAdapter(Context context, ArrayList<String> catList) {
        this.context = context;
        this.catList = catList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item, parent, false);
        CategoryViewHolder viewHolder = new CategoryViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, final int position) {

        holder.catNameTv.setText(catList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                catItemList.clear();
                row_index = position;
                notifyDataSetChanged();

                final String selectedCategory = catList.get(position);
                restRef.child(restId).child("Item").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Item item = dataSnapshot.getValue(Item.class);
                                String category = item.getItemCategory();
                                if (category.equals(selectedCategory)) {
                                    catItemList.add(item);
                                }
                            }
                            Log.e("category list ", catItemList.toString());

                            CatItemAdapter catItemAdapter = new CatItemAdapter(context, catItemList);
                            catItemListRec.setLayoutManager(new LinearLayoutManager(context));
                            catItemListRec.setNestedScrollingEnabled(true);
                            catItemListRec.setAdapter(catItemAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        if (pos == 0) {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            holder.catNameTv.setTextColor(Color.parseColor("#ffffff"));
            pos++;
        } else if (row_index == position) {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            holder.catNameTv.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.cardView.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.catNameTv.setTextColor(Color.parseColor("#000000"));
        }


    }


    @Override
    public int getItemCount() {
        return catList.size();
    }

    public interface OnItemClickListener {
        public void onClick(View view, int position);
    }
}
