package com.example.srulispc.project.controller;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.srulispc.project.R;
import com.example.srulispc.project.model.entities.Ride;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addRide = findViewById(R.id.addRide);
        addRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.userdetails_dialog);
                dialog.show();

                Button makeOrder = dialog.findViewById(R.id.dialogMakeorder);
                makeOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Ride newRide = new Ride(
                                ((EditText) dialog.findViewById(R.id.name)).getText().toString(),
                                ((EditText) dialog.findViewById(R.id.phonenumber)).getText().toString(),
                                ((EditText) dialog.findViewById(R.id.mail)).getText().toString()
                        );
                        newRide.setStatus(Ride.Status.AVAILABLE);

                        Toast.makeText(getApplicationContext(),
                                   newRide.getClientName() +
                                        newRide.getClientPhoneNumber() +
                                        newRide.getClientMail(),
                                Toast.LENGTH_LONG).show();
                    }
                });

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
}


