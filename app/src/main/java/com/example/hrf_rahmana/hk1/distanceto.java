package com.example.hrf_rahmana.hk1;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by HRF-RAHMANA on 19-03-2018.
 */

public class distanceto {
        public double CalculationByDistance(LatLng StartP, LatLng EndP) {
            double distance;
            Location locationA = new Location("");
            locationA.setLatitude(StartP.latitude);
            locationA.setLongitude(StartP.longitude);

            Location locationB = new Location("");
            locationB.setLatitude(EndP.latitude);
            locationB.setLongitude(EndP.longitude);

            distance = locationA.distanceTo(locationB);
            return distance;
    }

}
