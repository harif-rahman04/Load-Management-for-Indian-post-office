package com.example.hrf_rahmana.hk1;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by HRF-RAHMANA on 06-03-2018.
 */

    public class DownloadTask extends AsyncTask<String, Void, String> {
Context context;GoogleMap map;

    public DownloadTask(Context context, GoogleMap map) {
        this.context = context;
        this.map = map;
    }

    // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            new ParserTask(context,map).execute(result);
        }


        public String downloadUrl(String...strings){
    HttpURLConnection connection = null;
    BufferedReader reader = null;
            Log.d("cord","called");
            Log.d("cord",strings[0]);
            try {
        URL url = new URL(strings[0]);
        connection = (HttpURLConnection) url.openConnection();
        connection.addRequestProperty("apikey", "AIzaSyAdKz_mpQ-oUU81cOgFC106Tf53qgHqnrA");
        connection.connect();
        InputStream stream = connection.getInputStream();
        reader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
            Log.d("cord", "> " + line);   //here u ll get whole response...... :-
        }
        return buffer.toString();
    } catch (MalformedURLException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (connection != null)
            connection.disconnect();
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
            return null;
}
    }
