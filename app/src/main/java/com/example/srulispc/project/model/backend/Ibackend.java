package com.example.srulispc.project.model.backend;

import com.example.srulispc.project.model.entities.CustomLocation;
import com.example.srulispc.project.model.entities.Ride;

public interface Ibackend {
    interface Action<T>{
        void onSuccess(T obj);
        void onFailure(Exception exception);
        void onProgress(String status,double percent);
    }
    String addRide(Ride newRide, Action action);
    void updatelocation(String id, CustomLocation c);
    void cancelride(String id);
}
