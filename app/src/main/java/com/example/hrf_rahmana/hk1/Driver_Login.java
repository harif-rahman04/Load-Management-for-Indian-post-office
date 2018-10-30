package com.example.hrf_rahmana.hk1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class Driver_Login extends AppCompatActivity implements View.OnClickListener {
    private EditText Lmail,Lpass;
    private Button Lbutton;
    private Button button;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_login);
        Lmail = (EditText) findViewById(R.id.logmail);
        Lpass = (EditText) findViewById(R.id.logpwd);
        Lbutton = (Button) findViewById(R.id.logbutton);
        button = (Button) findViewById(R.id.regbutton);
        button.setOnClickListener(this);
        Lbutton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }
    private void userLogin()
    {
        String email = Lmail.getText().toString().trim();
        final String pass = Lpass.getText().toString().trim();

        if (email.isEmpty()){
            Lmail.setError("Email is required");
            Lmail.requestFocus();
            return;
        }

        /*if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Rmail.setError("Enter a Valid EmailId");
            Rmail.requestFocus();
            return;
        }*/

        if (pass.isEmpty()){
            Lpass.setError("Password is required");
            Lpass.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {Toast.makeText(getApplicationContext(),"Logged In",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Driver_Login.this,Driver_TravelInfo.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            Lpass.setText("");
                        }}
                });
    }
    @Override
    public void onClick(View v) {
        if(v == button)
        {
            startActivity(new Intent(this, Driver_Register.class));
        }
        if (v == Lbutton)
        {
            Toast.makeText(Driver_Login.this,"Starting DriverLogin",Toast.LENGTH_SHORT).show();
            userLogin();
        }

    }
}
