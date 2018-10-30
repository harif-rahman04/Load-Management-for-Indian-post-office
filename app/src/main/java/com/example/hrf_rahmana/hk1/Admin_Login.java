package com.example.hrf_rahmana.hk1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Admin_Login extends AppCompatActivity {
    private EditText admail,adpass;
    private ImageButton adLbutton;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login);
    admail=findViewById(R.id.adlogmail);
    adpass=findViewById(R.id.adlogpwd);
    adLbutton=findViewById(R.id.adlogbutton);
    mAuth=FirebaseAuth.getInstance();
    adLbutton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userLogin();
        }
    });
    }
    private void userLogin()
    {
        String email = admail.getText().toString().trim();
        String pass = adpass.getText().toString().trim();

        if (email.isEmpty()){
            admail.setError("Email is required");
            admail.requestFocus();
            return;
        }

        /*if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Rmail.setError("Enter a Valid EmailId");
            Rmail.requestFocus();
            return;
        }*/

        if (pass.isEmpty()){
            adpass.setError("Password is required");
            adpass.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(),"Logged In",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Admin_Login.this,Admin_Home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            adpass.setText("");
                        }}
                });
    }
}

