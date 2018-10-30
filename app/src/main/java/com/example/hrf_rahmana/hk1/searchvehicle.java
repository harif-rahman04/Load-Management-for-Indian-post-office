package com.example.hrf_rahmana.hk1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.Address;

public class searchvehicle extends AppCompatActivity {
EditText et1;Button b1;String vehicleid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchvehicle);
        et1=findViewById(R.id.hackvehid);
        b1=findViewById(R.id.hacksubbuttoon);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et1.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(),"Please Enter the fields",Toast.LENGTH_SHORT).show();
                vehicleid=et1.getText().toString();
                Intent intent=new Intent(getApplicationContext(), Adminmap.class);
                intent.putExtra("adminmapvalue","img3");
                intent.putExtra("Vehicleid",vehicleid);
                startActivity(intent);
            }
        });
    }

}
