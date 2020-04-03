package project.dheeraj.hackcovid_19.Fragment;

import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

public class ReportFragment extends Fragment implements OnMapReadyCallback {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_report, container, false);


//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.e);
        // Initialise View
        etPatientName = view.findViewById(R.id.etPatientName);
        etPatientMobile = view.findViewById(R.id.etPatientMobile);
        etPatientAddress = view.findViewById(R.id.etPatientAddress);
        etPersonName = view.findViewById(R.id.etName);
        etPersonMobile = view.findViewById(R.id.etMobile);
        buttonSubmit = view.findViewById(R.id.buttonSumbit);


        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        db = FirebaseFirestore.getInstance();

        mapView = mapFragment.getView();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);


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

//        getDeviceLocation();

        mMap.setBuildingsEnabled(true);
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                double lat = mMap.getCameraPosition().target.latitude;
                double lng = mMap.getCameraPosition().target.longitude;
                coordinates = new LatLng(lat, lng);
                Log.d("UPDATE_LOCATION", String.valueOf(coordinates.latitude));
//                setEtAddress(coordinates);
            }
        });
    }

    private void getDeviceLocation() {

//        try {
//            if (true) {
//                mFusedLocationProviderClient.getLastLocation()
//                        .addOnCompleteListener((Executor) this, new OnCompleteListener<Location>() {
//                            @Override
//                            public void onComplete(@NonNull Task task) {
//                                if (task.isSuccessful()) {
//                                    // Set the map's camera position to the current location of the device.
//                                    mLastKnownLocation = (Location) task.getResult();
//                                    coordinates = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
//                                    Log.d("UPDATE_LOCATION", String.valueOf(coordinates.latitude));
//                                    putData(coordinates);
//                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15.0f));
////                                    Toast.makeText(SignUpActivity.this, "success", Toast.LENGTH_SHORT).show();
//                                } else {
////                                    Toast.makeText(SignUpActivity.this, "failed "+task.getException(), Toast.LENGTH_SHORT).show();
//                                    Log.d("TAG", "Current location is null. Using defaults.");
//                                    Log.e("TAG", "Exception: %s", task.getException());
////                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, DEFAULT_ZOOM));
////                                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                                }
//
//                            }
//                        });
//            }
//        } catch(SecurityException e)  {
//            Log.e("Exception: %s", e.getMessage());
//        }


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
        details.put("patient_longitude", coordinates.latitude);
        details.put("verified", false);

        db.collection("reports")
                .add(details)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        buttonSubmit.setClickable(true);
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Data submitted for verification", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
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



}
