package com.example.medilocate_plus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Vector;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    MarkerOptions marker;
    LatLng centerlocation;
    private String URL = "http://10.20.141.198/mapict602/all.php";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 200;

    RequestQueue requestQueue;
    Gson gson;
    Information[] information;
    Vector<MarkerOptions> markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gson = new GsonBuilder().create();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        centerlocation = new LatLng(3.0,101);

        markerOptions = new Vector<>();

        markerOptions.add(new MarkerOptions()
                .title("Tuanku Fauziah Hospital, Kangar, Perlis")
                .position(new LatLng(6.44113, 100.19131))
                .snippet("3, Jalan Tun Abdul Razak, Pusat Bandar Kangar, 01000 Kangar, Perlis")

        );

        markerOptions.add(new MarkerOptions()
                .title("Hospital Sultanah Bahiyah, Alor Setar")
                .position(new LatLng(6.15159, 100.40578))
                .snippet("Km 6, Jln Langgar, Bandar, 05460 Alor Setar, Kedah")

        );

        markerOptions.add(new MarkerOptions()
                .title("Peneng General Hospital")
                .position(new LatLng(5.41712, 100.31129))
                .snippet("Jalan Residensi, 10990 George Town, Pulau Pinang")

        );

        markerOptions.add(new MarkerOptions()
                .title("Raja Permaisuri Bainun Hospital")
                .position(new LatLng(4.62748, 101.09171))
                .snippet("Jalan Raja Ashman Shah, 30450 Ipoh, Perak")

        );



    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        for (MarkerOptions mark : markerOptions) {
            mMap.addMarker(mark);
        }

        enableMyLocation();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerlocation,8));
        sendRequest();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
    }

    public void sendRequest(){
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,URL,onSuccess,onError);
        requestQueue.add(stringRequest);


    }

    public Response.Listener<String> onSuccess = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            information = gson.fromJson(response, Information[].class);

            Log.d("Information", "Number of Information : " + information.length);

            if  (information.length <1) {
                Toast.makeText(getApplicationContext(), "Problem retrieving JSON data", Toast.LENGTH_SHORT).show();
                return;
            }

            for (Information info: information) {
                Double lat = Double.parseDouble(info.lat);
                Double lng = Double.parseDouble(info.lng);
                String title = info.name;
                String snippet = info.description;



                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat,lng))
                        .title(title)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                mMap.addMarker(marker);

            }

        }
    };

    public Response.ErrorListener onError = new Response.ErrorListener(){

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("VolleyError", "Error in Volley request", error);
            Toast.makeText(getApplicationContext(), "Error in Volley request", Toast.LENGTH_LONG).show();

        }
    };

    private static class ActivityMapsBinding {
        public static ActivityMapsBinding inflate(LayoutInflater layoutInflater) {
            return null;
        }

        public int getRoot() {
            return 0;
        }
    }
}