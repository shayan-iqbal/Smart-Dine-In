package com.example.startuplogin;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startuplogin.DB.Cart;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    TextView tableNumTv;
    RecyclerView orderList;
    Bundle bundle;
    String intentType;
    CartAdapter adapter;
    String orderId, currentUid, restId;
    FirebaseAuth mAuth;
    DatabaseReference orderRef;
    DatabaseReference tableRef;
    List<Cart> orderArrayList;
    TextView totalAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        init();
        checkIntentType();
    }

    private void checkIntentType() {

        if (bundle != null) {
            intentType = bundle.getString("userType");
            Log.e("intent type ", intentType);
            if (intentType.equals("user")) {
                orderId = bundle.getString("orderId");
                restId = bundle.getString("restId");
                Log.e("order id ", orderId);
                tableRef=tableRef.child(restId).child("Table");
                getOrderDetails(orderId, restId);
            }


        }
    }

    private void getOrderDetails(final String orderId, final String restId) {

        orderRef.child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String tableId = snapshot.child("tableId").getValue().toString();
                displayTableName(tableId);
                Log.e("table Id", tableId);
//                Toast.makeText(OrderActivity.this, "on data", Toast.LENGTH_SHORT).show();
//                Toast.makeText(OrderActivity.this, "in if", Toast.LENGTH_SHORT).show();
                boolean counter = false;
                int i = 0;
                HashMap<String, String> list = new HashMap<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //
                    if (!counter) {
                        //   Log.e("counter ", dataSnapshot.child().getValue().toString());

                        //Cart cart = dataSnapshot.child("0").getValue(Cart.class);
                        // Log.e("cart ", cart.toString());
//                        for (DataSnapshot listArray :dataSnapshot.getChildren()){
//
//                            String drink = listArray.child("drink").getValue(String.class);
//                            Log.e("cart key", listArray.getKey());
//                            Log.e("drink", drink);
//                            Log.e("name ", listArray.getValue().toString());
//                        }

                    } else {
                        counter = true;
                        Log.e("array ", dataSnapshot.getValue().toString());
                    }
                    //list.add(dataSnapshot.getValue(Order.class));

                }
//                        Toast.makeText(OrderActivity.this, "in for", Toast.LENGTH_SHORT).show();
//                        String userId = dataSnapshot.child("userId").getValue().toString();
//                        String restIdData = dataSnapshot.child("restId").getValue().toString();
//                        Log.e("value ", userId);
//                        Log.e("key ", dataSnapshot.getKey());
//                        if ((userId.equals(currentUid)) && (restIdData.equals(restId))) {
//                            Log.e("snapshot ",dataSnapshot.getValue().toString());
//                            Cart cart = dataSnapshot.getValue(Cart.class);
//                           //Log.e("cart order ",dataSnapshot.child(orderId).child("drink").getValue().toString());
//                            totalAmount.setText(dataSnapshot.child("total").getValue().toString());
//                            displayTableName(dataSnapshot.child("tableId").getValue().toString());
//                            orderArrayList.add(cart);
//                            adapter = new CartAdapter(orderArrayList, OrderActivity.this, "user");
//                            orderList.hasFixedSize();
//                            orderList.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
//                            orderList.setAdapter(adapter);
//                        }

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
                Table table=snapshot.getValue(Table.class);
                Log.e("id ",snapshot.getValue().toString());
                String name=snapshot.child("tableName").getValue(String.class);
                tableNumTv.setText(name);
                Log.e("name ",snapshot.child("tableName").getValue(String.class));
              //  Log.e("id 2 ",snapshot.child("tableId").getValue().toString());
//                if(table.getTableId().equals(tableId)){
//                    String name=table.getTableName();
//                    Log.e("table name ",name);
//                    tableNumTv.setText(name);
//                }


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
        totalAmount = findViewById(R.id.totalTv);
    }
}
