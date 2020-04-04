package project.dheeraj.hackcovid_19.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import project.dheeraj.hackcovid_19.R;
import project.dheeraj.hackcovid_19.Util.UtilMethod;

public class ReportPatientActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private final float DEFAULT_ZOOM = 18f;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private View mapView;
    private LatLng coordinates;
    private View view;
    private LatLng getCoordinates;
    private FirebaseFirestore db;
    private TextInputEditText etPatientName, etPatientMobile, etPatientAddress, etPersonName, etPersonMobile;
    private Button buttonSubmit;
    private LocationProvider mLocationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_patient);

        etPatientName = findViewById(R.id.etPatientName);
        etPatientMobile = findViewById(R.id.etPatientMobile);
        etPatientAddress = findViewById(R.id.etPatientAddress);
        etPersonName = findViewById(R.id.etName);
        etPersonMobile = findViewById(R.id.etMobile);
        buttonSubmit = findViewById(R.id.buttonSumbit);
        buttonSubmit.setOnClickListener(this);

        mFusedLocationProviderClient = new FusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        db = FirebaseFirestore.getInstance();

        mapView = mapFragment.getView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        progressbar.setVisibility(View.GONE);
        mMap = googleMap;
        if (UtilMethod.checkLocationPermission(getApplicationContext())) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 40, 40);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

        getDeviceLocation();

        mMap.setBuildingsEnabled(true);
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                double lat = mMap.getCameraPosition().target.latitude;
                double lng = mMap.getCameraPosition().target.longitude;
                coordinates = new LatLng(lat, lng);
                Log.d("UPDATE_LOCATION", String.valueOf(coordinates.latitude));
            }
        });
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (true) {
                mFusedLocationProviderClient.getLastLocation()
                        .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    // Set the map's camera position to the current location of the device.
                                    mLastKnownLocation = (Location) task.getResult();
                                    coordinates = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                    Log.d("UPDATE_LOCATION", String.valueOf(coordinates.latitude));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15.0f));
                                    Toast.makeText(ReportPatientActivity.this, "success", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ReportPatientActivity.this, "failed "+task.getException(), Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", "Current location is null. Using defaults.");
                                    Log.e("TAG", "Exception: %s", task.getException());
                                }

                            }
                        });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }



    private void putData(LatLng coordinates){
        if (!checkDetails()){
            return;
        }
        buttonSubmit.setClickable(false);
        Map<String, Object> details = new HashMap<>();
        details.put("patient_name", etPatientName.getText().toString());
        if (!etPatientMobile.getText().toString().isEmpty())
            details.put("patient_mobile", etPatientMobile.getText().toString());
        details.put("patient_address", etPatientAddress.getText().toString());
        details.put("person_name", etPersonName.getText().toString());
        details.put("person_mobile", etPersonMobile.getText().toString());
        details.put("patient_latitude", coordinates.latitude);
        details.put("patient_longitude", coordinates.longitude);
        details.put("verified", false);

        db.collection("reports")
                .add(details)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        buttonSubmit.setClickable(true);
                        if (task.isSuccessful()){
                            Toast.makeText(ReportPatientActivity.this, "Data submitted for verification", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toast.makeText(ReportPatientActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean checkDetails(){
        if (etPatientName.getText().toString().isEmpty()){
            etPatientName.setError("Enter Patient Name");
            return false;
        }
        if (etPatientAddress.getText().toString().isEmpty()){
            etPatientAddress.setError("Enter Patient Address");
            return false;
        }
        if (etPersonName.getText().toString().isEmpty()){
            etPersonName.setError("Enter your Name");
            return false;
        }
        if (etPersonMobile.getText().toString().isEmpty()){
            etPersonMobile.setError("Enter your Mobile");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonSumbit:
                putData(coordinates);
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        /**
         * Request all parents to relinquish the touch events
         */
        mapView.getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}
