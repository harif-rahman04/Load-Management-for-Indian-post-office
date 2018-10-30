package com.example.hrf_rahmana.hk1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Admin_Home extends AppCompatActivity implements View.OnClickListener {
ImageView img1,img2,img3,img4,img5,img6;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_home);
        img1=findViewById(R.id.img1);
        img2=findViewById(R.id.img2);
        img3=findViewById(R.id.img3);
        img4=findViewById(R.id.img4);
        img5=findViewById(R.id.img5);
        img6=findViewById(R.id.img6);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        img5.setOnClickListener(this);
        img6.setOnClickListener(this);
       intent =new Intent(getApplicationContext(),Adminmap.class);}
    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.img1:
            intent.putExtra("adminmapvalue","img1");
            startActivity(intent);
            break;
            case R.id.img2:
                intent.putExtra("adminmapvalue","img2");
                startActivity(intent);
                break;
            case R.id.img3:
                startActivity(new Intent(getApplicationContext(),searchvehicle.class));
               /* intent.putExtra("adminmapvalue","img3");
                searchdriver();*/
                break;
            case R.id.img4:
                intent.putExtra("adminmapvalue","img4");
                startActivity(intent);
                break;
            case R.id.img5:
                //intent.putExtra("adminmapvalue","img5");
              //  startActivity(intent);
                startActivity(new Intent(getApplicationContext(),centralizeddb.class));
                break;
            case R.id.img6:
                intent.putExtra("adminmapvalue","img6");
                startActivity(intent);
                break;
    }}
    public void searchdriver(){
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.customsearchdialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getApplicationContext());
        alertDialogBuilder.setView(promptsView);
        final EditText userInput =promptsView
                .findViewById(R.id.etvehicleidinput);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("search Vehicle",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String str;
                                str=userInput.getText().toString();
                                intent.putExtra("Vehicleid",str);
                                startActivity(intent);
                            }})
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();}
    }