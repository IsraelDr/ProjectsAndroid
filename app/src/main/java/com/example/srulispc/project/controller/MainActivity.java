package com.example.srulispc.project.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srulispc.project.R;
import com.example.srulispc.project.model.backend.BackendFactory;
import com.example.srulispc.project.model.backend.Ibackend;
import com.example.srulispc.project.model.entities.CustomLocation;
import com.example.srulispc.project.model.entities.Ride;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.roger.catloadinglibrary.CatLoadingView;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationChangeListener,
        OnMapReadyCallback, View.OnClickListener {

    public static GoogleMap mMap;
    private static final int REQUEST_ACCESS_LOCATION = 0;
    private Ibackend backend;
    private String id;
    private Dialog dialog;
    CatLoadingView catLoading;
    ArrayList markerPoints = new ArrayList();

    private CustomLocation pickUpLocation = null;
    private CustomLocation targetLocation = new CustomLocation("targetLocation");

    String pickUpAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backend = BackendFactory.getInstance();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        findViewById(R.id.cancelRide).setVisibility(View.INVISIBLE);


        FloatingActionButton FAB = findViewById(R.id.myLocationButton);
        MaterialFancyButton addRide = findViewById(R.id.addRide);
        MaterialFancyButton cancelRide = findViewById(R.id.cancelRide);

        FAB.setOnClickListener(this);
        addRide.setOnClickListener(this);
        cancelRide.setOnClickListener(this);

        sourceAddressAutoComplete();
    }


    public void sourceAddressAutoComplete() {
        PlaceAutocompleteFragment placeAutocompleteFragment;
        placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.pickUpAddress);
        placeAutocompleteFragment.setHint(getString(R.string.pickupaddress));

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                pickUpLocation = new CustomLocation("pickUpLocation");
                pickUpLocation.setLatitude(place.getLatLng().latitude);
                pickUpLocation.setLongitude(place.getLatLng().longitude);
                pickUpAddress = place.getAddress().toString();
            }

            @Override
            public void onError(Status status) {

            }
        });
    }


    public void openDialog() {
        dialog = new Dialog(MainActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.userdetails_dialog);
        dialog.show();

        MaterialFancyButton makeOrder = dialog.findViewById(R.id.dialogMakeorder);
        MaterialFancyButton cancelOrder = dialog.findViewById(R.id.dialogCancelOrder);
        makeOrder.setOnClickListener(this);
        cancelOrder.setOnClickListener(this);

        TextView textView = dialog.findViewById(R.id.pickUpAddressTxt);
        if (pickUpAddress=="")
            textView.setText("כתובת איסוף: מיקומך הנוכחי");
        else
            textView.setText("כתובת איסוף: " + pickUpAddress);

        targetAddressAutoComplete();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                FragmentManager fm = getFragmentManager();
                Fragment fragment1 = (fm.findFragmentById(R.id.dialogTargetAddress));
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment1);
                ft.commit();
            }
        });
    }

    public void makeOrderButtonClicked(final Dialog dialog) {


        EditText name = dialog.findViewById(R.id.name);
        EditText phoneNum = dialog.findViewById(R.id.phonenumber);
        EditText mail = dialog.findViewById(R.id.mail);

        if (!inputCheck(name,phoneNum,mail))
            return;

        Ride newRide = new Ride(
                name.getText().toString(),
                phoneNum.getText().toString(),
                mail.getText().toString(),
                targetLocation);

        //Set the PickUp location
        if (pickUpLocation != null)
            newRide.setSourceLocation(pickUpLocation);
        else {
            if (mMap.getMyLocation() != null)
                newRide.setSourceLocation(new CustomLocation(mMap.getMyLocation()));
            else
                /*GPS is not enabled -> The source location is empty*/
                newRide.setSourceLocation(new CustomLocation());
        }

        newRide.setStatus(Ride.Status.AVAILABLE);
        id = backend.addRide(newRide, new Ibackend.Action() {
            @Override
            public void onSuccess(Object obj) {
                catLoading.dismiss();
                Toast.makeText(getApplicationContext(), "The Driver is one his way", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception exception) { }
            @Override
            public void onProgress(String status, double percent) { }
        });
        dialog.dismiss();

        //--------------------------Add Destination-Marker------------------------------
        if (markerPoints.size() > 1) {
            markerPoints.clear();
            mMap.clear();
        }

        // Adding new item to the ArrayList
        markerPoints.add(new LatLng(targetLocation.getLatitude(), targetLocation.getLongitude()));

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(new LatLng(targetLocation.getLatitude(), targetLocation.getLongitude()));

        if (markerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (markerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2
        mMap.addMarker(options);

        //--------------------------Replace AddRide with CancelRIde and waits ------------------------------
        findViewById(R.id.addRide).setVisibility(View.INVISIBLE);
        MaterialFancyButton cancelRide = findViewById(R.id.cancelRide);
        cancelRide.setVisibility(View.VISIBLE);
        (findViewById(R.id.addRide)).setVisibility(View.INVISIBLE);
        (findViewById(R.id.cancelRide)).setVisibility(View.VISIBLE);

        catLoading = new CatLoadingView();
        catLoading.setText(getString(R.string.searchDriver));
        catLoading.show(getSupportFragmentManager(), "");
        catLoading.setCanceledOnTouchOutside(false);
    }

    public void targetAddressAutoComplete() {
        PlaceAutocompleteFragment placeAutocompleteFragment;
        placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.dialogTargetAddress);
        placeAutocompleteFragment.setHint(getString(R.string.targetaddress));

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                targetLocation.setLatitude(place.getLatLng().latitude);
                targetLocation.setLongitude(place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
            }
        });
    }


    private Boolean inputCheck(EditText name, EditText phoneNum, EditText mail) {

        Boolean flag = true;

        if ( name.getText().toString().equals("") ) {
            name.setError("הכנס שם!");
            flag = false;
        }

        if ( phoneNum.getText().toString().equals("") ) {
            phoneNum.setError("הכנס מספר טלפון!");
            flag = false;
        }

        if ( mail.getText().toString().equals("") ) {
            mail.setError("הכנס כתובת מייל!");
            flag = false;
        }


        Location findme = mMap.getMyLocation();
        if (findme == null) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.locationrequired);
            alertDialog.setMessage(R.string.findLocationError);
            alertDialog.setNegativeButton(R.string.ok,null);
            alertDialog.show();
            flag = false;
        }


        return flag;
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;

        // TODO: Before enabling the My Location layer, you must request
        // location permission from the user. This sample does not include
        // a request for location permission.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_LOCATION);

        } else {
            this.getGPS();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    if (id != null)
                        backend.updatelocation(id, new CustomLocation(location));
                }
            });

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_LOCATION) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                this.getGPS();
                //mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);
                mMap.setOnMyLocationClickListener(this);

            } else {
                // Permission was denied. Display an error message.
            }
        }
    }

    private void getGPS() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Title
            alertDialog.setTitle(R.string.locationrequired);

            // Setting Dialog Message
            alertDialog.setMessage(R.string.locationdescription);

            // On pressing Settings button
            alertDialog.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 1);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            // Showing Alert Message
            alertDialog.show();
        }
    }
    @SuppressLint("MissingPermission")
    public void onActivityResult ( int requestCode, int resultCode, Intent result){

        //mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
    }
    @Override
    public void onMyLocationClick (@NonNull Location location){
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick () {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onPointerCaptureChanged ( boolean hasCapture){
        int k = 5;
    }

    @Override
    public void onMyLocationChange (Location location){
        backend.updatelocation(id, (CustomLocation) location);
    }

    @Override
    public void onClick (View view){

        switch (view.getId()) {

            case R.id.addRide:
                openDialog();
                break;

            case R.id.myLocationButton:

                Location findme = mMap.getMyLocation();
                if (findme != null) {
                    LatLng latLng = new LatLng(findme.getLatitude(), findme.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                    mMap.animateCamera(cameraUpdate);
                } else
                    Toast.makeText(getApplicationContext(), R.string.findLocationError, Toast.LENGTH_SHORT).show();
                break;

            case R.id.dialogMakeorder:
                makeOrderButtonClicked(dialog);
                break;

            case R.id.dialogCancelOrder:
                dialog.dismiss();
                break;

            case R.id.cancelRide:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                alertDialog.setTitle(R.string.areyousure);
                alertDialog.setMessage(R.string.areyousure);

                // On pressing Settings button
                alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        backend.cancelride(id);
                        findViewById(R.id.addRide).setVisibility(View.VISIBLE);
                        MaterialFancyButton cancelRide = findViewById(R.id.cancelRide);
                        cancelRide.setVisibility(View.INVISIBLE);
                        dialog.cancel();
                    }
                });

                // on pressing cancel button
                alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                // Showing Alert Message
                alertDialog.show();

                break;

        }
    }
}



