package project.dheeraj.hackcovid_19.Fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import project.dheeraj.hackcovid_19.MainActivity;
import project.dheeraj.hackcovid_19.Model.MapModel;
import project.dheeraj.hackcovid_19.Model.StateData;
import project.dheeraj.hackcovid_19.Model.StateViewModel;
import project.dheeraj.hackcovid_19.R;
import project.dheeraj.hackcovid_19.Util.UtilMethod;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.StrictMath.sin;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private ArrayList<MapModel> stateDatalist = new ArrayList<>();


    private final float DEFAULT_ZOOM = 18f;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private View mapView;
    private GoogleApiClient googleApiClient;
    private View view;
    private LatLng coordinates, cord;
    private FirebaseFirestore db;
    private List<Address> addressList = null;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.fragment_container_view_map_fragment);
        mapFragment.getMapAsync(this);

        mapView = mapFragment.getView();
        db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        progressbar.setVisibility(View.GONE);
        mMap = googleMap;

        if (UtilMethod.checkLocationPermission(getContext())) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 40, 40);

        db.collection("reports")
                .whereEqualTo("verified", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean present = false;
                        int count = 0;
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                double lat2  = doc.getDouble("patient_latitude");
                                double lon2  = doc.getDouble("patient_longitude");
                                cord = new LatLng(lat2, lon2);
                                googleMap.addMarker(new MarkerOptions().position(cord)
                                        .title(doc.getString("patient_name")));
                            }
                        }
                        else {
                            Toast.makeText(getContext(), "Can't load Map", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        Geocoder geocoder = new Geocoder(getContext());
        stateDatalist.add(new MapModel("Maharashra", Double.valueOf(200000)));
        stateDatalist.add(new MapModel("Kerala", Double.valueOf(160000)));
        stateDatalist.add(new MapModel("Delhi", Double.valueOf(150000)));
        stateDatalist.add(new MapModel("Uttar Pradesh", Double.valueOf(100000)));
        stateDatalist.add(new MapModel("Andhra Pradesh", Double.valueOf(80000)));


        for (int i=0; i<stateDatalist.size(); i++) {
            try {
                addressList = geocoder.getFromLocationName(stateDatalist.get(i).getState(), 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

            googleMap.addCircle(new CircleOptions().center(latLng).radius(stateDatalist.get(i).getRadius()).fillColor(0x55D32F2F).strokeColor(0x55D32F2F));
        }

        try {
            addressList = geocoder.getFromLocationName(stateDatalist.get(2).getState(), 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addressList.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5.0f));


    }

}
