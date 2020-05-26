package com.location.jobservice.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class Settings extends AppCompatActivity {
    private String target = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.set_toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().getExtras() != null) {
            target = getIntent().getExtras().getString("name");
        }

        Toast.makeText(this, target, Toast.LENGTH_SHORT).show();
    }
}
