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

public class FireBase implements Ibackend {

    //int counter;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Ride ride=new Ride();
    String id;


    public FireBase()
    {
        database = FirebaseDatabase.getInstance();
        myRef=database.getReference("Rides");

        //-------------Get counter from FireBase-------------
        /*myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ride=dataSnapshot.getValue(Ride.class);
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
        });*/
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


    public String addRide(Ride newRide, final Action action) {
        myRef = database.getReference("Rides");
        id = myRef.push().getKey();
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(id))
                    ride=dataSnapshot.getValue(Ride.class);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(id))
                    ride=dataSnapshot.getValue(Ride.class);
                if(ride.getStatus()== Ride.Status.BUSY)
                    action.onSuccess(null);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getKey().equals(id))
                    ride=null;
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        newRide.setRidekey(id);
        myRef.child(id).setValue(newRide);
        return id;
    }
    public void updatelocation(String id, CustomLocation c) {

        //counter++;
        //database.getReference("counter").setValue(counter);

        //myRef = database.getReference("Rides");
        if(ride!=null)
            myRef.child(id).child("sourceLocation").setValue(c);
    }

    @Override
    public void cancelride(String id) {
        myRef = database.getReference("Rides");
        myRef.child(id).removeValue();
    }

}
