package com.location.jobservice.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final String collection = "Locations";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private View headerView;
    private LinearLayout mainLayout;
    private LayoutInflater inflater;
    private int intNum = 0;

    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.mn_DrawerLayout);
        navigationView = findViewById(R.id.mn_navView);
        toolbar = findViewById(R.id.mn_toolbar);
        mainLayout = findViewById(R.id.mn_LinearLayout);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        headerView = navigationView.getHeaderView(0);
        fStore = FirebaseFirestore.getInstance();

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.mnu_home);

        fStore.collection(collection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                createCardView(document);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void createCardView(QueryDocumentSnapshot document){
        String Name = document.getId().trim();
        String SendData = document.get("sendData").toString().trim();
        String Lat = document.get("current.Lat").toString().trim();
        String Lng = document.get("current.Lng").toString().trim();
        String LastTime = "Last Update";
        String LastTimeTxt = "lastTimeHere";
        String latLng = "Lat: " + Lat + " Lng: " + Lng;

        // Create multiple card view
        View tempView = inflater.inflate(R.layout.list_templete, null);
        TextView txtName = tempView.findViewById(R.id.tmp_name);
        TextView txtSend = tempView.findViewById(R.id.tmp_sendingData);
        TextView txtLatLng = tempView.findViewById(R.id.tmp_currentLatLng);
        TextView txtLastTime = tempView.findViewById(R.id.tmp_lastUpdate);
        TextView txtLastTimeTxt = tempView.findViewById(R.id.tmp_lastUpdateTxt);
        TextView txtTouch = tempView.findViewById(R.id.tmp_touchTV);
        ImageView imgPhone = tempView.findViewById(R.id.tmp_img);

        txtName.setText(Name);
        txtSend.setText(SendData);
        txtLatLng.setText(latLng);
        txtLastTime.setText(LastTime);
        txtLastTimeTxt.setText(LastTimeTxt);
        imgPhone.setImageResource(R.drawable.ic_smartphone_black);
        txtTouch.setOnClickListener(cardSelected);
        txtTouch.setId(intNum);
        txtTouch.setTag(Name);

        intNum++;
        mainLayout.addView(tempView);
    }

    View.OnClickListener cardSelected = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String id = String.valueOf(v.getId());
            String name = (String) v.getTag();
            Toast.makeText(MainActivity.this, id + " " + name, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), DisplayPolyLine.class);
            intent.putExtra("name", name);
            startActivity(intent);
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
