package com.example.startuplogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startuplogin.DB.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    TextView tableNumTv;
    RecyclerView orderList;
    Bundle bundle;
    String intentType;
    CartAdapter adapter;
    String orderId, currentUid, restId, tableName;
    FirebaseAuth mAuth;
    DatabaseReference orderRef;
    DatabaseReference tableRef;
    List<Cart> orderArrayList;
    TextView totalAmount;
    TextView headingTv;
    TextView tableNumHeadTv;
    TextView totalTv;
    ArrayList<Cart> cartArrayList;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        init();
        checkIntentType();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (intentType.equals("manager")) {
            Intent intent = new Intent(OrderActivity.this, RestaurantList.class);
            intent.putExtra("userType", "manager");
            startActivity(intent);
            finish();
        }
    }

    private void checkIntentType() {

        if (bundle != null) {

            intentType = bundle.getString("userType");
            Log.e("intent  ", intentType);

            if (intentType.equals("user")) {
                orderId = bundle.getString("orderId");
                restId = bundle.getString("restId");
                tableRef = tableRef.child(restId).child("Table");

                headingTv.setText("Order Completed");
                tableNumHeadTv.setText("Your Table Number is ");

                getManagerOrderDetails(restId, orderId, "");

            } else if (intentType.equals("manager")) {
                restId = bundle.getString("restId");
                orderId = bundle.getString("orderId");
                tableName = bundle.getString("tableName");
                Log.e("order order ", orderId);

                headingTv.setText("Table Reserved");
                tableNumHeadTv.setText("Table Number is ");
                tableNumTv.setText(tableName);
                getManagerOrderDetails(restId, orderId, tableName);
            }


        }
    }

    private void getManagerOrderDetails(String restId, String orderId, String tableName) {

        orderRef.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("order snapshot", snapshot.getValue().toString());
                int i = 0;

                Log.e("count ", String.valueOf(snapshot.getChildrenCount()));
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (i == 0) {

                        String name = dataSnapshot.child("itemName").getValue(String.class);
                        String drink = dataSnapshot.child("drink").getValue(String.class);
                        String meat = dataSnapshot.child("meat").getValue(String.class);
                        String fries = dataSnapshot.child("fries").getValue(String.class);
                        String price = dataSnapshot.child("itemPrice").getValue(String.class);
                        String quantity = dataSnapshot.child("quantity").getValue(String.class);

                        Cart cart = new Cart("", name, "", price, meat, fries, drink, quantity);
                        cartArrayList.add(cart);
                        adapter = new CartAdapter(cartArrayList, OrderActivity.this, "manager");
                        orderList.hasFixedSize();
                        orderList.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
                        orderList.setAdapter(adapter);
                        dialog.dismiss();

                    } else {
                        Log.e("total ", dataSnapshot.getValue().toString());
                        long total = snapshot.child("total").getValue(Long.class);
                        String tableId = snapshot.child("tableId").getValue(String.class);
                        if (intentType.equals("user")) {
                            displayTableName(tableId);
                        }
                        totalTv.setText(String.valueOf(total));
                        dialog.dismiss();
                    }
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void displayTableName(final String tableId) {
        tableRef.child(tableId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Table table = snapshot.getValue(Table.class);
                Log.e("id ", snapshot.getValue().toString());
                String name = snapshot.child("tableName").getValue(String.class);
                tableNumTv.setText(name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void init() {
        tableNumTv = findViewById(R.id.tableNumTv);
        orderList = findViewById(R.id.orderList);
        bundle = getIntent().getExtras();
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();
        orderArrayList = new ArrayList<>();
        orderRef = FirebaseDatabase.getInstance().getReference("Order");
        tableRef = FirebaseDatabase.getInstance().getReference("Restaurant");
        headingTv = findViewById(R.id.heading);
        tableNumHeadTv = findViewById(R.id.tableNumHead);
        totalTv = findViewById(R.id.totalTv);
        cartArrayList = new ArrayList<>();
        dialog = new ProgressDialog(this);
        dialog.show();
        dialog.setCancelable(true);
    }
}
