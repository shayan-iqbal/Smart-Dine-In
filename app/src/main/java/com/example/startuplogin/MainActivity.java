package com.example.startuplogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.stetho.Stetho;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextInputEditText loginEmail;
    TextInputEditText loginPass;
    Button loginBtn;
    Button loginSignupBtn;
    FirebaseAuth mAuth;
    Spinner loginDropDown;
    FirebaseDatabase database;
    DatabaseReference reference;
    String[] loginOptions = {"Login As:", "Admin", "User", "Manager"};
    String loginValue;
    String currentUserEmail;
    ProgressDialog progressDialog;
    DatabaseReference restRef;
    String currentRestId;
    ProgressDialog startProgress;
    boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        getSupportActionBar().hide();
        init();

        if (mAuth.getCurrentUser() != null) {
            startProgress.show();
            currentUserEmail = mAuth.getCurrentUser().getEmail();
            if (currentUserEmail.equals("smartadmin@gmail.com")) {
                startProgress.dismiss();
                Intent addRestIntent = new Intent(MainActivity.this, RestaurantList.class);
                startActivity(addRestIntent);
                finish();
            } else {
                final String loginUserId = mAuth.getCurrentUser().getEmail();
                restRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                                String email = restaurant.getRestEmail();
                                if (email.equals(loginUserId)) {
                                    check=true;
                                    Intent addItemIntent = new Intent(MainActivity.this, ItemList.class);
                                    startActivity(addItemIntent);
                                    startProgress.dismiss();
                                    finish();
                                    break;
                                } else
                                    startProgress.dismiss();
                            }
                            if(!check){
                                Intent RestListIntent = new Intent(MainActivity.this, RestaurantList.class);
                                startActivity(RestListIntent);
                                startProgress.dismiss();
                                finish();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            Toast.makeText(this, String.valueOf(check), Toast.LENGTH_SHORT).show();

        }


        setSpinner();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDetails();
            }
        });

        loginSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(MainActivity.this, Signup.class);
                startActivity(signUpIntent);
            }
        });
    }

    private void setSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, loginOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loginDropDown.setAdapter(spinnerAdapter);

        loginDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loginValue = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getDetails() {
        boolean check = true;
        String email = loginEmail.getText().toString();
        String pass = loginPass.getText().toString();

        if (email.isEmpty()) {
            check = false;
            loginEmail.setError("Required Field");
        }
        if (pass.isEmpty()) {
            check = false;
            loginPass.setError("Required Field");
        }
        if (loginValue.equals("Login As:")) {
            check = false;
            Toast.makeText(MainActivity.this, "Please select Login As", Toast.LENGTH_SHORT).show();
        } else if (loginValue.equals("Admin")) {
            if (!(email.equals("smartadmin@gmail.com") && pass.equals("smartdinein"))) {
                check = false;
                Toast.makeText(MainActivity.this, "Invalid Admin username or password", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.show();
                loginAdmin(email, pass);
            }
        } else if (loginValue.equals("User")) {
            progressDialog.show();
            loginUser(email, pass);
        } else if (loginValue.equals("Manager")) {
            progressDialog.show();
            loginManager(email, pass);
        }
    }

    private void loginManager(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Intent restDetailIntent = new Intent(MainActivity.this, ItemList.class);
                    startActivity(restDetailIntent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void loginAdmin(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Intent adminIntent = new Intent(MainActivity.this, RestaurantList.class);
                    startActivity(adminIntent);
                    finish();
                } else
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loginUser(String email, String pass) {

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Intent mapIntent = new Intent(MainActivity.this, RestaurantList.class);
                    startActivity(mapIntent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void init() {
        loginEmail = findViewById(R.id.loginEmail);
        loginPass = findViewById(R.id.loginPass);
        loginBtn = findViewById(R.id.loginBtn);
        loginSignupBtn = findViewById(R.id.loginSignUpBtn);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        restRef = database.getReference("Restaurant");
        loginDropDown = findViewById(R.id.loginAsDropdown);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Data");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        startProgress = new ProgressDialog(this);
        startProgress.setTitle("Please Wait...");
        startProgress.setCancelable(false);
    }
}
