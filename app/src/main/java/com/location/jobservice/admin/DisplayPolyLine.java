package com.location.jobservice.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DisplayPolyLine extends AppCompatActivity implements OnMapReadyCallback {
    private static final String collection = "Locations";
    private String target;
    private List<Map<String, Double>> historyList = new ArrayList<>();

    private GoogleMap gMap;
    private Polyline polyline = null;
    private List<LatLng> latLngList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_poly_line);

        Toolbar toolbar = findViewById(R.id.pl_toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().getExtras() == null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            target = getIntent().getExtras().getString("name");
        }

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.pl_googleMap);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }

        // Get History List
        fStore.collection(collection).document(target)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                historyList = (List<Map<String, Double>>) document.get("History");
                                int intNum = 0;
                                LatLng latLng = null;
                                assert historyList != null;
                                for (Map<String, Double> map : historyList) {
                                    //noinspection ConstantConditions
                                    latLng = new LatLng(map.get("Lat"), map.get("Lng"));

                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(latLng);
                                    markerOptions.title("Pos" + intNum);
                                    Marker marker = gMap.addMarker(markerOptions);

                                    markerList.add(marker);
                                    latLngList.add(latLng);
                                    intNum++;
                                }
                                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            }
                        }
                    }
                });

    }

    private void DrawPolyline() {
        if (polyline != null) polyline.remove();
        PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngList).clickable(true);
        polyline = gMap.addPolyline(polylineOptions);
        polyline.setColor(Color.BLACK);
        polyline.setWidth(3);
    }

    private void clearMap() {
        if (polyline != null) polyline.remove();
        for (Marker marker : markerList) marker.remove();
        latLngList.clear();
        markerList.clear();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
    }

    // Add Menu in ToolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu2, menu);
        return true;
    }

    // Add Action to ToolBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mn2_home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            case R.id.mn2_drawPolyLine:
                if (polyline != null) {
                    polyline.remove();
                    polyline = null;
                } else {
                    DrawPolyline();
                }
                return true;
            case R.id.mn2_setting:
                Intent i = new Intent(getApplicationContext(), Settings.class);
                i.putExtra("name", target);
                startActivity(i);
                return true;
            case R.id.mn2_clear:
                clearMap();
                return true;
            case R.id.mn2_about:
                Toast.makeText(this, "About Us", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
