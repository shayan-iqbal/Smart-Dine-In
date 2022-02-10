package com.example.startuplogin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startuplogin.DB.AppDatabase;
import com.example.startuplogin.DB.Cart;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class CartDetails extends AppCompatActivity {

    FirebaseAuth mAuth;
    static String currentUId;
    RecyclerView cartItemListRv;
    List<Cart> cartList;
    CartAdapter cartAdapter;
    static TextView subTotalTV;
    static TextView subAmount;
    static TextView taxTv;
    static TextView taxAmount;
    static TextView totalTv;
    static TextView totalAmount;
    static int countPriceFromDb;
    static AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_details);

        init();
        setCartDetails();

    }

    public static void setAmount() {

        countPriceFromDb = db.cartDao().countPrice(currentUId);
        Log.e("total ", String.valueOf(countPriceFromDb));
        subAmount.setText(String.valueOf(countPriceFromDb));
        int total = 100 + countPriceFromDb;
        totalAmount.setText(String.valueOf(total));

    }

    private void setCartDetails() {
        AppDatabase db = AppDatabase.getDbInstance(this);
        cartList = db.cartDao().getAllItemsOfCurrentUser(currentUId);
        if (!cartList.isEmpty()) {
            cartAdapter = new CartAdapter(cartList, this);
            cartItemListRv.setAdapter(cartAdapter);
            cartItemListRv.setLayoutManager(new LinearLayoutManager(this));
            cartItemListRv.hasFixedSize();
            cartAdapter.notifyDataSetChanged();

            setAmount();

        } else {
            hideAmountTv();
        }
    }

    public static void hideAmountTv() {
        subTotalTV.setVisibility(View.INVISIBLE);
        subAmount.setVisibility(View.INVISIBLE);
        taxTv.setVisibility(View.INVISIBLE);
        taxAmount.setVisibility(View.INVISIBLE);
        totalTv.setVisibility(View.INVISIBLE);
        totalAmount.setVisibility(View.INVISIBLE);

    }

    private void init() {
        db = AppDatabase.getDbInstance(this);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null)
            currentUId = mAuth.getCurrentUser().getUid();
        cartItemListRv = findViewById(R.id.cartItemListRV);
        cartList = new ArrayList<>();
        subTotalTV = findViewById(R.id.subTotal);
        subAmount = findViewById(R.id.subAmount);
        taxTv = findViewById(R.id.tax);
        taxAmount = findViewById(R.id.taxAmount);
        totalTv = findViewById(R.id.total);
        totalAmount = findViewById(R.id.totalAmount);
    }
}
