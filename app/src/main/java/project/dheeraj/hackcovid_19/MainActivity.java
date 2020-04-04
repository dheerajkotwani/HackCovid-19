package project.dheeraj.hackcovid_19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;
import project.dheeraj.hackcovid_19.Activity.ReportPatientActivity;
import project.dheeraj.hackcovid_19.DuoAdapter;
import project.dheeraj.hackcovid_19.Fragment.AboutFragment;
import project.dheeraj.hackcovid_19.Fragment.DashboardFragment;
import project.dheeraj.hackcovid_19.Fragment.FragmentCountryView;
import project.dheeraj.hackcovid_19.Fragment.HelplineFragment;
import project.dheeraj.hackcovid_19.Fragment.IndiaFragment;
import project.dheeraj.hackcovid_19.Fragment.MapFragment;
import project.dheeraj.hackcovid_19.Fragment.NearbyFragment;
import project.dheeraj.hackcovid_19.Fragment.ReportFragment;
import project.dheeraj.hackcovid_19.Fragment.SymptomsFragment;
import project.dheeraj.hackcovid_19.Fragment.facilitiesfragment;
import project.dheeraj.hackcovid_19.Model.StateData;
import project.dheeraj.hackcovid_19.Model.StateViewModel;
import project.dheeraj.hackcovid_19.R;
import project.dheeraj.hackcovid_19.Util.UtilMethod;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.StrictMath.sin;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DuoMenuView.OnMenuClickListener {

    private ArrayList<StateData> stateDatalist = new ArrayList<>();

    private final float DEFAULT_ZOOM = 18f;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private View mapView;
    private LatLng coordinates;
    private FirebaseFirestore db;

    private ImageView navDrawerIcon;
    private Toolbar toolbar;
    private DuoMenuView duoMenuView;
    private DuoDrawerLayout drawerLayout;
    private DuoDrawerToggle drawerToggle;
    private Fragment fragmentDashboard, fragmentCountryView, fragmentMap, fragmentSymptoms;
    private Fragment fragmentIndia, fragmentHelpline, fragmentAbout, fragmentFacilities, fragmentNearby;
    private FragmentManager fragmentManager;
    private DuoAdapter duoMenuAdapter;
//    private View fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        duoMenuView = findViewById(R.id.duoMenuView);
        navDrawerIcon = findViewById(R.id.navDrawerIcon);
        drawerLayout = (DuoDrawerLayout) findViewById(R.id.duoDrawerLayout);
        drawerToggle = new DuoDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        fragmentDashboard = new DashboardFragment();
        fragmentCountryView = new FragmentCountryView();
        fragmentAbout = new AboutFragment();
        fragmentIndia = new IndiaFragment();
        fragmentSymptoms = new SymptomsFragment();
        fragmentHelpline = new HelplineFragment();
        fragmentFacilities = new facilitiesfragment();
        fragmentNearby = new NearbyFragment();
        fragmentMap = new MapFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.dashboardFragment, fragmentDashboard).commit();

        ArrayList<String> menuOptions = new ArrayList<>();
        menuOptions.add("Dashboard");
        menuOptions.add("Effected Countries");
        menuOptions.add("India");
        menuOptions.add("Map");
        menuOptions.add("Symptoms");
        menuOptions.add("Nearby");
        menuOptions.add("Facilities");
        menuOptions.add("Report");
        menuOptions.add("Helpline");
        menuOptions.add("About");

        duoMenuAdapter = new DuoAdapter(menuOptions);
        duoMenuView.setAdapter(duoMenuAdapter);
        duoMenuView.setOnMenuClickListener(this);
        duoMenuAdapter.setViewSelected(0, true);

        navDrawerIcon.setOnClickListener(this);
//        getLastLocation();
        if (UtilMethod.checkLocationPermission(MainActivity.this)) {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                    new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();

                            coordinates = new LatLng(location.getLatitude(), location.getLongitude());
                            checkData(coordinates);

                        }
                    }
            );
        }
//        getDeviceLocation();

    }



    private void checkData(LatLng coordinates){

        final double[] lat1 = {coordinates.latitude};
        double lon1 = coordinates.longitude;
        double M_PI = 3.14;

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
                                double dLat = (lat2 - lat1[0]) *
                                        M_PI / 180.0;
                                double dLon = (lon2 - lon1) *
                                        M_PI / 180.0;

                                // convert to radians
                                lat1[0] = (lat1[0]) * M_PI / 180.0;
                                lat2 = (lat2) * M_PI / 180.0;

                                // apply formulae
                                double a = pow(sin(dLat / 2), 2) +
                                        pow(sin(dLon / 2), 2) *
                                                cos(lat1[0]) * cos(lat2);
                                double rad = 6371;
                                double c = 2 * asin(sqrt(a));
                                double d = rad * c;
                                Log.d("Distance: ", String.valueOf(d));
                                if ((d)<=5){
                                    present = true;
                                    count++;
                                }
                            }
                            if (present){
                                Toast.makeText(MainActivity.this, count+" people found infected in your area", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(MainActivity.this, "You are in safe area", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this, "can't find patients in your area at this moment", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.navDrawerIcon:
                drawerLayout.openDrawer();
                break;
        }
    }

    @Override
    public void onFooterClicked() {

    }

    @Override
    public void onHeaderClicked() {

    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        switch (position){
            default:
            case 0:
                fragmentManager.beginTransaction().replace(R.id.dashboardFragment, fragmentDashboard).commit();
                duoMenuAdapter.setViewSelected(position, true);
                drawerLayout.closeDrawer();
                break;
            case 1:
                fragmentManager.beginTransaction().replace(R.id.dashboardFragment, fragmentCountryView).commit();
                duoMenuAdapter.setViewSelected(position, true);
                drawerLayout.closeDrawer();
                break;
            case 2:
                fragmentManager.beginTransaction().replace(R.id.dashboardFragment, fragmentIndia).commit();
                duoMenuAdapter.setViewSelected(position, true);
                drawerLayout.closeDrawer();
                break;
            case 3:
                fragmentManager.beginTransaction().replace(R.id.dashboardFragment, fragmentMap).commit();
                duoMenuAdapter.setViewSelected(position, true);
                drawerLayout.closeDrawer();
                break;
            case 4:
                fragmentManager.beginTransaction().replace(R.id.dashboardFragment, fragmentSymptoms).commit();
                duoMenuAdapter.setViewSelected(position, true);
                drawerLayout.closeDrawer();
                break;
            case 5:
                fragmentManager.beginTransaction().replace(R.id.dashboardFragment, fragmentNearby).commit();
                duoMenuAdapter.setViewSelected(position, true);
                drawerLayout.closeDrawer();
                break;
            case 6:
                fragmentManager.beginTransaction().replace(R.id.dashboardFragment, fragmentFacilities).commit();
                duoMenuAdapter.setViewSelected(position, true);
                drawerLayout.closeDrawer();
                break;
            case 7:
//               fragmentManager.beginTransaction().replace(R.id.dashboardFragment, fragmentReport).commit();
                startActivity(new Intent(MainActivity.this, ReportPatientActivity.class));
                duoMenuAdapter.setViewSelected(position, true);
                drawerLayout.closeDrawer();
                break;
            case 8:
                fragmentManager.beginTransaction().replace(R.id.dashboardFragment, fragmentHelpline).commit();
//                startActivity(new Intent(MainActivity.this, HelplineFragment.class));
                duoMenuAdapter.setViewSelected(position, true);
                drawerLayout.closeDrawer();
                break;
            case 9:
                fragmentManager.beginTransaction().replace(R.id.dashboardFragment, fragmentAbout).commit();
//            startActivity(new Intent(MainActivity.this, AboutFragment.class));
            duoMenuAdapter.setViewSelected(position, true);
            drawerLayout.closeDrawer();
            break;
        }

    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


}
