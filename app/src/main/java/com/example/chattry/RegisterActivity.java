package com.example.chattry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText mEmailEt,mPasswordEt;
    Button mRegisterBtn;

    TextView loginTv;

    ProgressBar progressBar;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
        loginTv=findViewById(R.id.loginTv);





        mEmailEt=findViewById(R.id.emailET);
        mPasswordEt=findViewById(R.id.passwordET);
        mRegisterBtn=findViewById(R.id.btnRgister);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(newIntent);
            }
        });




        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mEmailEt.getText().toString().trim();
                String password=mPasswordEt.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                }
                else if(password.length()<6){
                    mPasswordEt.setError("Password Must Be At Least 6 characters");
                    mPasswordEt.setFocusable(true);
                }

                else {
                    registerUser(email,password);
                }

            }
        });


        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Create Account");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void registerUser(String email, String password){
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();

                            FirebaseUser user = mAuth.getCurrentUser();

                            //get user email and uid from auth

                            String email=user.getEmail();
                            String uid=user.getUid();

                            //when the us is registered, store their info in FB-RT-DB using hashmaps

                            HashMap<Object,String> hashMap = new HashMap<>();
                            hashMap.put("email",email);
                            hashMap.put("uid",uid);
                            hashMap.put("name","");
                            hashMap.put("onlineStatus","online");
                            hashMap.put("typingTo","noOne");
                            hashMap.put("phone","");
                            hashMap.put("image","");
                            hashMap.put("cover","");

                            //make FB-DB instance
                            FirebaseDatabase database=FirebaseDatabase.getInstance();
                            //path to store user data named "Users"
                            DatabaseReference reference = database.getReference("Users");
                            //put data within hashmap in DB
                            reference.child(uid).setValue(hashMap);



                            Toast.makeText(RegisterActivity.this, "Registered...\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(RegisterActivity.this, DashboardActivity.class);
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.

                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this,"Auth failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();

    }
}
