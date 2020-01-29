package com.jku.appocado;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HabitOverview extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String HABIT_NAME = "habit name";
    public static final String USER_ID = "userID";
    public static final String HABIT_DESCRIPTION = "habit description";
    public static final String HABIT_ID = "habitID";

    private static final String TAG = "LocationDemo";
    private static final int MY_PERMISSIONS_REQUEST = 99;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private TextView mLocationText, mHabitText, mHabitDescription;
    private TextView mDeleteHabit;


    //back button logic
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_overview);

        Context context = this.getApplicationContext();
        checkGooglePS(context);
        checkPermissions();

        if (mGoogleApiClient == null) {
            Log.d(TAG, "mGoogleApiClient needs to be built");

            mGoogleApiClient = new GoogleApiClient.Builder(this).
                    addConnectionCallbacks(this).
                    addOnConnectionFailedListener(this).
                    addApi(LocationServices.API).build();
        }

        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeUI();


    }

    private void initializeUI() {
        mLocationText = findViewById(R.id.textView);
        mHabitText = findViewById(R.id.Habit);
        mDeleteHabit = findViewById(R.id.deleteHabit);
        final String userID = getIntent().getStringExtra(USER_ID);
        final String habitID = getIntent().getStringExtra(HABIT_ID);
        String sessionId = getIntent().getStringExtra(HABIT_NAME);
        mHabitText.setText(sessionId);

        mDeleteHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Ask user if he realy wants to delete the habit
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users/" + userID + "/habits");
                databaseReference.child(habitID).setValue(null);
                finish();
            }
        });
    }

    private void checkPermissions() {
        Log.d(TAG, "checkPermission()");

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST);
            }
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "mGoogleApiClient connect");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "mGoogleApiClient disconnect");
        mGoogleApiClient.disconnect();
    }

    private void checkGooglePS(Context context) {
        final GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        try { // Check installed version
            Log.d(TAG, getPackageManager().getPackageInfo("com.google.android.gms",
                    0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String testPS = "SUCCESS";
        if (availability.isGooglePlayServicesAvailable(context) !=
                ConnectionResult.SUCCESS) {
            testPS = "ERROR";
        }
        Log.d(TAG, "Google PS availability: " + testPS);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String cityName = addresses.get(0).getLocality();
            mLocationText.setText(cityName);

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Setup periodic location updates
        // Configure location request parameters

        Log.d(TAG, "set location updates");

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); //about each 10 sec
        locationRequest.setFastestInterval(5000); // 5 sec pause at min
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Request updates with configuration from above
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "permission problem");

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates
                (mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
