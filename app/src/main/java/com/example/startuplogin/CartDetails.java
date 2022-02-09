package com.example.startuplogin;

import android.os.Bundle;

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
    String currentUId;
    RecyclerView cartItemListRv;
    List<Cart> cartList;
    CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_details);

        init();
        setCartDetails();

    }

    private void setCartDetails() {
        AppDatabase db=AppDatabase.getDbInstance(this);
        cartList=db.cartDao().getAllItemsOfCurrentUser(currentUId);
        if(!cartList.isEmpty()){
            cartAdapter=new CartAdapter(cartList,this);
            cartItemListRv.setAdapter(cartAdapter);
            cartItemListRv.setLayoutManager(new LinearLayoutManager(this));

        }
    }

    private void init() {

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null)
            currentUId=mAuth.getCurrentUser().getUid();
        cartItemListRv=findViewById(R.id.cartItemListRV);
        cartList=new ArrayList<>();
    }
}
