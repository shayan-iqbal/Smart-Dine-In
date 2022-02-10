package com.example.startuplogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetails extends AppCompatActivity {

    TextView idTv;
    TextView amountTv;
    TextView statusTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        init();
    }

    private void init() {

        idTv=findViewById(R.id.idTV);
        amountTv=findViewById(R.id.amountTv);
        statusTv=findViewById(R.id.statusTv);

        getDetails();
    }

    private void getDetails() {

        Intent intent=getIntent();

        try {
            JSONObject object=new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(object.getJSONObject("response"),intent.getStringExtra("PaymentAmount"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDetails(JSONObject response, String paymentAmount) {

        try {
            idTv.setText(response.getString("id"));
            amountTv.setText(response.getString(String.format("$%s",paymentAmount)));
            statusTv.setText(response.getString("state"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
