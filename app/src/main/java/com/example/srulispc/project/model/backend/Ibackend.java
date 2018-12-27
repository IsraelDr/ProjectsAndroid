package com.example.srulispc.project.model.backend;

import com.example.srulispc.project.model.entities.CustomLocation;
import com.example.srulispc.project.model.entities.Ride;

public interface Ibackend {
    String addRide(Ride newRide);
    void updatelocation(String id, CustomLocation c);
}
