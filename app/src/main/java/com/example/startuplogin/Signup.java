package com.example.startuplogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {

    private static final String USERNAME_PATTERN = "^[a-zA-z ]{3,25}$";
    private static final String CONTACT_PATTERN = "^03[0-9]{2}-[0-9]{7}$";
    TextInputEditText signupName;
    TextInputEditText signupEmail;
    TextInputEditText signupPass;
    TextInputEditText signupConfpass;
    TextInputEditText signupContact;
    Button signupBtn;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    ProgressDialog progressDialog;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();
        init();

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDetails();
            }
        });
    }

    private void getDetails() {
        String name = signupName.getText().toString();
        String email = signupEmail.getText().toString();
        String pass = signupPass.getText().toString();
        String confPass = signupConfpass.getText().toString();
        String contact = signupContact.getText().toString();

        Pattern pattern;
        Matcher matcher;
        boolean check = true;

        if (!name.isEmpty()) {
            pattern = Pattern.compile(USERNAME_PATTERN);
            matcher = pattern.matcher(name);
            if (!matcher.matches()) {
                signupName.setError("Invalid Username");
                check = false;
            }
        } else {
            signupName.setError("Required Field");
            check = false;
        }
        if (email.isEmpty()) {
            check = false;
            signupEmail.setError("Required Field");
        }
        if (pass.isEmpty()) {
            check = false;
            signupPass.setError("Required Field");
        }
        if (!pass.equals(confPass)) {
            check = false;
            signupConfpass.setError("Password mismatch");
        }
        if (!contact.isEmpty()) {
            pattern = Pattern.compile(CONTACT_PATTERN);
            matcher = pattern.matcher(contact);
            if (!matcher.matches()) {
                check = false;
                signupContact.setError("Invalid Contact Number");
            }
        } else {
            signupContact.setError("Required Field");
            check = false;
        }

        if (check) {
            progressDialog.show();
            sendDetails(name, email, pass, contact);
        }
    }

    private void sendDetails(final String name, final String email, final String pass,
                             final String contact) {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    uid = mAuth.getCurrentUser().getUid();
                    Toast.makeText(Signup.this, "new uid"+uid, Toast.LENGTH_SHORT).show();
                    User user = new User(name, email, pass, contact,uid);
                    reference.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Signup.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                Intent RestLisrIntent = new Intent(Signup.this, RestaurantList.class);
                                startActivity(RestLisrIntent);
                                finish();
                            } else
                                Toast.makeText(Signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else
                    Toast.makeText(Signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPass = findViewById(R.id.signup_pass);
        signupConfpass = findViewById(R.id.signup_conf_pass);
        signupContact = findViewById(R.id.signup_contact);
        signupBtn = findViewById(R.id.signup_btn);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("User");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploadind Data");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }

}
