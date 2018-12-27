package com.example.srulispc.project.model.datasource;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.srulispc.project.model.backend.Ibackend;
import com.example.srulispc.project.model.entities.CustomLocation;
import com.example.srulispc.project.model.entities.Ride;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FireBase implements Ibackend {

    //int counter;
    FirebaseDatabase database;
    DatabaseReference myRef;
    HashMap<String,Ride> rides=new HashMap<String,Ride>();


    public FireBase()
    {
        database = FirebaseDatabase.getInstance();
        myRef=database.getReference("Rides");

        //-------------Get counter from FireBase-------------
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                rides.put(dataSnapshot.getKey(),dataSnapshot.getValue(Ride.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       /* myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //counter = dataSnapshot.getValue(Integer.class);
                HashMap temp2= (HashMap) ((HashMap) ( dataSnapshot.getValue())).get((String) (((HashMap)(dataSnapshot.getValue())).keySet()).toArray()[0]);
                Ride temp=new Ride((String) (temp2.get("clientName")),(String) (temp2.get("clientPhoneNumber")),
                        (String) (temp2.get("clientMail")),null,null);
                rides.put(temp.getClientPhoneNumber(),temp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }


    public String addRide(Ride newRide) {

        //counter++;
        //database.getReference("counter").setValue(counter);

        myRef = database.getReference("Rides");
        String id = myRef.push().getKey();
// create a child with index value
        myRef.child(id).setValue(newRide);
        return id;
    }
    public void updatelocation(String id, CustomLocation c) {

        //counter++;
        //database.getReference("counter").setValue(counter);

        myRef = database.getReference("Rides");
        myRef.child(id).child("sourceLocation").setValue(c);
    }
}
