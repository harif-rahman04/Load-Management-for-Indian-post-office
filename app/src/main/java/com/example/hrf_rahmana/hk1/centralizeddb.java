package com.example.hrf_rahmana.hk1;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class centralizeddb extends AppCompatActivity {
Spinner sp1,sp2;
EditText et1,et2,et3;
PlaceAutocompleteFragment fromautocompleteFragment,toautocompleteFragment;
DatabaseReference cdbref;ImageButton bsub;
String availability_status,Load_quantity;
LatLng sourcelatlng,destlatlng;
    List<Address> sourceaddresses,destaddresses;Address sourceaddress,destaddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centralizeddb);
        sp1=findViewById(R.id.cdbAvailability_status);
        sp2=findViewById(R.id.cdbloadquantity);
        et1=findViewById(R.id.cdbvehicleid);
        et2=findViewById(R.id.cdbweightinkgs);
        et3=findViewById(R.id.cdbeta);
        bsub=findViewById(R.id.cdbdetailsbut);
        fromautocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.cdbfromplace_autocomplete_fragment);
        fromautocompleteFragment.setHint("Source_port");
        toautocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.cdbtoautocompletefragment);
        toautocompleteFragment.setHint("Destination_port");
        AutocompleteFilter fromautocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();
        fromautocompleteFragment.setFilter(fromautocompleteFilter);
        AutocompleteFilter toautocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();
        toautocompleteFragment.setFilter(toautocompleteFilter);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_of_availability, android.R.layout.simple_dropdown_item_1line);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sp1.setAdapter(adapter);
        sp1.setSelection(0);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.load_weight, android.R.layout.simple_dropdown_item_1line);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sp2.setAdapter(adapter2);
        sp2.setSelection(0);
        cdbref= FirebaseDatabase.getInstance().getReference("LoadAllocation").push();
        fromautocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                sourcelatlng=place.getLatLng();
            }
            @Override
            public void onError(Status status) {

            }
        });
        toautocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destlatlng=place.getLatLng();
            }
            @Override
            public void onError(Status status) {
            }
        });
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("--->","sp1 called==>"+position);
                availability_status=parent.getItemAtPosition(position).toString();
                Log.d("--->","sp1 called==>"+availability_status);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               Log.d("--->","sp2 called"+position);
               Load_quantity=parent.getItemAtPosition(position).toString();
               Log.d("--->","sp2 called"+Load_quantity);
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });
    bsub.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                sourceaddresses= new Geocoder(getApplicationContext(), Locale.getDefault()).getFromLocation(sourcelatlng.latitude,sourcelatlng.longitude,1);
                sourceaddress=sourceaddresses.get(0);
                new GeoFire(cdbref.child("source")).setLocation(sourceaddress.getSubAdminArea(), new GeoLocation(sourcelatlng.latitude,sourcelatlng.longitude),
                        new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                Log.d("locchange", "key" + key + "error" + error);
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                destaddresses= new Geocoder(getApplicationContext(), Locale.getDefault()).getFromLocation(destlatlng.latitude,destlatlng.longitude,1);
                destaddress=destaddresses.get(0);
                new GeoFire(cdbref.child("destination")).setLocation(destaddress.getSubAdminArea(), new GeoLocation(sourcelatlng.latitude,sourcelatlng.longitude),
                        new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                Log.d("locchange", "key" + key + "error" + error);
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
            cdbref.child("Vehicleid").setValue(et1.getText().toString());
            cdbref.child("Weight_in_kgs").setValue(et2.getText().toString());
            cdbref.child("Availability_status").setValue(availability_status);
            cdbref.child("Load_Quantity").setValue(Load_quantity);
            cdbref.child("ETA").setValue(et3.getText().toString());
            Toast.makeText(getApplicationContext(),"Data Updated Successfully",Toast.LENGTH_SHORT).show();
        }
    });
    }
}
