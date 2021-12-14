 package com.example.currentlocdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


 public class MainFragment extends Fragment {
    //intialize variable
    Button btLocation;
    TextView tvlatitude,tvLongitude;
    FusedLocationProviderClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // intialize view
        View view = inflater.inflate(R.layout.fragment_main,container,false);

        //Assign variable
        btLocation = view.findViewById(R.id.bt_location);
        tvlatitude = view.findViewById(R.id.tv_latitude);
        tvLongitude = view.findViewById(R.id.tv_longitude);

        //Intialize Location client
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity()
                        ,Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getActivity()
                                ,Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

                    getCurrentLocation();
                }else {
                    //when permission is not granted
                    //request permission
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                            ,Manifest.permission.ACCESS_COARSE_LOCATION},100);
                }
            }
        });

        return view;
    }

     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         //Check condition
         if (requestCode == 100 && (grantResults.length > 0) &&
                 (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
             //when permission are granted
             //call method
             getCurrentLocation();
         }else {
             //when permission are denied
             //Display toast
             Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
         }
     }

     @SuppressLint("MissingPermission")
     private void getCurrentLocation() {
        //Initialize Location manager
         LocationManager locationManager = (LocationManager) getActivity()
                 .getSystemService(Context.LOCATION_SERVICE);
         //Check condition
         if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
         || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
             //when location service is enabled
             //Get last location
             client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                 @Override
                 public void onComplete(@NonNull Task<Location> task) {
                     //Initialize locaiton
                     Location location = task.getResult();
                     //Check condition
                     if (location != null) {
                         //when location result is not null
                         //set latitude
                         tvlatitude.setText(String.valueOf(location.getLatitude()));
                         //set longitude
                         tvLongitude.setText(String.valueOf(location.getLongitude()));
                     }else {
                         //when location result is null
                         //initialize location request
                         LocationRequest locationRequest = new LocationRequest()
                                 .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                 .setInterval(10000)
                                 .setFastestInterval(1000)
                                 .setNumUpdates(1);

                         //initialize location call back
                         LocationCallback locationCallback = new LocationCallback() {
                             @Override
                             public void onLocationResult( LocationResult locationResult) {
                                 //intialize location
                                 Location location1 = locationResult.getLastLocation();
                                 //Set Latitude
                                 tvlatitude.setText(String.valueOf(location.getLatitude()));
                                 //set longitude
                                 tvLongitude.setText(String.valueOf(location.getLongitude()));
                             }
                         };
                         //request location updates
                         client.requestLocationUpdates(locationRequest
                                 ,locationCallback, Looper.myLooper());
                     }
                 }
             });
         }else {
             //when location service is not enabled
             //open location setting
             startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                     .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
         }
     }
 }