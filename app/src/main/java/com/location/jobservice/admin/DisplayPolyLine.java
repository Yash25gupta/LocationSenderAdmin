package com.location.jobservice.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DisplayPolyLine extends AppCompatActivity implements OnMapReadyCallback, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "DisplayPolyLine";
    private static final String collection = "Locations";
    private boolean isShowingBoard = true;
    private LinearLayout settingsLayout;
    private FirebaseFirestore fStore;
    private String target;
    private List<Map<String, Double>> historyList = new ArrayList<>();

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

        Toolbar toolbar = findViewById(R.id.pl_toolbar);
        setSupportActionBar(toolbar);
        if (getIntent().getExtras() == null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            target = getIntent().getExtras().getString("name");
        }

        seekWidth = findViewById(R.id.pl_seekWidth);
        seekRed = findViewById(R.id.pl_seekRed);
        seekGreen = findViewById(R.id.pl_seekGreen);
        seekBlue = findViewById(R.id.pl_seekBlue);
        btnDraw = findViewById(R.id.pl_btnDraw);
        btnClear = findViewById(R.id.pl_btnClear);
        settingsLayout = findViewById(R.id.pl_settingsLayout);
        fStore = FirebaseFirestore.getInstance();

        // Initialize SupportMapFragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.pl_googleMap);
        supportMapFragment.getMapAsync(this);

        // Get History List
        fStore.collection(collection).document(target)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                historyList = (List<Map<String, Double>>) document.get("History");
                                // history: [{Lng=11.11, Lat=11.11}, {Lng=22.22, Lat=22.22}, {Lng=78.06139, Lat=27.889661}]
                                for (Map<String, Double> map : historyList){
                                    // map: {Lng=11.11, Lat=11.11}
                                    LatLng latLng = new LatLng(map.get("Lat"), map.get("Lng"));
                                    MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                                    Marker marker = gMap.addMarker(markerOptions);
                                    markerList.add(marker);
                                    latLngList.add(latLng);
                                }

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // latLngList==> [lat/lng: (22.545407609795284,1.2740881741046906), lat/lng: (15.715659846482021,17.871770225465298)]
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
        /*gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Create MarkerOptions
                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                // Create Marker
                Marker marker = gMap.addMarker(markerOptions);
                // Add LatLng and Marker
                latLngList.add(latLng);
                markerList.add(marker);
            }
        });*/
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnu_home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            case R.id.mnu_eye:
                if (isShowingBoard) {
                    settingsLayout.setVisibility(View.GONE);
                    isShowingBoard = false;
                } else {
                    settingsLayout.setVisibility(View.VISIBLE);
                    isShowingBoard = true;
                }
                return true;
            case R.id.mnu_menu:
                Toast.makeText(this, "Menu", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.mnu_menu2:
                Toast.makeText(this, "Menu2", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
