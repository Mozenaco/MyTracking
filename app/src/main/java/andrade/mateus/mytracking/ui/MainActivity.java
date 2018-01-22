package andrade.mateus.mytracking.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

import andrade.mateus.mytracking.R;
import andrade.mateus.mytracking.db.dao.CurrentLocationDAO;
import andrade.mateus.mytracking.db.dao.JourneyDAO;
import andrade.mateus.mytracking.db.parse.DateConverter;
import andrade.mateus.mytracking.service.BackgroundLocationService;
import andrade.mateus.mytracking.service.DrawOnMap;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import andrade.mateus.mytracking.service.BackgroundLocationService.LocalBinder;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Marker mCurrLocationMarker;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String TAG = "MainActivity";
    private final LatLng mDefaultLocation = new LatLng(0, 0);
    private Disposable disposable;
    private BackgroundLocationService mService;
    private CurrentLocationDAO currentLocationDAO;
    private JourneyDAO journeyDAO;
    private Boolean isRecordable = false;
    private DrawOnMap drawOnMap;
    private static Long currentJourneyId;
    private static Long currentJourneyStartTime;

    @BindView(R.id.switchMap) Switch switchMap;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            LocalBinder myBinder = (LocalBinder) iBinder;
            mService = myBinder.getService();
            disposable = mService.observePressure()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(location -> updateLocation(location));
            showCurrentLocationOnMap();
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.e(TAG, "onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializer();
    }

    private void initializer() {

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        currentLocationDAO = new CurrentLocationDAO(getApplicationContext());
        journeyDAO = new JourneyDAO(getApplicationContext());
    }

    @OnCheckedChanged(R.id.switchMap)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isRecordable = isChecked;
        if(isRecordable) {
            Toast.makeText(getApplicationContext(), R.string.recording, Toast.LENGTH_SHORT).show();
            currentJourneyId = DateConverter.toTimestamp(Calendar.getInstance().getTime());
            currentJourneyStartTime = currentJourneyId;
        }else
        {
            journeyDAO.saveJourney(currentJourneyId, "",
                    DateConverter.toDate(currentJourneyStartTime), Calendar.getInstance().getTime());
            Log.i(TAG, "Journey saved\n" +
                    "Start: " + currentJourneyStartTime + "\n" +
                    "End: " + Calendar.getInstance().getTime() + "\n" +
                    "Name: " + "" +
                    "Journey ID: " + String.valueOf(currentJourneyId));

            currentJourneyId = 0l;
            currentJourneyStartTime = 0l;
            Toast.makeText(getApplicationContext(), R.string.journey_saved, Toast.LENGTH_SHORT).show();
            mMap.clear();
            mCurrLocationMarker = null;
            showCurrentLocationOnMap();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMap!=null&&mLastKnownLocation!=null) {
            moveMapToCurrentPosition();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Saves the state of the map when the activity is paused.
     */

    @Override
    protected void onDestroy() {
        if(disposable != null)
            disposable.dispose();
        unbindService(mConnection);
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        getLocationPermission();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        if (mLocationPermissionGranted) {
            startService();
        }

        drawOnMap = new DrawOnMap(mMap);
    }

    private void startService(){
        // Create Intent Service
        Intent launch = new Intent(this, BackgroundLocationService.class);
        startService(launch);

        // Binding to it
        bindService(launch, mConnection, BIND_AUTO_CREATE);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            // Prompt the user for permission.
            getLocationPermission();
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void updateLocation(Location currentLocation){
        mLastKnownLocation = currentLocation;
        showCurrentLocationOnMap();
        if(isRecordable) {
            currentLocationDAO.saveLocation(currentLocation, Calendar.getInstance().getTime(), currentJourneyId);
            Log.i(TAG, "Location saved\n" +
                    "Latitude: " + String.valueOf(currentLocation.getLatitude()) + "\n" +
                    "Longitude: " + String.valueOf(currentLocation.getLatitude()) + "\n" +
                    "Timestamp: " + String.valueOf(Calendar.getInstance().getTime() +
                    "Journey ID: " + String.valueOf(currentJourneyId)));
        }
    }

    private void showCurrentLocationOnMap() {
        if (mLastKnownLocation == null)
            return;
        if (mCurrLocationMarker != null) {
            if (isRecordable) {
                drawOnMap.drawLine(mCurrLocationMarker.getPosition(),
                        new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
            }
            mCurrLocationMarker.setPosition(
                    new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
            return;
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("Current Position");
        markerOptions.position(
                new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        moveMapToCurrentPosition();
    }

    public void moveMapToCurrentPosition(){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    startService();
                }
            }
        }
        updateLocationUI();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended");
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.d(TAG, "onPointerCaptureChanged");
    }
}