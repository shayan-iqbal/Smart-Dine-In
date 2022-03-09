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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class CartDetails extends AppCompatActivity {

    private static final String GUEST_PATTERN = "^[1-9]{1,3}$";
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
    String tableTime;
    DatabaseReference cartRef;
    private String rId;

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
                dialog.show();
                Pattern pattern;
                Matcher matcher;
                boolean check = true;

                noOfGuest = noOfGuestEt.getText().toString();

                if (!noOfGuest.isEmpty()) {
                    pattern = Pattern.compile(GUEST_PATTERN);
                    matcher = pattern.matcher(noOfGuest);
                    if (!matcher.matches()) {
                        noOfGuestEt.setError("Invalid input");
                        check = false;
                        dialog.dismiss();
                    }
                } else {
                    noOfGuestEt.setError("Required Field");
                    check = false;
                    dialog.dismiss();
                }
                if (check) {
                    //saveOrder();
                    submitPayment();
                }
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

    private void submitPayment() {

        DropInRequest dropInRequest = new DropInRequest().clientToken(token);
        //Toast.makeText(this, "Tokennn " + token, Toast.LENGTH_SHORT).show();
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }

    private void getToken() {
        client.get("http://192.168.8.102/braintree/main.php", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                Log.e("Failure ",responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                token = responseString;
                dialog.dismiss();
                Log.e("success ",responseString);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
           // Toast.makeText(this, "request", Toast.LENGTH_SHORT).show();
            if (resultCode == RESULT_OK) {
               // Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce methodNonce = result.getPaymentMethodNonce();

                String totalAmount = String.valueOf(total);
                //if (!totalAmount.isEmpty()) {

                    RequestParams requestParams = new RequestParams();
                   // amount = amountEt.getText().toString();
                    String nonce = methodNonce.getNonce();
                    requestParams.put("nonce", nonce);
                    requestParams.put("amount", 200);

                    client.post("http://192.168.8.102/braintree/checkout.php", requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(CartDetails.this, responseString, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            Toast.makeText(CartDetails.this, responseString, Toast.LENGTH_LONG).show();
                            saveOrder();
                        }
                    });

//                } else {
//                    Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
//                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            } else {
                Exception exception = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("Exception error ", exception.toString());
            }
        }
    }

//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        Log.e("request code ",String.valueOf(requestCode));
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE) {
//
//            if (resultCode == RESULT_OK) {
//                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
//                PaymentMethodNonce methodNonce = result.getPaymentMethodNonce();
//
//                String totalAmount = String.valueOf(total);
//                if (!totalAmount.isEmpty()) {
//
//                    RequestParams requestParams = new RequestParams();
//                    String nonce = methodNonce.getNonce();
//                    requestParams.put("nonce", nonce);
//                    requestParams.put("amount", totalAmount);
//
//                    client.post("http://192.168.8.102/braintree/checkout.php", requestParams, new TextHttpResponseHandler() {
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                            Toast.makeText(CartDetails.this, responseString, Toast.LENGTH_LONG).show();
//                            dialog.dismiss();
//                            Log.e("Failure 2 ", responseString);
//                        }
//
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                            // saveOrder();
//                            Toast.makeText(CartDetails.this, "Transaction Successfull", Toast.LENGTH_LONG).show();
//                            Intent orderIntent = new Intent(CartDetails.this, OrderActivity.class);
//                            orderIntent.putExtra("userType", "user");
//                            orderIntent.putExtra("orderId", orderId);
//                            orderIntent.putExtra("restId", restId);
//
//                            orderIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            orderIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(orderIntent);
//                            finish();
//                            dialog.dismiss();
//
//                        }
//                    });
//
//                } else {
//                    Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                }
//            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
//            } else {
//                Exception exception = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
//                Toast.makeText(this, "exception " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
//            }
//        }
//    }

    private void saveOrder() {

        final ArrayList<Order> orders = new ArrayList<>();
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
                               // getId(orderId);
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

//    private void getId(final String orderId) {
//
//        cartRef.child(currentUId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                rId = snapshot.getValue().toString();
//                checkTable(orderId, rId);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void checkTable(final String orderId) {

       // tableRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(rId);
            Log.e("rIIDDDD ",tableRef.getKey());
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
                            getTime();

                            orderReference.child(orderId).child("tableId").setValue(table.getTableId());

                            tableRef.child(table.getTableId()).child("tableStatus").setValue("Reserved");
                            tableRef.child(table.getTableId()).child("tableTimeOut").setValue(tableTime);
                            tableRef.child(table.getTableId()).child("orderId").setValue(orderId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        deleteFromCart();
                                        //          submitPayment(orderId);
                                        Intent orderIntent = new Intent(CartDetails.this, OrderActivity.class);
                                        orderIntent.putExtra("userType", "user");
                                        orderIntent.putExtra("orderId", orderId);
                                        orderIntent.putExtra("restId", restId);

                                        orderIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        orderIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(orderIntent);
                                        finish();
                                        dialog.dismiss();

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

    private void getTime() {

        Date netDate = new Date(); // current time from here
        SimpleDateFormat sfd = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        tableTime = sfd.format(netDate);
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sfd.parse(tableTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.MINUTE, 45);
        tableTime = sfd.format(cal.getTime());


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
                        Intent restDetailIntent = new Intent(CartDetails.this, RestaurantList.class);
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

            Log.e("carrrrt list ", cartList.toString());

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
        cartRef = FirebaseDatabase.getInstance().getReference("Cart");
    }
}