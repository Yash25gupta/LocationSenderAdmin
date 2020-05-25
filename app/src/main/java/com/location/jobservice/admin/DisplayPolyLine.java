package com.location.jobservice.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class DisplayPolyLine extends AppCompatActivity implements OnMapReadyCallback, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "DisplayPolyLine";
    // Initialize Variable
    GoogleMap gMap;
    SeekBar seekWidth, seekRed, seekGreen, seekBlue;
    Button btnDraw, btnClear;

    Polyline polyline = null;
    List<LatLng> latLngList = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();

    int red = 0, green = 0, blue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_poly_line);

        seekWidth = findViewById(R.id.pl_seekWidth);
        seekRed = findViewById(R.id.pl_seekRed);
        seekGreen = findViewById(R.id.pl_seekGreen);
        seekBlue = findViewById(R.id.pl_seekBlue);
        btnDraw = findViewById(R.id.pl_btnDraw);
        btnClear = findViewById(R.id.pl_btnClear);

        // Initialize SupportMapFragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.pl_googleMap);
        supportMapFragment.getMapAsync(this);

        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "latLngList: " + latLngList);
                // latLngList: [lat/lng: (22.545407609795284,1.2740881741046906), lat/lng: (15.715659846482021,17.871770225465298)]
                // Draw Polyline on Map
                if (polyline != null) polyline.remove();
                // Create PolylineOptions
                PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngList).clickable(true);
                polyline = gMap.addPolyline(polylineOptions);
                // set Polyline Color
                polyline.setColor(Color.rgb(red, green, blue));
                setWidth();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear All
                if (polyline != null) polyline.remove();
                for (Marker marker : markerList) marker.remove();
                latLngList.clear();
                markerList.clear();
                seekWidth.setProgress(3);
                seekRed.setProgress(0);
                seekGreen.setProgress(0);
                seekBlue.setProgress(0);
            }
        });

        seekRed.setOnSeekBarChangeListener(this);
        seekGreen.setOnSeekBarChangeListener(this);
        seekBlue.setOnSeekBarChangeListener(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Create MarkerOptions
                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                // Create Marker
                Marker marker = gMap.addMarker(markerOptions);
                // Add LatLng and Marker
                latLngList.add(latLng);
                markerList.add(marker);
                Log.d(TAG, "latLng: " + latLng);
                // latLng: lat/lng: (15.715659846482021,17.871770225465298)
            }
        });
    }

    private void setWidth() {
        seekWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Get Seekbar Progess
                int width = seekWidth.getProgress();
                if (polyline != null)
                    // Set Polyline Width
                    polyline.setWidth(width);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.pl_seekRed:
                red = progress;
                break;
            case R.id.pl_seekGreen:
                green = progress;
                break;
            case R.id.pl_seekBlue:
                blue = progress;
                break;
        }
        // set Polyline Color
        polyline.setColor(Color.rgb(red, green, blue));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
