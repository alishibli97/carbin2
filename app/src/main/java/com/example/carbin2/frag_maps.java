package com.example.carbin2;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;


public class frag_maps extends Fragment implements SensorEventListener {

    private static final int ERROR_DIALOG_REQUEST = 9001;

    static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private FloatingActionButton fabStart;
    private static boolean started = false;
    private static Timer prevTimer;
    private static int userMilles = -1;
    private Timestamp currentTime = new Timestamp(0);

    private static double currentX = 0;
    private static double currentY = 0;
    private static double currentZ = 0;
    private static double currentS = 0;

    private static double[] accelerometerOriented = new double[3];
    private float[] rotationMatrix = new float[16];
    static final float[] orientationAngles = new float[3];

    private Date currentTimeD;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat preciseTimeFormat = new SimpleDateFormat("HH:mm:ss:SSS");

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private Location prevLocation;
    private Location currLocation;
    private int counter = 0;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer = null;
    private Sensor mRotationVector = null;


    public frag_maps() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        fabStart = view.findViewById(R.id.fab);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // getActivity() instead of this
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        final String userMillesS = sharedPref.getString("userMilles", "");
        if(!userMillesS.equals("")){
            userMilles = Integer.parseInt(userMillesS);
        }

        int i = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if(i == ConnectionResult.SUCCESS){
            final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    LatLng haddad = new LatLng(33.89695, 35.47943);
                    googleMap.addMarker(new MarkerOptions().position(haddad).title("Haddad"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(haddad,15.0f));

                    fabStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // startRecordingData();
                            toast("Here just re-adjust the functions you did to collect the data in csv file.\n" +
                                    "They are in the function startRecordingData()");

                            /*/
                                Note: ONCE THE FUNCTION IS WORKING WELL, YOU CAN POINT THE POINTER OF GOOGLE MAPS TO THE CURRENT LOCATION
                                      BY SPECIFING THE LATITUDE AND LONGITUDE FROM THE LOCATION-MANAGER.
                                      NOW IT IS INITIALLY POINTING TO BEIRUT
                             */

                        }
                    });
                }
            });
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(i)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(),i,ERROR_DIALOG_REQUEST);
            toast(dialog.toString());
        } else {
            toast("You can't make requests to google maps");
        }

        return view;
    }

    private void startRecordingData() {
        if(started) {
            started = false;
            prevTimer.cancel();
            ((FloatingActionButton)getView()).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.start)));
            ((FloatingActionButton)getView()).setImageDrawable(getResources().getDrawable(R.drawable.ic_map));
        }
        else {

            // here specify the frequency required passed from the settings

            //
            int frequency = ((MainActivity) Objects.requireNonNull(getActivity())).frequency;
            // and use this frequency . That is chosen by the user in the settings.
            // initial value is 50 Hz
            mSensorManager.registerListener(this, mAccelerometer, 100, 100);
            mSensorManager.registerListener(this, mRotationVector, 100, 100);


            started = true;
            final Timer t = new Timer();
            int period = userMilles == -1 ? 1000 : userMilles;
            t.scheduleAtFixedRate(new TimerTask() {

                AtomicInteger counter = new AtomicInteger(101);

                @Override
                public void run() {

                    if(currentTimeD != null && format.format(Calendar.getInstance().getTime()).equals(format.format(currentTimeD))) {
                        counter.incrementAndGet();
                    } else {
                        counter.set(0);
                        currentTimeD = Calendar.getInstance().getTime();
                    }

                    // data = counter.get()
                    // speed value = currentS
                    // acceleration value = new com.example.carbin2.AccThreeAxis(currentX,currentY,currentZ,preciseTimeFormat.format(Calendar.getInstance().getTime()))
                    // orientation value = new com.example.carbin2.AccThreeAxis(orientationAngles[1], orientationAngles[2], orientationAngles[0], preciseTimeFormat.format(Calendar.getInstance().getTime()))

                }

            }, 0, period);
            prevTimer = t;
            ((FloatingActionButton)getView()).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.stop)));
            ((FloatingActionButton)getView()).setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            currentTime.setTime(System.currentTimeMillis());

            currentX = sensorEvent.values[0];
            currentY = sensorEvent.values[1];
            currentZ = sensorEvent.values[2];

            getAcceleration(alpha);
            toast("accelerometerOriented x:"+String.valueOf(accelerometerOriented[0])+"y"+String.valueOf(accelerometerOriented[1])+
                    "accelerometerOriented z:"+String.valueOf(accelerometerOriented[2]));
        }

        else if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            rotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);

            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            double azimuth = Math.toDegrees(orientationAngles[0]);
            double pitch = Math.toDegrees(orientationAngles[1]) * -1;
            double roll = Math.toDegrees(orientationAngles[2]);

            toast("\nangles:\nroll: " + String.format("%.0f",roll) + " pitch: " + String.format("%.0f",pitch) + " yaw: " +
                    String.format("%.0f",azimuth));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public void requestLocationUpdates() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Log.d("myLocation", "onLocationResult: got a location");

                for (Location location : locationResult.getLocations()) {
                    currLocation = location;
                    if(currLocation.getSpeed() < 0)
                        currentS = 0;
                    else
                        currentS = currLocation.getSpeed();

                    if(counter > 0)
                        alpha = getAlpha(prevLocation, currLocation);
                    counter++;
                    prevLocation = currLocation;

                    // currentS

                }
            }
        };

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                prevLocation = location;
                                currLocation = location;
                            }
                        }
                    });
        }
        else {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    double alpha = 0.0;
    double dPitch = 0.0;
    double currAngleX = 0.0;
    double currAngleZ = 0.0;
    double currAngleY = 0.0;

    public double getAlpha(Location prevLocation, Location currLocation) {
        double timeInterval = currLocation.getTime() - prevLocation.getTime(); // almost 1 sec
        double D = 0.5 * (currLocation.getSpeed() + prevLocation.getSpeed()) * timeInterval;
        double dE = currLocation.getAltitude() - prevLocation.getAltitude();
        double angle = D / dE;

        if(! Double.isNaN (Math.asin(angle))) { // Check for NaN ? 0.0 !
            alpha = Math.asin(angle);
            Log.d("alpha", "getAlpha: " + alpha);
        }
        else {
            Log.d("alpha", "getAlpha: NaN");
        }

        // alpha

        return alpha;
    }

    public void getAcceleration(double alpha) {
        double aX, aY, aZ;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            dPitch = currAngleX + alpha;
            aZ = ((-currentY * Math.sin(dPitch) + currentX * Math.cos(dPitch) * Math.sin(currAngleY)
                    - currentZ * Math.cos(currAngleY) * Math.cos(dPitch)));

            aX = currentY * Math.cos(currAngleZ) * Math.cos(currAngleY)
                    + currentX * (Math.cos(currAngleZ) * Math.sin(dPitch) * Math.sin(currAngleY)
                    - Math.cos(dPitch) * Math.sin(currAngleZ))
                    - currentZ * (Math.sin(dPitch) * Math.sin(currAngleZ)
                    + Math.cos(dPitch) * Math.cos(currAngleZ) * Math.sin(currAngleY));

            aY = currentY * Math.cos(currAngleY) * Math.sin(currAngleZ)
                    + currentX * (Math.cos(dPitch) * Math.cos(currAngleZ)
                    + Math.sin(dPitch) * Math.sin(currAngleZ) * Math.sin(currAngleY))
                    - currentZ * (Math.cos(dPitch) * Math.sin(currAngleZ) * Math.sin(currAngleY)
                    - Math.cos(currAngleZ) * Math.sin(dPitch));
        } else {
            dPitch = currAngleY  + alpha;
            aZ = ((-currentY * Math.sin(dPitch) + currentX * Math.cos(dPitch) * Math.sin(currAngleY)
                    - currentZ * Math.cos(currAngleY) * Math.cos(dPitch)));

            aX = currentY * Math.cos(currAngleZ) * Math.cos(currAngleY)
                    + currentX * (Math.cos(currAngleZ) * Math.sin(dPitch) * Math.sin(currAngleY)
                    - Math.cos(dPitch) * Math.sin(currAngleZ))
                    - currentZ * (Math.sin(dPitch) * Math.sin(currAngleZ)
                    + Math.cos(dPitch) * Math.cos(currAngleZ) * Math.sin(currAngleY));

            aY = currentY * Math.cos(currAngleY) * Math.sin(currAngleZ)
                    + currentX * (Math.cos(dPitch) * Math.cos(currAngleZ)
                    + Math.sin(dPitch) * Math.sin(currAngleZ) * Math.sin(currAngleY))
                    - currentZ * (Math.cos(dPitch) * Math.sin(currAngleZ) * Math.sin(currAngleY)
                    - Math.cos(currAngleZ) * Math.sin(dPitch));
        }
        accelerometerOriented = new double[]{aX , aY , aZ};
    }

    private void toast(String s) {
        Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();
    }

}