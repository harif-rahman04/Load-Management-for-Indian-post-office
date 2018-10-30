package com.example.hrf_rahmana.hk1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Driver_Register extends AppCompatActivity  {
    private EditText Rmail,Rpwd,Rfirstname,Rlastname,Rconpwd,Rcont;
    private ImageButton Rbutton;
    private FirebaseAuth mAuth;
    String email,password,firstname,lastname,fullname,contact,confirmpassword,Uid;
DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_register);
        Rmail = (EditText) findViewById(R.id.rmail);
        Rpwd = (EditText) findViewById(R.id.rpwd);
        Rbutton = (ImageButton) findViewById(R.id.rbutton);
        Rfirstname=findViewById(R.id.idfirstname);
        Rlastname=findViewById(R.id.idlastname);
        Rconpwd=findViewById(R.id.rcnfmpwd);
        Rcont=findViewById(R.id.idcont);
        mAuth = FirebaseAuth.getInstance();
        Rbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });}
    public void registerUser() {
        Log.d("test-->","register user is called");
        email = Rmail.getText().toString().trim();
        password = Rpwd.getText().toString().trim();
        firstname=Rfirstname.getText().toString().trim();
        lastname=Rlastname.getText().toString().trim();
        confirmpassword=Rconpwd.getText().toString();
        contact=Rcont.getText().toString();
        if (firstname.isEmpty()){
            Rfirstname.setError("firstname is required");
            Rfirstname.requestFocus();
            return;
        }
        if (contact.isEmpty()){
            Rcont.setError("Contact is required");
            Rcont.requestFocus();
            return;
        }
        if (email.isEmpty()){
            Rmail.setError("Email is required");
            Rmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            Rpwd.setError("Password is required");
            Rpwd.requestFocus();
            return;
        }
        if (confirmpassword.isEmpty()){
            Rconpwd.setError("Confirm password is required");
            Rconpwd.requestFocus();
            return;
        }

        if(lastname.isEmpty())
            fullname=firstname;
        else
        fullname=firstname.concat(lastname);
        Log.d("test","email===>"+email+"password-->"+password);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("test","entered oncomplte");
                        if(task.isSuccessful()) {
                            Log.d("test","login su" +
                                    "ccessfully");
                            Uid=mAuth.getCurrentUser().getUid();
                            databaseReference= FirebaseDatabase.getInstance().getReference("DriverDetails").child(Uid);
                            databaseReference.setValue(new Driver_details(fullname,contact,email));
                            Toast.makeText(getApplicationContext(),"Registration Successful",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),Driver_TravelInfo.class));
                            finish();
                        }else{
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(Driver_Register.this,"Already Registered",Toast.LENGTH_SHORT).show();
                            }else
                            {
                                Toast.makeText(Driver_Register.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
