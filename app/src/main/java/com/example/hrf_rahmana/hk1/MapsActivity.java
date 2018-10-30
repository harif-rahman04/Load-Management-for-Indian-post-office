package com.example.hrf_rahmana.hk1;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    SupportMapFragment mapFragment;private GoogleMap mMap;GoogleApiClient mgoogleapiclient;
    Location mlastlocation,locchangeloc,NewLocation;
    public static final int LOCATION_REQUEST = 500;
    LocationRequest mLocationRequest;
    String Name;
    Button getdirebutton;
    String TAG="Test=====>";
    String driverfoundid;
    ArrayList<LatLng> dirpoints = new ArrayList<>();
    String userid = null,Loadweight;
    String outwardloadweight;
    private int radius=1;
    Boolean driverfound =false,emergecymode=false,vehiclesentornot;
    ProgressDialog pd;
    DatabaseReference driverreference,Emergencydrivers,pickupreference,availabledriverref,Destinationref,postref,cirumferenceref;
    PlaceAutocompleteFragment autocompleteFragment;
    GeoFire geoFirsource;
    HashSet<String> keydriverset = new HashSet<String>();
    List<Address> addresses;Address address;Context context;Marker postmark,markerdriver,searchmarker,Emergencymarker,dirsourcemarker,dirdestmarker;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        context=getApplicationContext();
        getdirebutton=findViewById(R.id.bgetdir);
        userid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        keydriverset.add(userid);
        driverreference = FirebaseDatabase.getInstance().getReference("AvailableDrivers").child(userid);
        availabledriverref=FirebaseDatabase.getInstance().getReference("AvailableDrivers");
        pickupreference=FirebaseDatabase.getInstance().getReference("Pickupdetails");
        Destinationref=FirebaseDatabase.getInstance().getReference("Destinationinfo");
        postref= FirebaseDatabase.getInstance().getReference("Postofficedetails");
        cirumferenceref=FirebaseDatabase.getInstance().getReference("Haryana");
        pd=new ProgressDialog(MapsActivity.this);
        geoFirsource = new GeoFire(availabledriverref);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Enter address");
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .setCountry("INDIA")
                .build();
        autocompleteFragment.setFilter(autocompleteFilter);
        AsyncTask<Void,Void,Void> locchange;
        getdirebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdirection();
            }
        });
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                if(searchmarker!=null)
                    searchmarker.remove();
                Log.i("test", "Place: " + place.getName());//get place details here
                LatLng latLng=place.getLatLng();
                searchmarker=mMap.addMarker(new MarkerOptions().title(place.getName().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)).position(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("placeautocomplete", "An error occurred: " + status);
            }
        });
    /*    etsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            String searchstr;Address address;
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH||actionId==EditorInfo.IME_ACTION_DONE||event.getAction()==KeyEvent.ACTION_DOWN)
                    searchstr=etsearch.getText().toString();
                Geocoder geocoder=new Geocoder(MapsActivity.this);
                List<Address> listad=new ArrayList<>();
                try{
                    listad=geocoder.getFromLocationName(searchstr,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(listad.size()>0)
                    address=listad.get(0);
                LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
                return false;
            }
        });*/
        mapFragment.getMapAsync(this);
        buildApigoogleclient();
switch (getIntent().getStringExtra("value")) {
    case "img1":
        Log.d(TAG,"1st case called");
        break;
  /*  case "img2":
        Log.d(TAG,"2nd case called");
        getclosestdriver();
        break;*/
    case "img3":
        mapmarker();
        break;
    case "img4":
        emergecymode=true;
        buildApigoogleclient();
        break;
    case "img5":
        //searchcircumference();
        break;
    case "img6":
        break;
}
    }
    public void requestclosestdriver(){
        driverfound=false;
        getclosestdriver();
    }
    public void getdirection(){
    if(mlastlocation==null){
        Toast.makeText(getApplicationContext(),"mlastlocation is null",Toast.LENGTH_SHORT).show();
    }else {
        Log.d(TAG, "mlastlocation is not null");
        dirpoints.add(new LatLng(mlastlocation.getLatitude(), mlastlocation.getLongitude()));
        Log.d(TAG, "dirpointd1-->" + dirpoints);
        Destinationref.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Datasnapshot in -->" + dataSnapshot);
                Double latt = Double.parseDouble(dataSnapshot.child("l").child("0").getValue().toString());
                Log.d(TAG, "lat-->" + latt);
                Double longg = Double.parseDouble(dataSnapshot.child("l").child("1").getValue().toString());
                Log.d(TAG, "longg-->" + longg);
                dirpoints.add(new LatLng(latt, longg));
                Log.d(TAG, "dirpointd2-->" + dirpoints);
                dirdestmarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latt, longg))
                        .title("destination").icon(BitmapDescriptorFactory.fromResource(R.drawable.source)));
                Log.d(TAG, "dirpontssize" + dirpoints.size());
                if (dirpoints.size() == 2) {
                    new DownloadTask(getApplicationContext(), mMap).execute(geturl(dirpoints.get(0), dirpoints.get(1)));
                    dirsourcemarker = mMap.addMarker(new MarkerOptions().position(dirpoints.get(0))
                            .title("source").icon(BitmapDescriptorFactory.fromResource(R.drawable.destination)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(dirpoints.get(0)));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(9));
                 /*   Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.rootrelativedriver), "Distance :"+new distdur().dist+"Duration"+new distdur().dur, Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Snackbar snackbar1 = Snackbar.make(findViewById(R.id.rootrelativedriver), "Message is restored!", Snackbar.LENGTH_SHORT);
                                    snackbar1.show();
                                }
                            });*/
                  //  snackbar.show();
                    if (!dirpoints.isEmpty())
                        dirpoints.clear();
                } else
                    Toast.makeText(getApplicationContext(), "dirpoint is null", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    } }
    public void Distcalc(){
        Toast.makeText(getApplicationContext(),"Distance is"+CalculationByDistance(dirpoints.get(0),dirpoints.get(1))+" KM",Toast.LENGTH_LONG).show();
    }
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        Log.d(TAG,"Calculaton by distance is calculated");
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
    public void getclosestdriver(){
        pd.setMessage("Getting your closest driver");
        pd.show();
GeoFire geoFire=new GeoFire(availabledriverref);
Log.d("fir","mlastlocation"+mlastlocation);
GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(mlastlocation.getLatitude(),mlastlocation.getLongitude()),radius);
geoQuery.removeAllListeners();
geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        Log.d("fir", "onkeyentered called-->> " + keydriverset);
        Log.d("fir","KEY--->"+key);
        for (String str : keydriverset) {
            Log.d("fir","str==>"+str+" key-->"+key);
            if (str.equals(key)) {
                driverfound = false;
                break;
            }
            else{
                driverfound = true;
        }}
        if (!driverfound)
            radius++;
        else {
            Log.d("fir", "DRIVER FOUND");
            driverfoundid = key;
            Log.d("fir", "driverfound id---->" + driverfoundid);
            LatLng latLng = new LatLng(location.latitude, location.longitude);
            pd.dismiss();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(25));
            Toast.makeText(getApplicationContext(), "driver found at " + radius + " KM", Toast.LENGTH_LONG).show();
            if (dirpoints.size() == 1)
                dirpoints.add(latLng);
            else
                dirpoints.set(1, latLng);
            //bgetdir.setVisibility(View.VISIBLE);
            //bdist.setVisibility(View.VISIBLE);
            keydriverset.add(key);
        }
    }
    @Override
    public void onKeyExited(String key) {
Log.d("fir","onkeyexited called");
    }
    @Override
    public void onKeyMoved(String key, GeoLocation location) {
  /*      new GeoFire(driverreference).setLocation(Name, new GeoLocation(location.latitude, location.longitude),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        Log.d("fir", "key" + key + "error" + error);
                    }
                });*/
    }

    @Override
    public void onGeoQueryReady() {
if(!driverfound){
    radius+=3;
    Log.d("fir","radius is incremented");
    Log.d("fir","radius values is "+radius);
getclosestdriver();
    }
    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

    }
});}
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("fir", "map ready called");
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.d("fir", "checking self permisssion failed in onMap ready");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
       // mMap.getUiSettings().setZoomControlsEnabled(true);
     /*   mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
           //     setdestination(latLng);
                // mMap.animateCamera(CameraUpdateFactory.zoomTo(5));
                if(driverreference!=null){
                    if(listpoints.size()==1) {
                        listpoints.add(latLng);}
                    else {
                        listpoints.set(1, latLng);
                        mMap.clear();
                    }
                    Log.d("fir","setting up dest"+latLng);
                    Log.d("fir","in setonmaplclicllist"+listpoints);
                    MarkerOptions markerOptions=new MarkerOptions();
                    markerOptions.position(latLng).title(Name+"destination");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    //  getdirbt.setVisibility(View.VISIBLE);}
                    mMap.addMarker(markerOptions);
                    new GeoFire(driverreference).setLocation("destination", new GeoLocation(latLng.latitude, latLng.longitude),
                            new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    Log.d("fir", "key" + key + "error" + error);
                                }
                            });
                    if(listpoints.size()==2) {
                     bdist.setVisibility(View.VISIBLE);
                        bgetdir.setVisibility(View.VISIBLE);
                    }
                }}});*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("fir", "onPause called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("fir", "onStart called");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("fir", "onRestart called");
    }
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        Log.d("fir", "Onrequestpermission called");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
               if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("fir","permission check failes in onRequestpermission result");
                    return;
                }
                //doLocationAccessRelatedJob();
            } else {
                // User refused to grant permission. You can add AlertDialog here
                Log.d("fir","permission declined");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            }
        }
}
    public synchronized void buildApigoogleclient(){
        Log.d("fir","build google api client called");
        mgoogleapiclient=new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mgoogleapiclient.connect();
    }
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG,"onLocationchanged called"+location);
        locchange(location);
    }
private String geturl(LatLng origin, LatLng dest) {
    // Origin of route
    String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
    // destination of route
    String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
    // Sensor enabled
    String sensor = "sensor=false";
    // Building the parameters to the web service
    String parameters = str_origin + "&" + str_dest + "&" + sensor;
    // Output format
    String output = "json";
    // Building the url to the web service
    String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    return url;
}
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("fir","On connected called");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(60000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUEST);
            // TODO: Consider calling]
            Log.d("fir","permission checking failed in Oncoonected");
            return;
        }
        mlastlocation= LocationServices.FusedLocationApi.getLastLocation(mgoogleapiclient);
       LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleapiclient, mLocationRequest, this);
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("fir"," Onconnectio suspended called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("fir","  Onstop called");
    /*    String userid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("drivers");*/
       /* GeoFire geoFire=new GeoFire(driverreference);
        geoFire.removeLocation(Name);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume called");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("cord", "connection failed");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void mapmarker()  {
        try {
            Log.d("fir","Map marker called");
            postref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child1 : dataSnapshot.getChildren()) {
                        Log.d("fir","MAPCHIL===>"+child1);
                        Double latt = Double.parseDouble(child1.child("l").child("0").getValue().toString());
                        Double longg = Double.parseDouble(child1.child("l").child("1").getValue().toString());
                        try {
                            addresses= new Geocoder(getApplicationContext(), Locale.getDefault()).getFromLocation(latt,longg,1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        address=addresses.get(0);
                       postmark= mMap.addMarker(new MarkerOptions().position(new LatLng(latt, longg))
                                .title(address.getLocality()).snippet(address.getAddressLine(0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.postoffice1)));
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mlastlocation.getLatitude(),mlastlocation.getLongitude())));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "error in postmarking", Toast.LENGTH_SHORT).show();
        }
    }
@SuppressLint("StaticFieldLeak")
public void locchange(Location location) {
    Log.d("fir", "onLocation changed called");
  if(location!=null){
    locchangeloc = location;
    mlastlocation = location;
    AsyncTask<Void,Void,Void> locchange =
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Log.d("async","doin background acceseed");
                    geoFirsource.setLocation(userid, new GeoLocation(locchangeloc.getLatitude(), locchangeloc.getLongitude()),
                            new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    Log.d("locchange", "key" + key + "error" + error);
                                }
                            });
                    availabledriverref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child1 : dataSnapshot.getChildren()) {
                                if(emergecymode){
                                    if(child1.getKey().equals(userid)){
                                        Log.d("locchange","userid equaled");
                                        mMap.addMarker(new MarkerOptions().position(new LatLng(mlastlocation.getLatitude(), mlastlocation.getLongitude()))
                                                .title(Name).icon(BitmapDescriptorFactory.fromResource(R.drawable.emergency))).setSnippet("breakdown vehicle");
                                        continue;
                                    }}
                                Double latt = Double.parseDouble(child1.child("l").child("0").getValue().toString());
                                Double longg = Double.parseDouble(child1.child("l").child("1").getValue().toString());
                                markerdriver=mMap.addMarker(new MarkerOptions().position(new LatLng(latt, longg))
                                        .title(child1.getKey()).icon(BitmapDescriptorFactory.fromResource(R.drawable.truck1cpy)));
                                //brequest.setVisibility(View.VISIBLE);
                            }
                            if(markerdriver!=null)
                            markerdriver.setVisible(true);}
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("locchange","oncancelled");
                        }
                    });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        };

    locchange.execute();}
}
/*
public void searchcircumference(){
    mMap.addCircle(new CircleOptions()
            .center(new LatLng(10.379663, 78.820845))
            .radius(000)
            .strokeColor(Color.parseColor("#ff0000"))
            .fillColor(Color.parseColor("#3032CD32")).strokeWidth(4));
        mMap.addCircle(new CircleOptions().radius())
cirumferenceref.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
            Double latt = Double.parseDouble(dataSnapshot.child("l").child("0").getValue().toString());
            Double longg = Double.parseDouble(dataSnapshot.child("l").child("1").getValue().toString());
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).position(new LatLng(latt,longg)).title(dataSnapshot.getKey()));
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(final Marker marker) {
                    final View v = getLayoutInflater().inflate(R.layout.adminmarkerinfowindow, null);
                    Log.d(TAG, "infowindow is called");
                    v.setLayoutParams(new ActionBar.LayoutParams(380, 350));
                    txtid = (TextView) v.findViewById(R.id.idtxt);
                    txtdist = (TextView) v.findViewById(R.id.distid);
                    Log.d(TAG, "later called");
                    txtid.setText(marker.getTitle());
                    txtdist.setText(radiusmap.get(marker.getTitle()) + " KMS AWAY");
                    loadassigner(marker);
                    txtqty.setText(loadpercentvalueall);
                    txtto.setText(todistrictvalueall);
                    txtvehichleid.setText(vehicleidvalueall);
                    Log.d(TAG, "Info windowRADIUS-->" + radiusmap.get(marker.getTitle()));
                    return v;
                }});
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});
*/
/*public void loadassigner(final Marker marker){
    cirumferenceref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            outwardloadweight=dataSnapshot.child(marker.getTitle()).child("Loadweight").getValue().toString();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    })
}*/
}