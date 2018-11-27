//hgfgjhfjh
//jyjhgjd
package com.example.srulispc.project.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.srulispc.project.R;
import com.example.srulispc.project.model.backend.BackendFactory;
import com.example.srulispc.project.model.backend.Ibackend;
import com.example.srulispc.project.model.entities.Ride;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,GoogleMap.OnMyLocationChangeListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_ACCESS_LOCATION = 0;
    private Ibackend backend;

    private Location targetLocation = new Location("targetLocation");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backend = BackendFactory.getInstance();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleApiClient

        Button addRide = findViewById(R.id.addRide);
        addRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.userdetails_dialog);
                dialog.show();



                //---------------Auto complete setting-----------------------
                PlaceAutocompleteFragment placeAutocompleteFragment;
                placeAutocompleteFragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById( R.id.autocomplete );
                placeAutocompleteFragment.setHint("כתובת  יעד");

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

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        FragmentManager fm = getFragmentManager();
                        Fragment fragment = (fm.findFragmentById(R.id.autocomplete));
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.remove(fragment);
                        ft.commit();
                    }
                });
                //---------------Make Order Button---------------------------
                Button makeOrder = dialog.findViewById(R.id.dialogMakeorder);
                makeOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Ride newRide = new Ride(
                                ((EditText) dialog.findViewById(R.id.name)).getText().toString(),
                                ((EditText) dialog.findViewById(R.id.phonenumber)).getText().toString(),
                                ((EditText) dialog.findViewById(R.id.mail)).getText().toString(),
                                mMap.getMyLocation(),
                                targetLocation
                        );
                        newRide.setStatus(Ride.Status.AVAILABLE);
                        backend.addRide(newRide);
                    }
                });
                //---------------Cancel Order Button-------------------------
                Button cancelOrder = dialog.findViewById(R.id.dialogCancelOrder);
                cancelOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
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
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);

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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(R.string.locationrequired);

        // Setting Dialog Message
        alertDialog.setMessage(R.string.locationdescription);

        // On pressing Settings button
        alertDialog.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent,1);
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
    @SuppressLint("MissingPermission")
    public void onActivityResult(int requestCode, int resultCode, Intent result) {

        //mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
    }
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        int k=5;
    }

    @Override
    public void onMyLocationChange(Location location) {
        int k=5;
    }
}


