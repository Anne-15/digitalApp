package com.example.digitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerRegister extends AppCompatActivity {
    private EditText mname,memail,mpassword,mphone,mcar;
    private Button register;
    private TextView login;
    private FirebaseAuth mAuth;
    private ProgressBar mprogressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);

        mname = findViewById(R.id.fullName);
        memail = findViewById(R.id.email);
        mpassword = findViewById(R.id.password);
        mphone = findViewById(R.id.phone);
        mcar = findViewById(R.id.car);
        register = findViewById(R.id.Registerbutton);
        mprogressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){
            Intent intent = new Intent(CustomerRegister.this, CustomerLoginActivity.class);
            startActivity(intent);
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = memail.getText().toString();
                String password = mpassword.getText().toString();
                if (TextUtils.isEmpty(email)){
                    memail.setError("Email is empty");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mpassword.setError("Password is empty");
                    return;
                }
                if (password.length() < 6){
                    mpassword.setError("Password must be >= 6 characters");
                    return;
                }
                mprogressBar.setVisibility(View.VISIBLE);
                //Register customer

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            Toast.makeText(CustomerRegister.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }else{
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id);
                            current_user_db.setValue(true);
                            Toast.makeText(CustomerRegister.this, "User Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CustomerRegister.this, CustomerLoginActivity.class);
                            startActivity(intent);
                        }
                    }
                });

            }
        });
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerRegister.this, CustomerLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
