package com.example.startuplogin;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class RestaurantDetail extends AppCompatActivity{

    FirebaseAuth mAuth;
    RecyclerView catListRec;
    static RecyclerView catItemListRec;
    ArrayList<String> catList;
    ArrayList<Item> catItemList;
    CategoryAdapter categoryAdapter;
    CatItemAdapter catItemAdapter;
    DatabaseReference restRef;
    FirebaseDatabase database;
    static String restId;
    TextView restNameTv;
    TextView restTypeTv;
    TextView restTableCountTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        getSupportActionBar().hide();

        init();
        getBundle();
        getItems();
    }

    private void getCatItemsDefault(ArrayList<String> catList) {

        final String selectedCategory = catList.get(0);
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

                    CatItemAdapter catItemAdapter = new CatItemAdapter(RestaurantDetail.this, catItemList);
                    catItemListRec.setLayoutManager(new LinearLayoutManager(RestaurantDetail.this));
                    catItemListRec.setNestedScrollingEnabled(true);
                    catItemListRec.setAdapter(catItemAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setList() {

        Set<String> listWithoutDuplicates = new LinkedHashSet<String>(catList);
        catList.clear();
        catList.addAll(listWithoutDuplicates);
        Log.e("cat list ", catList.toString());
        categoryAdapter = new CategoryAdapter(RestaurantDetail.this, catList );
        catListRec.hasFixedSize();
        catListRec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        catListRec.setAdapter(categoryAdapter);

        getCatItemsDefault(catList);

    }

    private void getItems() {

        Toast.makeText(this, "in get ", Toast.LENGTH_SHORT).show();

        restRef.child(restId).child("Item").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("in method", "");
                if (snapshot.exists()) {
                    Log.e("in if", snapshot.getKey());
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Item item = dataSnapshot.getValue(Item.class);
                        String category = item.getItemCategory();
                        catList.add(category);
                        Log.e("cat name", catList.toString());
                    }
                    setList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getBundle() {

        Bundle bundle = getIntent().getExtras();
        restId = bundle.getString("restId");
        getRestaurantDetail(restId);
    }

    private void getRestaurantDetail(String restId) {

        restRef.child(restId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("restName").getValue().toString();
                String type = snapshot.child("restType").getValue().toString();
                //String tableCount = snapshot.child("restTableCount").getValue().toString();
                Log.e("name ", name);
                restNameTv.setText(name);
                restTypeTv.setText(type);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void init() {

        catListRec = findViewById(R.id.catRecV);
        catItemListRec = findViewById(R.id.catItemRecV);
        catList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        restRef = database.getReference("Restaurant");
        restNameTv = findViewById(R.id.restDetailName);
        restTypeTv = findViewById(R.id.restDetailType);
        restTableCountTv = findViewById(R.id.restDetailTable);
        catItemList = new ArrayList<>();

    }


//    public void onClick(View view, int position) {
////        catItemList.clear();
////
////        final String selectedCategory = catList.get(position);
////        restRef.child(restId).child("Item").addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////
////                if (snapshot.exists()) {
////
////                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
////                        Item item = dataSnapshot.getValue(Item.class);
////                        String category = item.getItemCategory();
////                        if (category.equals(selectedCategory)) {
////                            catItemList.add(item);
////                        }
////                    }
////                    Log.e("category list ", catItemList.toString());
////
////                    catItemAdapter = new CatItemAdapter(RestaurantDetail.this, catItemList);
////                    catItemListRec.setLayoutManager(new LinearLayoutManager(RestaurantDetail.this));
////                    catItemListRec.setNestedScrollingEnabled(true);
////                    catItemListRec.setAdapter(catItemAdapter);
////
////                }
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////
////            }
////        });
//   }

}
