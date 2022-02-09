package com.example.startuplogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startuplogin.DB.AppDatabase;
import com.example.startuplogin.DB.Cart;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RestaurantList extends AppCompatActivity implements SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {

    ArrayList<Restaurant> restaurants;
    RecyclerView restListRv;
    RestaurantAdapter restAdapter;
    FirebaseAuth mAuth;
    FirebaseDatabase restDatabase;
    DatabaseReference restRef;
    TextView noRestTv;
    Button listAddRest;
    FloatingActionButton listFloatAddBtn;
    ProgressDialog startDialog;
    String currentUEmail;
    String currentUId;
    BottomNavigationView bottomNavigationView;
    Button viewCartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        init();
        checkCart();

        currentUEmail = mAuth.getCurrentUser().getEmail();

        restRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.hasChildren()) {
                    startDialog.dismiss();
                    restListRv.setVisibility(View.INVISIBLE);
                    noRestTv.setVisibility(View.VISIBLE);
                    listAddRest.setVisibility(View.VISIBLE);
                    listFloatAddBtn.setVisibility(View.INVISIBLE);

                    listAddRest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent addRestInt = new Intent(RestaurantList.this, AddRestaurant.class);
                            startActivity(addRestInt);
                            finish();
                        }
                    });
                }
//                else {
//                    restListRv.setVisibility(View.VISIBLE);
//                    noRestTv.setVisibility(View.INVISIBLE);
//                    listAddRest.setVisibility(View.INVISIBLE);
//                    listFloatAddBtn.setVisibility(View.VISIBLE);
//
//                    listFloatAddBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent addRestInt = new Intent(RestaurantList.this, AddRestaurant.class);
//                            startActivity(addRestInt);
//                            finish();
//                        }
//                    });
//
//                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
//                    if (restaurant != null) {
//                        startDialog.dismiss();
//                        Log.e("snap",snapshot.getValue().toString());
//                    }
//
//                    restaurants.add(restaurant);
//                    restAdapter = new RestaurantAdapter(restaurants, RestaurantList.this);
//                    restListRv.hasFixedSize();
//                    restListRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                    restListRv.setAdapter(restAdapter);
//                    restAdapter.notifyDataSetChanged();
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        restRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (snapshot.hasChildren()) {
                    // startDialog.show();
                    String key = snapshot.getKey();
                    Log.e("key", key);
                    restListRv.setVisibility(View.VISIBLE);
                    noRestTv.setVisibility(View.INVISIBLE);
                    listAddRest.setVisibility(View.INVISIBLE);

                    if (!currentUEmail.equals("smartadmin@gmail.com")) {
                        listFloatAddBtn.setVisibility(View.INVISIBLE);
                    } else

                        listFloatAddBtn.setVisibility(View.VISIBLE);

                    listFloatAddBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent addRestInt = new Intent(RestaurantList.this, AddRestaurant.class);
                            startActivity(addRestInt);
                        }
                    });

                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
                    if (restaurant != null) {
                        startDialog.dismiss();
                    }
                    Log.e("id", restaurant.getRestId());
                    restaurants.add(restaurant);
                    restAdapter = new RestaurantAdapter(restaurants, RestaurantList.this);
                    restListRv.hasFixedSize();
                    restListRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    restListRv.setAdapter(restAdapter);
                    restAdapter.notifyDataSetChanged();


                } else {
                    startDialog.dismiss();
                    restListRv.setVisibility(View.INVISIBLE);
                    noRestTv.setVisibility(View.VISIBLE);
                    listAddRest.setVisibility(View.VISIBLE);
                    listFloatAddBtn.setVisibility(View.INVISIBLE);

                    listAddRest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent addRestInt = new Intent(RestaurantList.this, AddRestaurant.class);
                            startActivity(addRestInt);
                        }
                    });
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.e("removed", snapshot.getValue().toString());
                if (!snapshot.hasChildren()) {
                    Log.e("r", snapshot.getKey());
                    restListRv.setVisibility(View.INVISIBLE);
                    noRestTv.setVisibility(View.VISIBLE);
                    listAddRest.setVisibility(View.VISIBLE);
                    listFloatAddBtn.setVisibility(View.INVISIBLE);

                    listAddRest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent addRestInt = new Intent(RestaurantList.this, AddRestaurant.class);
                            startActivity(addRestInt);
                        }
                    });
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
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
                    Intent cartDetails = new Intent(RestaurantList.this, CartDetails.class);
                    startActivity(cartDetails);
                }
            });
        }
        Log.e("user id ", currentUId);
        Log.e("cart list ", cartList.toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Restaurant");
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_logout: {
                mAuth.signOut();
                Intent logoutIntent = new Intent(RestaurantList.this, MainActivity.class);
                startActivity(logoutIntent);
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<Restaurant> Search(ArrayList<Restaurant> restList, String query) {
        query = query.toLowerCase();
        final ArrayList<Restaurant> searchList = new ArrayList<>();
        for (Restaurant s : restList) {
            final String name = s.getRestName().toLowerCase();
            if (name.contains(query)) {
                searchList.add(s);
            }
        }
        return searchList;
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final ArrayList<Restaurant> searchList = Search(restaurants, newText);

        if (searchList.size() > 0) {
            restAdapter = new RestaurantAdapter(searchList, RestaurantList.this);
            restListRv.setAdapter(restAdapter);
            restAdapter.setFilter(searchList);
            return true;
        } else {
            Toast.makeText(RestaurantList.this, "No Record Found", Toast.LENGTH_SHORT).show();
            restListRv.setAdapter(null);
            return false;
        }
    }

    private void init() {
        restaurants = new ArrayList<>();
        restListRv = findViewById(R.id.rest_list);
        restDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        restRef = restDatabase.getReference().child("Restaurant");
        noRestTv = findViewById(R.id.list_no_rest_tv);
        listAddRest = findViewById(R.id.list_add_btn);
        listFloatAddBtn = findViewById(R.id.list_float_btn);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        startDialog = new ProgressDialog(this);
        startDialog.setTitle("Please Wait...");
        startDialog.setCancelable(false);
        startDialog.show();
        bottomNavigationView = findViewById(R.id.BNavigation);
        viewCartBtn = findViewById(R.id.viewCartBtn);

        if (mAuth.getCurrentUser() != null)
            currentUId = mAuth.getCurrentUser().getUid();
    }
}
