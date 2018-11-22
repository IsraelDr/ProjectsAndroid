package com.example.srulispc.project.model.entities;


import android.location.Location;

import java.util.Date;

public class Ride {

    public enum  Status {
        AVAILABLE,
        BUSY,
        FINISHED
    }

    private Status status;
    private Location targetLocation;
    private Location sourceLocation;
    private Date rideStartTime;
    private Date rideFinishTime;
    private String clientName;
    private String clientPhoneNumber;
    private String clientMail;

    public Ride(String name, String phoneNumber, String mail, Location sourcelocation) {
        this.clientName= name;
        this.clientPhoneNumber = phoneNumber;
        this.clientMail = mail;
        this.sourceLocation=sourcelocation;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    public Location getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(Location sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public Date getRideStartTime() {
        return rideStartTime;
    }

    public void setRideStartTime(Date rideStartTime) {
        this.rideStartTime = rideStartTime;
    }

    public Date getRideFinishTime() {
        return rideFinishTime;
    }

    public void setRideFinishTime(Date rideFinishTime) {
        this.rideFinishTime = rideFinishTime;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    public String getClientMail() {
        return clientMail;
    }

    public void setClientMail(String clientMail) {
        this.clientMail = clientMail;
    }
}
