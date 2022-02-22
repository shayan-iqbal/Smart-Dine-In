package com.example.startuplogin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startuplogin.DB.AppDatabase;
import com.example.startuplogin.DB.Cart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import static com.example.startuplogin.TableAdapter.table;
import static com.example.startuplogin.TableAdapter.tableList;

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
    LinearLayout tableGroup;
    RecyclerView tableListRec;
    TableAdapter tableAdapter;
    ArrayList<Table> tableArrayList;
    static String restId;
    Button addTableBtn;
    EditText tableNameEt;
    EditText tableSeatEt;
    Spinner seatSpinner;
    int noOfSeats;
    Spinner tableSeatSp;
    Spinner tableStatus;
    static String loginType;
    int check=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        init();
//        if(TableAdapter.check){
//            tableSeatSp.setSelected(false);  // must
//            tableSeatSp.setSelection(0,true);
//        }
        checkManager();
        getCurrentRest();
        checkCart();

        addTableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = tableNameEt.getText().toString();
                // String seat = tableSeatEt.getText().toString();

                String tableId = restRef.child(restId).child("Table").push().getKey();
                Table table = new Table(name, noOfSeats, "Free", tableId);
                restRef.child(restId).child("Table").child(tableId).setValue(table).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RestaurantList.this, "Table Added", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(RestaurantList.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        currentUEmail = mAuth.getCurrentUser().getEmail();

        restRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.hasChildren()) {
                    //startDialog.dismiss();
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

                    restaurants.add(restaurant);
                    restAdapter = new RestaurantAdapter(restaurants, RestaurantList.this);
                    restListRv.hasFixedSize();
                    restListRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    restListRv.setAdapter(restAdapter);
                    restAdapter.notifyDataSetChanged();


                } else {
                    //startDialog.dismiss();
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

//            TableViewHolder.tableStatusSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                    String status = TableAdapter.tableStatusOption[i];
//                    showDialog(i, status);
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> adapterView) {
//
//                }
//            });

    }

    private void showDialog(final int position, final String option) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (option.equals("Reserved")) {
            builder.setMessage("Are you sure,you want to Free this Table");
        }


        builder.setTitle("Warning");
        builder.setIcon(R.drawable.ic_warning);
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (option.equals("Reserved")) {
                    updateStatus(position, option);
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

    private void updateStatus(int position, String option) {
        table = tableList.get(position);
        restRef.child(RestaurantList.restId).child("Table").child(table.getTableId()).child("tableStatus").setValue(option).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RestaurantList.this, "Status Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkManager() {
        startDialog.show();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            loginType = bundle.getString("userType");
            if (loginType.equals("manager")) {
                tableGroup.setVisibility(View.VISIBLE);
                tableListRec.setVisibility(View.VISIBLE);

            }
        }

    }

    private void setSpinner() {

        String[] seatOptions = {"2", "4", "6", "8", "10"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(RestaurantList.this, android.R.layout.simple_spinner_item, seatOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seatSpinner.setAdapter(spinnerAdapter);

        seatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                noOfSeats = Integer.parseInt(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getCurrentRest() {

        restRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                        String email = restaurant.getRestEmail();
                        if (!(email == null))
                            if (email.equals(currentUEmail)) {
                                restId = restaurant.getRestId();
                                Log.e("current rest ", restId);
                                getId(restId);
                                break;
                            }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getId(String restId) {
        String id = restId;
        // Log.e("id  ",id);
        setTableList(id);
    }

    private void setTableList(String rId) {

        setSpinner();
        restRef.child(rId).child("Table").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
                Table table = snapshot.getValue(Table.class);
                tableArrayList.add(table);
                TableAdapter adapter = new TableAdapter(RestaurantList.this, tableArrayList);
                tableListRec.setLayoutManager(new LinearLayoutManager(RestaurantList.this));
                tableListRec.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                startDialog.dismiss();
//
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
                Table table = snapshot.getValue(Table.class);
                tableArrayList.add(table);
                TableAdapter adapter = new TableAdapter(RestaurantList.this, tableArrayList);
                tableListRec.setLayoutManager(new LinearLayoutManager(RestaurantList.this));
                tableListRec.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {

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

    @Override
    protected void onResume() {
        super.onResume();
        checkCart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkCart();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        tableGroup = findViewById(R.id.TableGroup);
        tableArrayList = new ArrayList<>();
        tableListRec = findViewById(R.id.tableListRec);
        addTableBtn = findViewById(R.id.addTableBtn);
        tableNameEt = findViewById(R.id.tableNameEt);
        seatSpinner = findViewById(R.id.tableSeatSpIn);
        tableSeatSp = findViewById(R.id.tableSeatSp);

        // tableSeatSp.setOnItemSelectedListener(this);

        if (mAuth.getCurrentUser() != null)
            currentUId = mAuth.getCurrentUser().getUid();
    }


}
