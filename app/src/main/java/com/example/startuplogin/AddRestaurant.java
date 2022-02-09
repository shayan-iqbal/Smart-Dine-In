package com.example.startuplogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AddRestaurant extends AppCompatActivity {

    TextInputEditText restName;
    TextInputEditText restEmail;
    TextInputEditText restContact;
    TextInputEditText restLocation;
    TextInputEditText restBranchCode;
    TextInputEditText restType;
    ImageView logoutImg;
    Button addRestBtn;
    String passcode;
    private static final String USERNAME_PATTERN = "^[a-zA-z @0-9]{3,25}$";
    private static final String CONTACT_PATTERN = "^03[0-9]{2}-[0-9]{7}$";
    private static final String BRANCHCODE_PATTERN = "^[a-zA-z-0-9]{7,25}$";
    private static final String EMAIL_PATTERN = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\\\.[a-z]{2,6}$";
    private static final String TYPE_PATTERN = "^[a-zA-z ]{3,25}$";
    FirebaseDatabase database;
    DatabaseReference restRef;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    Bundle bundle;
    boolean bundleCheck;
    String currentRestId = "";
    String currentRestImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        getSupportActionBar().hide();

        init();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            bundleCheck = true;
            currentRestId = bundle.getString("id");
            currentRestImage = bundle.getString("image");
            restName.setText(bundle.getString("name"));
            restEmail.setText(bundle.getString("email"));
            restContact.setText(bundle.getString("contact"));
            restLocation.setText(bundle.getString("location"));
            restType.setText(bundle.getString("type"));
            restBranchCode.setText(bundle.getString("branch_code"));
            restEmail.setEnabled(false);
        }

        logoutImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent loginIntent = new Intent(AddRestaurant.this, MainActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        addRestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                getDetails();
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void getDetails() {
        String name = restName.getText().toString();
        String email = restEmail.getText().toString();
        String contact = restContact.getText().toString();
        String branchCode = restBranchCode.getText().toString();
        String location = restLocation.getText().toString();
        String type = restType.getText().toString();

        Pattern pattern;
        Matcher matcher;
        boolean check = true;

        if (!name.isEmpty()) {
            pattern = Pattern.compile(USERNAME_PATTERN);
            matcher = pattern.matcher(name);
            if (!matcher.matches()) {
                restName.setError("Invalid Username");
                check = false;
                progressDialog.dismiss();
            }
        } else {
            restName.setError("Required Field");
            check = false;
            progressDialog.dismiss();
        }
        if (!email.isEmpty()) {
//            pattern = Pattern.compile(EMAIL_PATTERN);
//            matcher = pattern.matcher(email);
//            if (!matcher.matches()) {
//                restEmail.setError("Invalid Email");
//                check = false;
//            }
        } else {
            restEmail.setError("Required Field");
            check = false;
            progressDialog.dismiss();
        }
        if (!contact.isEmpty()) {
            pattern = Pattern.compile(CONTACT_PATTERN);
            matcher = pattern.matcher(contact);
            if (!matcher.matches()) {
                check = false;
                progressDialog.dismiss();
                restContact.setError("Invalid Contact Number");
            }
        } else {
            restContact.setError("Required Field");
            check = false;
            progressDialog.dismiss();
        }
        if (!branchCode.isEmpty()) {
            pattern = Pattern.compile(BRANCHCODE_PATTERN);
            matcher = pattern.matcher(branchCode);
            if (!matcher.matches()) {
                check = false;
                restBranchCode.setError("Invalid Branch Code");
                progressDialog.dismiss();
            }
        } else {
            restBranchCode.setError("Required Field");
            check = false;
            progressDialog.dismiss();
        }

        if (location.isEmpty()) {
            check = false;
            restLocation.setError("Required Field");
            progressDialog.dismiss();
        }
        if (!type.isEmpty()) {
            pattern = Pattern.compile(TYPE_PATTERN);
            matcher = pattern.matcher(type);
            if (!matcher.matches()) {
                check = false;
                restType.setError("Invalid Restaurant Type");
                progressDialog.dismiss();
            }
        } else {
            restType.setError("Required Field");
            check = false;
            progressDialog.dismiss();
        }
        if (check) {
            passcode = getPassword(name);
            if (!currentRestId.isEmpty()) {
                saveRestaurant(name, email, bundle.getString("pass"), contact, branchCode, location, type);
            } else {
                sendEmail(name, email, passcode);
                createRestUser(name, email, passcode, contact, branchCode, location, type);
            }
        }
    }

    private void createRestUser(final String name, final String email, final String passcode, final String contact, final String branchCode, final String location, final String type) {
        mAuth.createUserWithEmailAndPassword(email, passcode).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    saveRestaurant(name, email, passcode, contact, branchCode, location, type);
                    mAuth.signOut();
                    mAuth.signInWithEmailAndPassword("smartadmin@gmail.com","smartdinein").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(AddRestaurant.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(AddRestaurant.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void saveRestaurant(String name, String email, String passcode, String contact, String branchCode, String location, String type) {
        Restaurant restaurant;
        String restId;
        if (!currentRestId.isEmpty()) {
            restId = currentRestId;
            restaurant = new Restaurant(currentRestId, name, email, passcode, contact, branchCode, location, type, currentRestImage);
        } else {
            restId = restRef.push().getKey();
            restaurant = new Restaurant(restId, name, email, passcode, contact, branchCode, location, type, "");
        }
        restRef.child(restId).setValue(restaurant).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    if (!currentRestId.isEmpty()) {
                        Toast.makeText(AddRestaurant.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddRestaurant.this, "Restaurant Created Successfully", Toast.LENGTH_SHORT).show();
                    }
                    clearForm((ViewGroup) findViewById(R.id.mainLayout));
                    Intent restListIntent = new Intent(AddRestaurant.this, RestaurantList.class);
                    startActivity(restListIntent);
                    finish();
                } else {
                    Toast.makeText(AddRestaurant.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private String getPassword(String name) {
        char[] chars = name.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();

        Random rand = new Random();

        for (int i = 0; i < 8; i++) {
            char c = chars[rand.nextInt(chars.length)];
            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }

    private void sendEmail(String name, String email, String passcode) {

        final String senderEmail = "smart.dine.in.app@gmail.com";
        final String senderPass = "smartdinein";
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderPass);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Smart Dine In Login Code");
            message.setText("Hi " + email + "\n Please use the following passcode to login in our Smart DineIn app.\n" +
                    "\n     " + passcode);
            Transport.send(message);
        } catch (MessagingException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
        restName = findViewById(R.id.restName);
        restEmail = findViewById(R.id.restEmail);
        restContact = findViewById(R.id.restContact);
        restBranchCode = findViewById(R.id.restBranchCode);
        restLocation = findViewById(R.id.restLocation);
        restType = findViewById(R.id.restType);
        addRestBtn = findViewById(R.id.addRestBtn);
        database = FirebaseDatabase.getInstance();
        restRef = database.getReference("Restaurant");
        mAuth = FirebaseAuth.getInstance();
        logoutImg = findViewById(R.id.logout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Data");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }
}
