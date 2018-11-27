package com.example.srulispc.project.model.datasource;

import android.media.audiofx.Visualizer;
import android.support.annotation.NonNull;

import com.example.srulispc.project.model.backend.Ibackend;
import com.example.srulispc.project.model.entities.Ride;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FireBase implements Ibackend {

    int counter;
    FirebaseDatabase database;
    DatabaseReference myRef;


    public FireBase()
    {
        database = FirebaseDatabase.getInstance();
        myRef=database.getReference("counter");

        //-------------Get counter from FireBase-------------
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                counter = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void addRide(Ride newRide) {

        counter++;
        database.getReference("counter").setValue(counter);

        myRef = database.getReference("Ride-"+ counter);
        myRef.setValue(newRide);
    }
}
