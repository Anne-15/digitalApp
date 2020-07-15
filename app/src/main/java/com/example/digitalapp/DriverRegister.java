package com.example.digitalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverRegister extends AppCompatActivity {
    private EditText mFullname, memail, mpassword, mcar, mphone;
    private TextView mTextview4;
    private Button mRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);

        mFullname = findViewById(R.id.fullName);
        memail = findViewById(R.id.email);
        mpassword = findViewById(R.id.password);
        mcar = findViewById(R.id.car);
        mphone = findViewById(R.id.phone);
        mTextview4 = findViewById(R.id.textView4);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){
            Intent intent = new Intent(DriverRegister.this, DriverLoginActivity.class);
            startActivity(intent);
        }

        mTextview4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverRegister.this, DriverLoginActivity.class);
                startActivity(intent);
            }
        });

        mRegister = findViewById(R.id.Registerbutton);

        mRegister.setOnClickListener(new View.OnClickListener() {
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

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){

                            Toast.makeText(DriverRegister.this, "Error", Toast.LENGTH_SHORT).show();

                        }else{
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("name");
                            current_user_db.setValue(true);

                            Toast.makeText(DriverRegister.this, "User Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DriverRegister.this, DriverLoginActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }
}
