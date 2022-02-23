package com.example.startuplogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startuplogin.DB.AppDatabase;
import com.example.startuplogin.DB.Cart;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RestaurantDetail extends AppCompatActivity {

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
    String currentUId;
    BottomNavigationView bottomNavigationView;
    Button viewCartBtn;
    TextView tableSeat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        getSupportActionBar().hide();

        init();

        checkCart();
        getBundle();
        getItems();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkCart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkCart();
    }

    private void checkCart() {

        AppDatabase appDatabase = AppDatabase.getDbInstance(this);
        List<Cart> cartList = appDatabase.cartDao().getAllItemsOfCurrentUser(currentUId);
        if (!cartList.isEmpty()) {
            bottomNavigationView.setVisibility(View.VISIBLE);

            viewCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent cartDetails = new Intent(RestaurantDetail.this, CartDetails.class);
                    startActivity(cartDetails);
                    //finish();
                }
            });
        }
        Log.e("user id ", currentUId);
        Log.e("cart list ", cartList.toString());

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
        categoryAdapter = new CategoryAdapter(RestaurantDetail.this, catList);
        catListRec.hasFixedSize();
        catListRec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        catListRec.setAdapter(categoryAdapter);

        getCatItemsDefault(catList);

    }

    private void getItems() {


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
        //restId = bundle.getString("restId");
        restId=RestaurantAdapter.restId;
        String seat=bundle.getString("seats");
        getRestaurantDetail(RestaurantAdapter.restId,seat);
    }

    private void getRestaurantDetail(String restId, final String seat) {

        restRef.child(RestaurantAdapter.restId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("restName").getValue().toString();
                String type = snapshot.child("restType").getValue().toString();
                //String tableCount = snapshot.child("restTableCount").getValue().toString();
                Log.e("name ", name);
                restNameTv.setText(name);
                restTypeTv.setText(type);
                tableSeat.setText(seat);
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
        bottomNavigationView = findViewById(R.id.BNavigation);
        viewCartBtn = findViewById(R.id.viewCartBtn);
        mAuth = FirebaseAuth.getInstance();
        tableSeat=findViewById(R.id.restDetailTable);
        if (mAuth.getCurrentUser() != null)
            currentUId = mAuth.getCurrentUser().getUid();
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
