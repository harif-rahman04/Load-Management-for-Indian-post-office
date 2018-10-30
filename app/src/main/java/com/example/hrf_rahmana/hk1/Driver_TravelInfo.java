package com.example.hrf_rahmana.hk1;


import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Driver_TravelInfo extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private ImageButton Detailsbutton;Spinner spinner;PlaceAutocompleteFragment fromautocompleteFragment,toautocompleteFragment;
    LatLng sourcelatlng,destlatlng;
    DatabaseReference InitialSourceref,Destref;
    String Uid,Loadweight;
    AlertDialog adconfirmtraveldetails; AlertDialog.Builder adBuilderconfirmtraveldetails;
    List<Address> sourceaddresses,destaddresses;Address sourceaddress,destaddress;
    EditText etvehicleid;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_travelinfo);
        spinner = (Spinner) findViewById(R.id.loadper);
      fromautocompleteFragment = (PlaceAutocompleteFragment)
              getFragmentManager().findFragmentById(R.id.fromplace_autocomplete_fragment);
        toautocompleteFragment = (PlaceAutocompleteFragment)
           getFragmentManager().findFragmentById(R.id.toautocompletefragment);
        etvehicleid=findViewById(R.id.vehicleid);
        Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        fromautocompleteFragment.setHint("FROM");
        toautocompleteFragment.setHint("TO");
        InitialSourceref= FirebaseDatabase.getInstance().getReference("InitialDriversource").child(Uid);
        Destref= FirebaseDatabase.getInstance().getReference("Destinationinfo");
        adBuilderconfirmtraveldetails= new AlertDialog.Builder(this);
        adBuilderconfirmtraveldetails.setTitle("Please,Ensure your journey details before proceed?");
        adBuilderconfirmtraveldetails.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (etvehicleid.getText().toString().equals(null)){
                    etvehicleid.setError("Vehicleid is required");
                    etvehicleid.requestFocus();
                    return;}
                new GeoFire(Destref).setLocation(Uid, new GeoLocation(destlatlng.latitude,destlatlng.longitude),
                        new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if(error==null){
                                    Toast.makeText(getApplicationContext(),"destination setup success",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Error "+error,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                Destref.child(Uid).child("District").setValue(destaddress.getSubAdminArea());
                Destref.child(Uid).child("Loadpercentage").setValue(Loadweight);
                Destref.child(Uid).child("VehicleId").setValue(etvehicleid.getText().toString().trim());
                startActivity(new Intent(getApplicationContext(),Driver_Home.class));
            }
        });
        adBuilderconfirmtraveldetails.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adconfirmtraveldetails.dismiss();
            }
        });
        adconfirmtraveldetails=adBuilderconfirmtraveldetails.create();
        AutocompleteFilter fromautocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();
        fromautocompleteFragment.setFilter(fromautocompleteFilter);
        AutocompleteFilter toautocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();
        toautocompleteFragment.setFilter(toautocompleteFilter);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.load_weight, android.R.layout.simple_dropdown_item_1line);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
       Detailsbutton = (ImageButton) findViewById(R.id.detailsbut);
        spinner.setOnItemSelectedListener(this);
        Detailsbutton.setOnClickListener(this);
        fromautocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
sourcelatlng=place.getLatLng();
                try {
                    sourceaddresses= new Geocoder(getApplicationContext(), Locale.getDefault()).getFromLocation(sourcelatlng.latitude,sourcelatlng.longitude,1);
                    sourceaddress=sourceaddresses.get(0);
                    new GeoFire(InitialSourceref).setLocation(sourceaddress.getLocality(), new GeoLocation(sourcelatlng.latitude,sourcelatlng.longitude),
                            new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    Log.d("locchange", "key" + key + "error" + error);
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Status status) {

            }
        });
        toautocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destlatlng=place.getLatLng();
                try{
                    destaddresses= new Geocoder(getApplicationContext(), Locale.getDefault()).getFromLocation(destlatlng.latitude,destlatlng.longitude,1);
                    destaddress=destaddresses.get(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }}
            @Override
            public void onError(Status status) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        adconfirmtraveldetails.show();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),""+parent.getItemAtPosition(position),Toast.LENGTH_SHORT).show();
        Loadweight=parent.getItemAtPosition(position).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
