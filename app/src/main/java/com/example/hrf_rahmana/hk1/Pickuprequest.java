package com.example.hrf_rahmana.hk1;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by HRF-RAHMANA on 18-03-2018.
 */

public class Pickuprequest {
    DatabaseReference breakdownref;
    String brkid,pid;

    public Pickuprequest(String brkid, String pid) {
        this.brkid = brkid;
        this.pid = pid;
    }
}
