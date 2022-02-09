package com.example.startuplogin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.startuplogin.DB.AppDatabase;
import com.example.startuplogin.DB.Cart;
import com.google.firebase.auth.FirebaseAuth;

public class ItemDetail extends AppCompatActivity {

    TextView itemNameTv;
    TextView itemDescTv;
    TextView itemPriceTv;
    ImageView itemImageIV;
    ImageView plusBtn;
    ImageView minusBtn;
    TextView quantityTv;
    Button addToCartBtn;
    RadioGroup var1;
    RadioGroup var2;
    RadioGroup var3;
    private Item item;
    FirebaseAuth mAuth;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        init();
        getBundle();

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(quantityTv.getText().toString());
                quantityTv.setText(String.valueOf(++quantity));

            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(quantityTv.getText().toString());
                if (quantity > 1)
                    quantityTv.setText(String.valueOf(--quantity));

            }
        });

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVariationsDetails();
            }
        });

    }

    private void getVariationsDetails() {

        boolean check=true;
        int var1Id = var1.getCheckedRadioButtonId();
        int var2Id = var2.getCheckedRadioButtonId();
        int var3Id = var3.getCheckedRadioButtonId();

        String var1Value = "", var2Value = "", var3Value = "";
        if (var1.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getApplicationContext(), "Please Select all variations", Toast.LENGTH_LONG).show();
            check=false;
        } else {

            var1Value = ((RadioButton) findViewById(var1Id)).getText().toString();
        }
        if (var2.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getApplicationContext(), "Please Select all variations", Toast.LENGTH_LONG).show();
            check=false;
        } else {
            var2Value = ((RadioButton) findViewById(var2Id)).getText().toString();
        }
        if (var3.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getApplicationContext(), "Please Select all variations", Toast.LENGTH_LONG).show();
            check=false;
        } else {
            var3Value = ((RadioButton) findViewById(var3Id)).getText().toString();
        }
        Log.e("var values ", var1Value + var2Value + var3Value);

        saveCartItem(item, var1Value, var2Value, var3Value);

    }

    private void saveCartItem(Item item, String var1Value, String var2Value, String var3Value) {

        AppDatabase db = AppDatabase.getDbInstance(this);

        Cart cart = new Cart(currentUserId, item.getItemName(), "", item.getItemPrice(), var1Value, var2Value, var3Value,quantityTv.getText().toString());
        db.cartDao().insertCartItem(cart);
        finish();
    }

    private void getBundle() {

        item = (Item) getIntent().getSerializableExtra("itemBundle");
        setItemDetails(item);

    }

    private void setItemDetails(Item item) {
        itemNameTv.setText(item.getItemName());
        itemDescTv.setText(item.getItemDesc());
        itemPriceTv.setText("Rs. " + item.getItemPrice());
        // itemImageIV.setImageResource(item.getItemImage());

    }

    private void init() {

        itemNameTv = findViewById(R.id.itemDetailName);
        itemDescTv = findViewById(R.id.itemDetailDesc);
        itemPriceTv = findViewById(R.id.itemDetailPrice);
        itemImageIV = findViewById(R.id.itemDetailImage);
        plusBtn = findViewById(R.id.plusQuantity);
        minusBtn = findViewById(R.id.minusQuantity);
        quantityTv = findViewById(R.id.quantityTv);
        addToCartBtn = findViewById(R.id.addToCartBtn);
        var1 = findViewById(R.id.variation1);
        var2 = findViewById(R.id.variation2);
        var3 = findViewById(R.id.variation3);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null)
            currentUserId = mAuth.getCurrentUser().getUid();
    }
}
