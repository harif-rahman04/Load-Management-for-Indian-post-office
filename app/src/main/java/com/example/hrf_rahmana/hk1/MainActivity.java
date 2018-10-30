package com.example.hrf_rahmana.hk1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button AdminButton,DriverButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AdminButton = (Button) findViewById(R.id.adminbut);
        DriverButton = (Button) findViewById(R.id.driverbut);
        AdminButton.setOnClickListener(this);
        DriverButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == AdminButton){
            startActivity(new Intent(this,Admin_Login.class));
        }
        if (v == DriverButton){
            startActivity(new Intent(this,Driver_Login.class));
        }
    }
}
