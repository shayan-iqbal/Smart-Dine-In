package com.example.startuplogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class Payment extends AppCompatActivity {
    Button payBtn;
    EditText amountEt;
    private String amount, token;
    final String API_GET_TOKEN = "http://10.0.2.2/braintree/main.php";
    final String API_CHECKOUT = "http://10.0.2.2/braintree/checkout.php";
    HashMap<String, String> paramsHash;
    private final int REQUEST_CODE = 123;
    private AsyncHttpClient client = new AsyncHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        init();
        getToken();

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPayment();
            }
        });
    }

    private void submitPayment() {

        DropInRequest dropInRequest = new DropInRequest().clientToken(token);
        Toast.makeText(this, "Tokennn " + token, Toast.LENGTH_SHORT).show();
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            Toast.makeText(this, "request", Toast.LENGTH_SHORT).show();
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce methodNonce = result.getPaymentMethodNonce();


                if (!amountEt.getText().toString().isEmpty()) {

                    RequestParams requestParams = new RequestParams();
                    amount = amountEt.getText().toString();
                    String nonce = methodNonce.getNonce();
                    requestParams.put("nonce", nonce);
                    requestParams.put("amount", amount);

                    client.post("http://10.0.2.2/braintree/checkout.php", requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(Payment.this, responseString, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            Toast.makeText(Payment.this, responseString, Toast.LENGTH_LONG).show();
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

    private void getToken() {
        client.get("http://10.0.2.2/braintree/main.php", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                token = responseString;
            }
        });
    }

    private void init() {

        payBtn = findViewById(R.id.payBtn);
        amountEt = findViewById(R.id.amount);
    }

}
