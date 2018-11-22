package com.example.srulispc.project.model.datasource;

import com.example.srulispc.project.model.backend.Ibackend;
import com.example.srulispc.project.model.entities.Ride;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBase implements Ibackend {

    static int counter=1;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public FireBase()
    {
        database = FirebaseDatabase.getInstance();
        myRef=database.getReference("counter");
        counter=Integer.parseInt(myRef.getKey());
    }
    public void addRide(Ride newRide) {

        myRef = database.getReference("Ride-"+counter);
        myRef.setValue(newRide);
        counter++;


    }
}
