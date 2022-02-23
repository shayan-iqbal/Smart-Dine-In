package com.example.startuplogin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.example.startuplogin.DB.AppDatabase;
import com.example.startuplogin.DB.Cart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CartDetails extends AppCompatActivity {

    private static int total = 0;
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
    Button paymentBtn;
    private String amount, token;
    private final int REQUEST_CODE = 123;
    private AsyncHttpClient client = new AsyncHttpClient();
    EditText noOfGuestEt;
    android.app.ProgressDialog dialog;
    private String noOfGuest;
    DatabaseReference orderRef;
    DatabaseReference restRef;
    DatabaseReference tableRef;
    DatabaseReference orderReference;
    boolean tableStatus;
    String restId;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_details);

        getSupportActionBar().hide();
        init();
        getToken();
        setCartDetails();

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noOfGuest = noOfGuestEt.getText().toString();
                dialog.show();
                saveOrder();
                //submitPayment();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //  finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    private void submitPayment(String orderId) {

        DropInRequest dropInRequest = new DropInRequest().clientToken(token);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }

    private void getToken() {
        client.get("http://10.0.2.2/braintree/main.php", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                Log.e("Failure ", responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                token = responseString;
                if (token != null)
                    dialog.dismiss();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce methodNonce = result.getPaymentMethodNonce();

                String totalAmount = String.valueOf(total);
                if (!totalAmount.isEmpty()) {

                    RequestParams requestParams = new RequestParams();
                    String nonce = methodNonce.getNonce();
                    requestParams.put("nonce", nonce);
                    requestParams.put("amount", totalAmount);

                    client.post("http://10.0.2.2/braintree/checkout.php", requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(CartDetails.this, responseString, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            // saveOrder();
                            Toast.makeText(CartDetails.this, "Transaction Successfull", Toast.LENGTH_LONG).show();
                            Intent orderIntent = new Intent(CartDetails.this, OrderActivity.class);
                            orderIntent.putExtra("userType", "user");
                            orderIntent.putExtra("orderId", orderId);
                            orderIntent.putExtra("restId", restId);

                            orderIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            orderIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(orderIntent);
                            finish();
                            dialog.dismiss();

                        }
                    });

                } else {
                    Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            } else {
                Exception exception = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("Exception error ", exception.toString());
            }
        }
    }

    private void saveOrder() {

        ArrayList<Order> orders = new ArrayList<>();
        orderId = orderReference.push().getKey();

        orderReference.child(orderId).setValue(cartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    orderReference.child(orderId).child("userId").setValue(currentUId);
                    orderReference.child(orderId).child("total").setValue(total);
                    orderReference.child(orderId).child("restId").setValue(restId).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                checkTable(orderId);
                            } else
                                Toast.makeText(CartDetails.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(CartDetails.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkTable(final String orderId) {

        final int guest = Integer.parseInt(noOfGuest);
        tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Table table = dataSnapshot.getValue(Table.class);
                        Log.e("Table before", table.toString());
                        if ((table.getTableSeat() >= guest) && (table.tableStatus.equals("Free"))) {
                            tableStatus = true;
                            Log.e("Table", table.toString());
                            String tableName = table.getTableName();
                            orderReference.child(orderId).child("tableId").setValue(table.getTableId());
                            tableRef.child(table.getTableId()).child("tableStatus").setValue("Reserved");
                            tableRef.child(table.getTableId()).child("orderId").setValue(orderId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        deleteFromCart();
                                        submitPayment(orderId);
//                                        Intent orderIntent = new Intent(CartDetails.this, OrderActivity.class);
//                                        orderIntent.putExtra("userType", "user");
//                                        orderIntent.putExtra("orderId", orderId);
//                                        orderIntent.putExtra("restId", restId);
//
//                                        orderIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        orderIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        startActivity(orderIntent);
//                                        finish();
//                                        dialog.dismiss();

                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(CartDetails.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            break;
                        }

                    }
                    if (!tableStatus) {
                        orderReference.child(orderId).removeValue();
                        showAlert();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deleteFromCart() {


        AppDatabase appDatabase = AppDatabase.getDbInstance(this);
        appDatabase.cartDao().deleteUserCart(currentUId);

    }

    private void showAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sorry! We dont have ant table free for " + noOfGuest + " persons now.Please check again. ")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent restDetailIntent = new Intent(CartDetails.this, RestaurantDetail.class);
                        startActivity(restDetailIntent);
                        finish();
                        restDetailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public static void setAmount() {

        countPriceFromDb = db.cartDao().countPrice(currentUId);
        Log.e("total ", String.valueOf(countPriceFromDb));
        subAmount.setText(String.valueOf(countPriceFromDb));
        total = 100 + countPriceFromDb;
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
        paymentBtn = findViewById(R.id.paymentBtn);
        noOfGuestEt = findViewById(R.id.noOfGuestEt);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.show();
        orderReference = FirebaseDatabase.getInstance().getReference("Order");
        orderRef = FirebaseDatabase.getInstance().getReference("User").child(mAuth.getCurrentUser().getUid()).child("Order");
        if (RestaurantAdapter.restId != null) {
            restId = RestaurantAdapter.restId;
            restRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(restId).child("Order");
            tableRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(restId).child("Table");
        } else if (RestaurantDetail.restId != null) {
            restId = RestaurantDetail.restId;
            restRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(restId).child("Order");
            tableRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(restId).child("Table");
        }

    }
}