package com.location.jobservice.admin;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity {
    private static final String collection = "Locations";
    private static final String sField = "sendData";
    private String target = "";
    private SwitchCompat pauseSwitch;
    private boolean isSendingData = false;
    private FirebaseFirestore fStore;
    DocumentReference docReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.set_toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().getExtras() != null) {
            target = getIntent().getExtras().getString("name");
        }

        pauseSwitch = findViewById(R.id.set_pauseSwitch);
        fStore = FirebaseFirestore.getInstance();
        docReference = fStore.collection(collection).document(target);

        // Get Last sendData Status
        docReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                pauseSwitch.setChecked(document.getBoolean(sField));
                            }
                        }
                    }
                });

        pauseSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSendingData = pauseSwitch.isChecked();
                changeSendData();
            }
        });

    }

    private void changeSendData() {
        Map<String, Boolean> send = new HashMap<>();
        send.put(sField, isSendingData);
        docReference.set(send, SetOptions.merge());
    }

}
