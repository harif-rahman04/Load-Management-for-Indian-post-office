package com.example.hrf_rahmana.hk1;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import static com.example.hrf_rahmana.hk1.MapsActivity.LOCATION_REQUEST;

public class Adminmap extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{
PlaceAutocompleteFragment autocompleteFragment;
    SupportMapFragment mapFragment;
GoogleMap adminmap;
GoogleApiClient admingooglepaiclient;
LocationRequest adminLocreq;
Marker nearbyvehicle,postmarker,alldrivers,sortedmarker,searchmarker,Foundvehicleidmarker;
Button binc,bdec;
Circle circle;
int wait=0;
ArrayList<LatLng> points;
HashSet<String> Breakdownvehiclelist;
Polyline line;
Boolean Newkeyfound;
HashSet<String> keydriverset = new HashSet<String>();
HashMap<String,String> radiusmap=new HashMap<>();
Boolean vehiclefilter=false,emergecymode=false;
String TAG="--->",todistrictvalueall,loadpercentvalueall,vehicleidvalueall;
Location mlastlocation;  TextView txtid,txtdist,txtto,txtqty,txtfrom,txtvehichleid;TextView txtdisplayrad;
Context context;Boolean districtcheckmatch=false;
    Spinner localityspinner;
    List<Address> infowindowfromaddresses;Address infowindowfromaddres;
    List<Address> postaddresses;Address postaddress;
DatabaseReference availabledriverref,Destinationref,postref,Breakdownref;
    List<String> Districtlist;
int radius=3,j=0,maxradius=50;
    String Filterdistrict="None",Resetstr,searchvehicleid;
    DatabaseReference filterref,Sourceref;Boolean retrievalproblem=false;
    Circle tncircle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminmap);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment2);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        binc=findViewById(R.id.binc);
        bdec=findViewById(R.id.bdec);
        txtdisplayrad=findViewById(R.id.txtrad);
        txtdisplayrad.setText(maxradius+" KM");
        txtdisplayrad.setVisibility(View.INVISIBLE);
        binc.setVisibility(View.INVISIBLE);
        bdec.setVisibility(View.INVISIBLE);
        points = new ArrayList<LatLng>();
        Breakdownvehiclelist=new HashSet<>();
        Districtlist=new ArrayList<String>();
        autocompleteFragment.setHint("Enter address");
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();
        Newkeyfound=false;
        context=getApplicationContext();
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng=place.getLatLng();
                searchmarker=adminmap.addMarker(new MarkerOptions().title(place.getName().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)).position(latLng));
                adminmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                adminmap.animateCamera(CameraUpdateFactory.zoomTo(10));}
            @Override
            public void onError(Status status) {

            }
        });
        localityspinner = (Spinner) findViewById(R.id.localityspinner);
        postref= FirebaseDatabase.getInstance().getReference("Postofficedetails");
        Breakdownref= FirebaseDatabase.getInstance().getReference("Breakdownvehicles");
        availabledriverref= FirebaseDatabase.getInstance().getReference("AvailableDrivers");
        Destinationref=FirebaseDatabase.getInstance().getReference("Destinationinfo");
        Sourceref= FirebaseDatabase.getInstance().getReference("Driversource");
        Districtlist.add("None");
       Destinationref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    Log.d(TAG,areaSnapshot+"");
                    String DistrictName = areaSnapshot.child("District").getValue(String.class);
                    Log.d(TAG,"District:"+DistrictName);
                    Districtlist.add(DistrictName);}
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,Districtlist);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                localityspinner.setAdapter(areasAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
       localityspinner.setSelection(0);
        localityspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
Toast.makeText(getApplicationContext(),""+parent.getItemAtPosition(position),Toast.LENGTH_SHORT).show();
Filterdistrict=""+parent.getItemAtPosition(position);
if(nearbyvehicle!=null)
    nearbyvehicle.remove();
if(sortedmarker!=null)
    sortedmarker.remove();
radius++;
locchange();}
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Filterdistrict="None";
            }
        });
        binc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bdec.setVisibility(View.VISIBLE);
                if(maxradius>100){
                    binc.setVisibility(View.INVISIBLE);
              }
                else {
                maxradius+=25;
                if(circle!=null)
                    circle.remove();
                radius++;
                locchange();
                Log.d("admin","radius increased-->"+maxradius);
                txtdisplayrad.setText(maxradius+" KM");
                    circle = adminmap.addCircle(new CircleOptions()
                            .center(new LatLng(10.379663, 78.820845))
                            .radius(maxradius*1000)
                            .strokeColor(Color.parseColor("#ff0000"))
                            .fillColor(Color.parseColor("#3032CD32")).strokeWidth(4));
                    if(nearbyvehicle!=null)
                    nearbyvehicle.remove();
                    if(sortedmarker!=null)
                        sortedmarker.remove();
            }}
        });
      bdec.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              binc.setVisibility(View.VISIBLE);
              if(maxradius==25){
              bdec.setVisibility(View.INVISIBLE);
              }
              else{
              maxradius-=25;
              radius++;
              if(nearbyvehicle!=null)
                  nearbyvehicle.remove();
              if(circle!=null)
                  circle.remove();
              adminmap.clear();
              keydriverset.clear();
              locchange();
                  Log.d("admin","radius increased-->"+maxradius);
                  txtdisplayrad.setText(maxradius+" KM");
                  if(circle.isVisible())circle.remove();
                  circle = adminmap.addCircle(new CircleOptions()
                          .center(new LatLng(10.379663, 78.820845))
                          .radius(maxradius*1000)
                          .strokeColor(Color.parseColor("#ff0000"))
                          .fillColor(Color.parseColor("#3032CD32")).strokeWidth(4));

          }}
      });
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
        public void onPlaceSelected(Place place) {
            // TODO: Get info about the selected place.
            Log.i("placeautocomplete", "Place: " + place.getName());//get place details here
            LatLng latLng=place.getLatLng();
            adminmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            adminmap.animateCamera(CameraUpdateFactory.zoomTo(10));
           /* new GeoFire(Destinationref).setLocation("",new GeoLocation(latLng.latitude,latLng.longitude))*/;
        }
        @Override
        public void onError(Status status) {
            // TODO: Handle the error.
            Log.i("placeautocomplete", "An error occurred: " + status);
        }
    });
        localityspinner.setVisibility(View.INVISIBLE);
        binc.setVisibility(View.INVISIBLE);
        bdec.setVisibility(View.INVISIBLE);
        txtdisplayrad.setVisibility(View.INVISIBLE);
        mapFragment.getMapAsync(this);
        Log.d(TAG,"calling switch method");
        Log.d(TAG,""+getIntent().getStringExtra("adminmapvalue"));
        switch (getIntent().getStringExtra("adminmapvalue")){
            case "img1":
                break;
            case "img2":
                Log.d(TAG,"img2 called");
                if(tncircle!=null){
                tncircle = adminmap.addCircle(new CircleOptions()
                        .center(new LatLng(10.790483, 78.704673))
                        .radius(400*1000)
                        .strokeColor(Color.parseColor("#ff0000"))
                        .fillColor(Color.parseColor("#3032CD32")).strokeWidth(4));}
                mapmarker();
                break;
            case "img3":
                String str=getIntent().getStringExtra("Vehicleid");
                Log.d(TAG,"vehhid"+str);
                searchvehicle(str);
                break;
            case "img4":
                if(circle!=null)
                    circle.setVisible(false);
                viewalldriver();
                break;
            case "img5":
                break;
            case "img6":
                Log.d(TAG,"img6 called");
                localityspinner.setVisibility(View.VISIBLE);
                binc.setVisibility(View.VISIBLE);
                bdec.setVisibility(View.VISIBLE);
                txtdisplayrad.setVisibility(View.VISIBLE);
                break;
            case "img7":
                break;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(keydriverset!=null)
        keydriverset.clear();
        if(radiusmap!=null)
        radiusmap.clear();
        if(nearbyvehicle!=null)
        nearbyvehicle.remove();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        adminLocreq = new LocationRequest();
        adminLocreq.setInterval(60000);
        adminLocreq.setFastestInterval(60000);
        adminLocreq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUEST);
            // TODO: Consider calling]
            Log.d("admin","permission checking failed in Oncoonected");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(admingooglepaiclient, adminLocreq, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
mlastlocation=location;
locchange();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("admin","onmap ready called");
        adminmap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        adminmap.setMyLocationEnabled(true);
       // adminmap.getUiSettings().setZoomControlsEnabled(true);
        adminmap.getUiSettings().setIndoorLevelPickerEnabled(true);
        adminmap.getUiSettings().setAllGesturesEnabled(true);
        adminmap.getUiSettings().setMapToolbarEnabled(false);
        adminmap.getUiSettings().setRotateGesturesEnabled(true);
        circle = adminmap.addCircle(new CircleOptions()
                .center(new LatLng(10.379663, 78.820845))
                .radius(maxradius*1000)
                .strokeColor(Color.parseColor("#ff0000"))
                .fillColor(Color.parseColor("#3032CD32")).strokeWidth(4));
        buildApigoogleclient();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("admin","permission check failes in onRequestpermission result");
                    return;
                }
            } else {
                // User refused to grant permission. You can add AlertDialog here
                Log.d("admin","permission declined");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            }
        }
    }
    public synchronized void buildApigoogleclient(){
        Log.d("admin","build google api client called");
        admingooglepaiclient=new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        admingooglepaiclient.connect();
    }
    public void locchange(){
        Log.d(TAG,"Loc changed called");
        Log.d(TAG,"Filterdistrict in locchange"+Filterdistrict);
        Log.d(TAG,"radius------>"+radius);
    /*    adminmap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                String getstring = txtid.getText().toString();
                ClipData clip = ClipData.newPlainText("Contact", getstring);
                clipboard.setPrimaryClip(clip);
                if(clipboard.hasPrimaryClip()){
                    Toast.makeText(context,"Copied to clipboard",Toast.LENGTH_SHORT).show();
                }
            }
        });
        adminmap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                String getstring = txtid.getText().toString();
                ClipData clip = ClipData.newPlainText("DRIVERID", getstring);
                clipboard.setPrimaryClip(clip);
                if(clipboard.hasPrimaryClip()){
                    Toast.makeText(context,"Copied to clipboard",Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        //   adminmap.clear();
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> locchange =
                new AsyncTask<Void,Void,Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Log.d(TAG, "doin background called");
                        GeoFire geoFire = new GeoFire(availabledriverref);
                        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(10.379663, 78.820845), radius);
                        geoQuery.removeAllListeners();
                        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                            @Override
                            public void onKeyEntered(final String key, final GeoLocation location) {
                                Log.d(TAG, "OnKey entered-->" + key);
                                final LatLng latLng = new LatLng(location.latitude, location.longitude);
                                if (!Filterdistrict.equals("None")) {
                                    Log.d(TAG, "Entering filter district");
                                    districtcheckmatch=distcheck(key);
                                    Log.d(TAG,"Returned District checkmatch"+districtcheckmatch);
                                   }
                                Log.d(TAG, "Below if statement with distrchmatch" + districtcheckmatch);
                                Newkeyfound = false;
                                Log.d("admin", "KEY ENTERED==>" + key);
                                Log.d("admin", "KEY DRIVERSET==>" + keydriverset);
                                if (keydriverset.isEmpty()) {
                                    Newkeyfound = true;
                                    Log.d("admin", "Keydriverset ids empty");
                                }
                                if (!Newkeyfound) {
                                    for (String str : keydriverset) {
                                        Log.d("admin", "str==>" + str + " key-->" + key);
                                        if (str.equals(key)) {
                                            Newkeyfound = false;
                                            Log.d("admin", "DUPLICATE KEY");
                                            break;
                                        } else {
                                            Newkeyfound = true;
                                        }
                                    }
                                }
                                if (Newkeyfound) {
                                    Log.d(TAG,"Newkey found");
                                    keydriverset.add(key);
                                    Log.d(TAG,"key added"+keydriverset);
                                    Log.d("admin", "map marker entered");
                                    if (districtcheckmatch) {
                                        sortedmarker = adminmap.addMarker(new MarkerOptions().title(key).position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                        districtcheckmatch = false;
                                    } else
                                        nearbyvehicle = adminmap.addMarker(new MarkerOptions().title(key).position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.truck1cpy)));
                                    nearbyvehicle.setVisible(true);
                                    Log.d(TAG,"Radius insertes with key==>"+key+"Radius==> "+radius);
                                    radiusmap.put(key, radius + "");
                                    Log.d("admin", "RADIUSMAP==>" + radiusmap);
                                }
                                adminmap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                    @Override
                                    public View getInfoWindow(Marker marker) {
                                        return null;
                                    }

                                    @Override
                                    public View getInfoContents(Marker marker) {
                                        final View v = getLayoutInflater().inflate(R.layout.adminmarkerinfowindow, null);
                                        Log.d(TAG, "infowindow is called");
                                        v.setLayoutParams(new ActionBar.LayoutParams(480, 450));
                                        txtid = (TextView) v.findViewById(R.id.idtxt);
                                        txtdist = (TextView) v.findViewById(R.id.distid);
                                        txtvehichleid=v.findViewById(R.id.txtvehicleid);
                                        txtfrom = v.findViewById(R.id.txtfrom);
                                        txtto = v.findViewById(R.id.txtto);
                                        txtqty =v.findViewById(R.id.txtqty);
                                        Log.d(TAG, "later called");
                                        txtid.setText(marker.getTitle());
                                          txtdist.setText(radiusmap.get(marker.getTitle()) + " KMS AWAY");
                                          districtloadassigner(marker);
                                          txtqty.setText(loadpercentvalueall);
                                          txtto.setText(todistrictvalueall);
                                          txtvehichleid.setText(vehicleidvalueall);
                                          Log.d(TAG, "Info windowRADIUS-->" + radiusmap.get(marker.getTitle()));
                                        return v;
                                    }});}
                            @Override
                            public void onKeyExited(String key) {
                                Log.d("admin", "onkeyexited called");}
                            @Override
                            public void onKeyMoved(String key, GeoLocation location) {
                                Log.d("admin", "onkeymovedcalled");
                            }
                            @Override
                            public void onGeoQueryReady() {
                                if (radius >= maxradius) {
                                    Log.d(TAG,"Radius gone reset");
                                    radius = 1;
                                    Resetstr="Reset";
                                    keydriverset.clear();
                                    LatLng latLng = new LatLng(10.379663, 78.820845);
                                    adminmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                    adminmap.animateCamera(CameraUpdateFactory.zoomTo(8));
                                } else {
                                    radius += 6;
                                    Log.d(TAG, "Radius is incremented");
                                    Log.d(TAG, "Radius value--> " + radius);
                                    locchange();
                                }}
                            @Override
                            public void onGeoQueryError(DatabaseError error) {
                            }
                        });
                        return null;
                    }};
        if(radius!=1){
            locchange.execute();
        }
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
                            postaddresses= new Geocoder(getApplicationContext(), Locale.getDefault()).getFromLocation(latt,longg,1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        postaddress=postaddresses.get(0);
                        postmarker= adminmap.addMarker(new MarkerOptions().position(new LatLng(latt, longg))
                                .title(postaddress.getLocality()).snippet(postaddress.getAddressLine(0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.postoffice1)).snippet(postaddress.getAddressLine(0)));
                    }
                    LatLng latLng=new LatLng(29.058776, 76.085601);
                    adminmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    adminmap.animateCamera(CameraUpdateFactory.zoomTo(8));
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
public void viewalldriver(){
        Log.d(TAG,"view all driver called");
        if(!Breakdownvehiclelist.isEmpty())
            Breakdownvehiclelist.clear();
        Breakdownref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"BREAKDOWN SNAPSHOT-->"+dataSnapshot);
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d(TAG,"breakdownkeys--->"+child.getKey());
                    Breakdownvehiclelist.add(child.getKey());
                }
                Log.d(TAG,"breakdown vehicles list"+Breakdownvehiclelist);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    availabledriverref.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            for (DataSnapshot child1 : dataSnapshot.getChildren()) {
                Double latt = Double.parseDouble(child1.child("l").child("0").getValue().toString());
                Double longg = Double.parseDouble(child1.child("l").child("1").getValue().toString());
                LatLng latLng=new LatLng(latt,longg);
                for (String str : Breakdownvehiclelist) {
                    Log.d(TAG,"str==>"+str+" child1 "+child1.getKey());
                    if (child1.getKey().equals(str)){
                        Log.d(TAG,"Breakdown vehicle-->"+str);
                        emergecymode = true;}
                }
                if (emergecymode) {
                    Log.d(TAG,"emergency mode called");
                    adminmap.addMarker(new MarkerOptions().position(latLng)
                            .title(child1.toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.warn))).setSnippet("breakdown vehicle");
                    emergecymode = false;
                    continue;
                }

                alldrivers = adminmap.addMarker(new MarkerOptions().position(new LatLng(latt, longg))
                        .title(child1.getKey()).icon(BitmapDescriptorFactory.fromResource(R.drawable.truck1cpy)));
                //brequest.setVisibility(View.VISIBLE);
            }
            if(alldrivers!=null)
            alldrivers.setVisible(true);
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d("locchange","oncancelled");
        }
    });
}
    void redrawLine(){
        adminmap.clear();  //clears all Markers and Polylines
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }
        adminmap.addMarker(new MarkerOptions()); //add Marker in current position
        line = adminmap.addPolyline(options); //add Polyline
    }
    public Boolean distcheck(String key){
        Destinationref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Inside DistrictCheck filter" + dataSnapshot);
                if (dataSnapshot.child("District").getValue().equals(Filterdistrict)) {
                    Log.d(TAG, "District equaled");
                    districtcheckmatch = true;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"oncancelled");
            }

        });
        return districtcheckmatch;
    }
    public void districtloadassigner(Marker marker){
        Destinationref.child(marker.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"districtloadassignercalled");
                todistrictvalueall=dataSnapshot.child("District").getValue().toString();
                loadpercentvalueall=dataSnapshot.child("Loadpercentage").getValue().toString();
                vehicleidvalueall=dataSnapshot.child("VehicleId").getValue().toString();
             //   Log.d(TAG,"onDatachange info window called"+dataSnapshot);
               // Log.d(TAG,"Towards"+dataSnapshot.child("District").getValue());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});
    }
public void searchvehicle(final String vehicleid){
        Log.d(TAG,"Entering search vehicle id");
Destinationref.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Double latt=null,longg=null;
        for (DataSnapshot child1 : dataSnapshot.getChildren()){
            Log.d(TAG,"searhvehicle children"+child1);
           if( child1.child("VehicleId").getValue().equals(vehicleid)){
               searchvehicleid=child1.getValue().toString();
               Log.d(TAG,"vehicle found");
         /*      latt=Double.parseDouble(availabledriverref.child(searchvehicleid).child("l").child("0").get);
               longg=Double.parseDouble(child1.child("l").child("1").getValue().toString());
               Log.d(TAG,"found vehicle==> at"+searchvehicleid);
               Log.d(TAG,"found latt and longg"+latt +longg);*/
               break;
           }}
           setvehicle(searchvehicleid);
       /* if(latt!=null&&longg!=null) {
            LatLng latLng = new LatLng(latt, longg);
            searchmarker = adminmap.addMarker(new MarkerOptions().title(searchvehicleid).icon(BitmapDescriptorFactory.fromResource(R.drawable.track)).position(latLng).title("found vehicle"));
            adminmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            adminmap.animateCamera(CameraUpdateFactory.zoomTo(8));
        }
        else
            Toast.makeText(getApplicationContext(),"latt and longg is null",Toast.LENGTH_SHORT).show();*/
    }
    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});}
public void setvehicle(final String str){
    Log.d(TAG,"setvehicle called");
    availabledriverref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG,""+dataSnapshot);
            Double latt=Double.parseDouble(dataSnapshot.child(str).child("l").child("0").getValue().toString());
            Double longg=Double.parseDouble(dataSnapshot.child(str).child("l").child("1").getValue().toString());
            LatLng latLng = new LatLng(latt, longg);
            searchmarker = adminmap.addMarker(new MarkerOptions().title(searchvehicleid).icon(BitmapDescriptorFactory.fromResource(R.drawable.track)).position(latLng).title("Found vehicle"));
            adminmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            adminmap.animateCamera(CameraUpdateFactory.zoomTo(8));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
}
}
