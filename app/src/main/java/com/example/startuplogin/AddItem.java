package com.example.startuplogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddItem extends AppCompatActivity {

    TextInputEditText itemName;
    TextInputEditText itemCat;
    TextInputEditText itemPrice;
    TextInputEditText itemDesc;
    ImageView logoutImg;
    ImageView itemImage;
    Button addItemBtn;
    private static final String ITEM_PATTERN = "^[a-zA-z ]{3,20}$";
    private static final String DESC_PATTERN = "^[a-zA-z 0-9]{3,500}$";
    private static final String PRICE_PATTERN = "^[0-9]{2,8}$";

    FirebaseDatabase database;
    DatabaseReference itemRef;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    Bundle bundle;
    boolean bundleCheck;
    String currentItemId = "";
    String currentItemImage;
    String restId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        getSupportActionBar().hide();

        init();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            bundleCheck = true;
            currentItemId = bundle.getString("id");
            currentItemImage = bundle.getString("image");
            itemName.setText(bundle.getString("name"));
            itemCat.setText(bundle.getString("category"));
            itemPrice.setText(bundle.getString("price"));
            itemDesc.setText(bundle.getString("desc"));
        }

        logoutImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent loginIntent = new Intent(AddItem.this, MainActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                getDetails();
            }
        });
    }

    private void getDetails() {
        String name = itemName.getText().toString();
        String price = itemPrice.getText().toString();
        String category = itemCat.getText().toString();
        String desc = itemDesc.getText().toString();

        Pattern pattern;
        Matcher matcher;
        boolean check = true;

        if (!name.isEmpty()) {
            pattern = Pattern.compile(ITEM_PATTERN);
            matcher = pattern.matcher(name);
            if (!matcher.matches()) {
                itemName.setError("Invalid Name");
                check = false;
                progressDialog.dismiss();
            }
        } else {
            itemName.setError("Required Field");
            check = false;
            progressDialog.dismiss();
        }
        if (!category.isEmpty()) {
            pattern = Pattern.compile(ITEM_PATTERN);
            matcher = pattern.matcher(category);
            if (!matcher.matches()) {
                itemCat.setError("Invalid Category");
                check = false;
            }
        } else {
            itemCat.setError("Required Field");
            check = false;
            progressDialog.dismiss();
        }
        if (!desc.isEmpty()) {
            pattern = Pattern.compile(DESC_PATTERN);
            matcher = pattern.matcher(desc);
            if (!matcher.matches()) {
                check = false;
                progressDialog.dismiss();
                itemDesc.setError("Invalid Description");
            }
        } else {
            itemDesc.setError("Required Field");
            check = false;
            progressDialog.dismiss();
        }
        if (!price.isEmpty()) {
            pattern = Pattern.compile(PRICE_PATTERN);
            matcher = pattern.matcher(price);
            if (!matcher.matches()) {
                check = false;
                itemPrice.setError("Invalid Price");
                progressDialog.dismiss();
            }
        } else {
            itemPrice.setError("Required Field");
            check = false;
            progressDialog.dismiss();
        }
        if (check) {
            saveItem(name, category, price, desc);
        }
    }

    private void saveItem(String name, String category, String price, String desc) {
        Item item;
        final String itemId;
        if (!currentItemId.isEmpty()) {
            itemId = currentItemId;
            item = new Item(currentItemId,name,category,desc,price,"");
        } else {
            itemId = itemRef.push().getKey();
            item = new Item(itemId, name, category, desc, price, "");
        }
        itemRef.child(itemId).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    if (!itemId.isEmpty()) {
                        Toast.makeText(AddItem.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddItem.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                    }
                    clearForm((ViewGroup) findViewById(R.id.mainLayout));
                    Intent restListIntent = new Intent(AddItem.this, ItemList.class);
                    startActivity(restListIntent);
                    restListIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                } else {
                    Toast.makeText(AddItem.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void clearForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).setText("");
            }

            if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0))
                clearForm((ViewGroup) view);
        }
    }

    private void init() {
        itemName = findViewById(R.id.itemName);
        itemCat = findViewById(R.id.itemCategory);
        itemDesc = findViewById(R.id.itemDesc);
        itemPrice = findViewById(R.id.itemPrice);
        addItemBtn = findViewById(R.id.addItemBtn);
        database = FirebaseDatabase.getInstance();
        restId = ItemList.currentRestId;
        itemRef = database.getReference("Restaurant").child(restId).child("Item");
        mAuth = FirebaseAuth.getInstance();
        logoutImg = findViewById(R.id.logout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Data");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }
}
