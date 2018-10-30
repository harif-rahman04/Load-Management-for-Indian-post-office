
package com.example.hrf_rahmana.hk1;

import android.location.Address;
import android.location.Geocoder;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Postofficedet extends AppCompatActivity {
DatabaseReference postdet;Double lat,longg;Button bsub;EditText etlat,etlongg; List<Address> addresses;Address address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postofficedet);
        postdet=FirebaseDatabase.getInstance().getReference("Postofficedetails");
        bsub=findViewById(R.id.button2);
        etlat=findViewById(R.id.etlat);
        etlongg=findViewById(R.id.etlong);
        bsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lat=Double.parseDouble(etlat.getText().toString());
                longg=Double.parseDouble(etlongg.getText().toString());
                Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    addresses= geocoder.getFromLocation(lat,longg,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
address=addresses.get(0);
                GeoFire geoFire=new GeoFire(postdet);
                geoFire.setLocation(address.getPostalCode(), new GeoLocation(lat,longg),
                        new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if(error==null) {
                                    Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
                                    etlat.setText("");
                                    etlongg.setText("");
                                }

                            }
                        });
            }
        });
    }
}
