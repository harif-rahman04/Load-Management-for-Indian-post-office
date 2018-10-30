package com.example.hrf_rahmana.hk1;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class sample extends AppCompatActivity {
EditText et1,et2,et3,et4;Button b1;
DatabaseReference localref;
List<Address> addresses;
Address address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        et1=findViewById(R.id.etlat);
        et2=findViewById(R.id.etlng);
        et3=findViewById(R.id.etweight);
        et4=findViewById(R.id.etloadsentornot);
        b1=findViewById(R.id.button3);

        localref= FirebaseDatabase.getInstance().getReference("Delhi");
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double latt = Double.parseDouble(et1.getText().toString());
                Double longg = Double.parseDouble(et2.getText().toString());
                try {
                    addresses= new Geocoder(getApplicationContext(), Locale.getDefault()).getFromLocation(latt,longg,1);
                    address=addresses.get(0);
                } catch (IOException e) {
                    e.printStackTrace();}
                new GeoFire(localref) .setLocation(address.getPostalCode(), new GeoLocation(latt, longg),
                        new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if(error==null)
                                    Toast.makeText(getApplicationContext(),"added",Toast.LENGTH_SHORT).show();
                               else
                                    Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
                            }
                        });
                localref.child(address.getPostalCode()).child("Loadweight").setValue(et3.getText().toString());
                localref.child(address.getPostalCode()).child("Loadavailable").setValue(et4.getText().toString());
                et1.setText("");
                et2.setText("");
                et3.setText("");
                et4.setText("");
            }
        });
    }
}
