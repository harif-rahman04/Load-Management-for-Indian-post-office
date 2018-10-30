package com.example.hrf_rahmana.hk1;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.provider.Contacts;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AlertDialogLayout;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;

public class Driver_Home extends AppCompatActivity implements View.OnClickListener {
ImageView b1,b3,b4,b5,b6,blogout;String TAG="Driver_Home";
AlertDialog adlogout; AlertDialog.Builder alertDialogBuilderlogout;AlertDialog adbreakdown;AlertDialog.Builder alertDialogBuilderBreakdown;
DatabaseReference Breakdownref,Availableref,originalsourcelatlngref;String Uid; Intent intent;
TextView txtbreakdownreq;Boolean Emergencymode=false;LatLng originalsourcelatlng,currentlatlng;

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop called");
       // Availableref.removeValue();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_home);
        Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        Breakdownref= FirebaseDatabase.getInstance().getReference("Breakdownvehicles");
        Availableref = FirebaseDatabase.getInstance().getReference("AvailableDrivers").child(Uid);
        originalsourcelatlngref=FirebaseDatabase.getInstance().getReference("InitialDriversource");
        intent=new Intent(getApplicationContext(),MapsActivity.class);
        Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        txtbreakdownreq=findViewById(R.id.txtbrekdownreq);
        alertDialogBuilderlogout = new AlertDialog.Builder(this);
        alertDialogBuilderlogout.setTitle("Are you sure you want to Logout?");
        alertDialogBuilderlogout.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FirebaseAuth.getInstance().signOut();
                Availableref.removeValue();
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    startActivity(new Intent(getApplicationContext(),Driver_Login.class));
                }
            }
        });
        alertDialogBuilderlogout.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             adlogout.dismiss();
            }
        });
        adlogout=alertDialogBuilderlogout.create();
        alertDialogBuilderBreakdown=new AlertDialog.Builder(this);
        String str=(Emergencymode)?"Do you want to Request breakdown/other request":"Do you want to cancel the request";
        alertDialogBuilderBreakdown.setTitle(str);
        alertDialogBuilderBreakdown.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!Emergencymode){
                    Emergencymode = true;
                Breakdownref.child(Uid).setValue("True");
                txtbreakdownreq.setText("Cancel Breakdown request");
                startActivity(intent);
                }
            else{
                    Breakdownref.child(Uid).removeValue();
                Emergencymode=false;
                txtbreakdownreq.setText("Request Breakdown");
            }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
           adbreakdown.dismiss();
            }
        });
        adbreakdown = alertDialogBuilderBreakdown.create();
        b1=findViewById(R.id.img1);
        //b2=findViewById(R.id.img2);
        b3=findViewById(R.id.img3);
        b4=findViewById(R.id.img4);
        b5=findViewById(R.id.img5);
        b6=findViewById(R.id.img6);
        blogout=findViewById(R.id.bdriverlogout);
        b1.setOnClickListener(this);
     //   b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        blogout.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Log.d(TAG,"Entered in onclick driverhome");
        switch (v.getId()){
            case R.id.img1:
                intent.putExtra("value","img1");
                startActivity(intent);
                break;
        /*    case R.id.img2:
                intent.putExtra("value","img2");
                startActivity(intent);
                break;*/
            case R.id.img3:
                intent.putExtra("value","img3");
                startActivity(intent);
                break;
            case R.id.img4:
                intent.putExtra("value","img4");
                adbreakdown.show();
                break;
            case R.id.img5:
                intent.putExtra("value","img5");
                startActivity(intent);
                break;
            case R.id.img6:
                getdistance();
                showChangeLangDialog();
              //  intent.putExtra("value","img6");
                //startActivity(intent);
                break;
            case R.id.bdriverlogout:
                adlogout.show();
        }
    }
    public void getdistance(){
        Availableref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double latt = Double.parseDouble(dataSnapshot.child("l").child("0").getValue().toString());
                Double longg = Double.parseDouble(dataSnapshot.child("l").child("1").getValue().toString());
                currentlatlng=new LatLng(latt,longg);}
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
originalsourcelatlngref.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Double latt = Double.parseDouble(dataSnapshot.child("l").child("0").getValue().toString());
        Double longg = Double.parseDouble(dataSnapshot.child("l").child("1").getValue().toString());
        originalsourcelatlng=new LatLng(latt,longg);
    }
    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});}
    public void showChangeLangDialog() {
        Log.d(TAG,"show lang change dialog");
        final AlertDialog b;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        LayoutInflater inflater = this.getLayoutInflater();
    /*    final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);*/
      //  final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        Double latt=10.66632310,longg=78.744786;
        originalsourcelatlng=new LatLng(latt,longg);
        b = dialogBuilder.create();
        dialogBuilder.setTitle("TRAVELLED INFO");
        dialogBuilder.setMessage("Travelled distance  "+CalculationByDistance(originalsourcelatlng,currentlatlng)+"KM");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                b.dismiss();
                //do something with edt.getText().toString();
            }
        });
     /*   dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });*/

        b.show();
    }
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        double distance;
        Location locationA = new Location("");
        locationA.setLatitude(StartP.latitude);
        locationA.setLongitude(StartP.longitude);

        Location locationB = new Location("");
        locationB.setLatitude(EndP.latitude);
        locationB.setLongitude(EndP.longitude);

        distance = locationA.distanceTo(locationB);
        return distance/1000;
    }

}
