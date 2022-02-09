package com.example.startuplogin;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.HashMap;

public class ItemList extends AppCompatActivity implements SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {

    ArrayList<Item> items;
    RecyclerView itemListRv;
    ItemAdapter itemAdapter;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference restRef;
    DatabaseReference itemRef;
    TextView noItemTv;
    Button listAddItem;
    FloatingActionButton listFloatAddItem;
    static String currentRestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        init();

        getCurrentUserRest();

    }

    private void getCurrentUserRest() {
        if (mAuth.getCurrentUser() != null) {
            final String loginUserId = mAuth.getCurrentUser().getEmail();
            restRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                            String email = restaurant.getRestEmail();
                            if (email.equals(loginUserId)) {
                                currentRestId = restaurant.getRestId();
                                Log.e("rest id", currentRestId);
                                if (!snapshot.hasChild("Item")) {
                                   showItem();
                                }
                                //
                                updateItemList();
                                break;
                            }
                            Log.e("email", email);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void showItem() {

        itemRef.child(currentRestId).child("Item").child("Category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChildren()) {
                    Log.e("r", snapshot.getKey());
                    itemListRv.setVisibility(View.INVISIBLE);
                    noItemTv.setVisibility(View.VISIBLE);
                    listAddItem.setVisibility(View.VISIBLE);
                    listFloatAddItem.setVisibility(View.INVISIBLE);

                    listAddItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent addRestInt = new Intent(ItemList.this, AddItem.class);
                            startActivity(addRestInt);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateItemList() {

        itemRef.child(currentRestId).child("Item").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.hasChildren()) {

                    String catName=snapshot.getValue().toString();
                    String catKey=snapshot.child(catName).getKey();

                    Log.e("add child if cat name",catName);
                    Log.e("add child if cat key",catKey);

                    itemListRv.setVisibility(View.VISIBLE);
                    noItemTv.setVisibility(View.INVISIBLE);
                    listAddItem.setVisibility(View.INVISIBLE);
                    listFloatAddItem.setVisibility(View.VISIBLE);

                    listFloatAddItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent addItemIntent = new Intent(ItemList.this, AddItem.class);
                            startActivity(addItemIntent);
                            finish();
                        }
                    });

                    Item item = snapshot.getValue(Item.class);

                    items.add(item);
                    itemAdapter = new ItemAdapter(items, ItemList.this);
                    itemListRv.hasFixedSize();
                    itemListRv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                    itemListRv.setAdapter(itemAdapter);

                } else {
                    Log.e("add child else ",snapshot.getValue().toString());
                    itemListRv.setVisibility(View.INVISIBLE);
                    noItemTv.setVisibility(View.VISIBLE);
                    listAddItem.setVisibility(View.VISIBLE);
                    listFloatAddItem.setVisibility(View.INVISIBLE);

                    listAddItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent addItemIntent = new Intent(ItemList.this, AddItem.class);
                            startActivity(addItemIntent);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChildren()) {
                    Log.e("r", snapshot.getKey());
                    itemListRv.setVisibility(View.INVISIBLE);
                    noItemTv.setVisibility(View.VISIBLE);
                    listAddItem.setVisibility(View.VISIBLE);
                    listFloatAddItem.setVisibility(View.INVISIBLE);

                    listAddItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent addRestInt = new Intent(ItemList.this, AddItem.class);
                            startActivity(addRestInt);
                            finish();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Item");
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_logout: {
                mAuth.signOut();
                Intent logoutIntent = new Intent(ItemList.this, MainActivity.class);
                startActivity(logoutIntent);
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<Item> Search(ArrayList<Item> itemList, String query) {
        query = query.toLowerCase();
        final ArrayList<Item> searchList = new ArrayList<>();
        for (Item s : itemList) {
            final String name = s.getItemName().toLowerCase();
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
        final ArrayList<Item> searchList = Search(items, newText);

        if (searchList.size() > 0) {
            itemAdapter = new ItemAdapter(searchList, ItemList.this);
            itemListRv.setAdapter(itemAdapter);
            itemAdapter.setFilter(searchList);
            return true;
        } else {
            Toast.makeText(ItemList.this, "No Record Found", Toast.LENGTH_SHORT).show();
            itemListRv.setAdapter(null);
            return false;
        }
    }

    private void init() {
        items = new ArrayList<>();
        itemListRv = findViewById(R.id.itemList);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        restRef = database.getReference("Restaurant");
        itemRef = database.getReference("Restaurant");
        noItemTv = findViewById(R.id.list_no_item_tv);
        listAddItem = findViewById(R.id.list_add_item_btn);
        listFloatAddItem = findViewById(R.id.list_item_float_btn);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
