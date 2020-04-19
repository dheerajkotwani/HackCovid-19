package project.dheeraj.hackcovid_19.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import project.dheeraj.hackcovid_19.Model.CountryModel;
import project.dheeraj.hackcovid_19.Adapter.ListCountriesAdapter;
import project.dheeraj.hackcovid_19.Model.StateData;
import project.dheeraj.hackcovid_19.Model.StateViewModel;
import project.dheeraj.hackcovid_19.R;
import project.dheeraj.hackcovid_19.Util.UtilMethod;

public class DashboardFragment extends Fragment implements View.OnClickListener{

    // start
    private ArrayList<StateData> stateDatalist = new ArrayList<>();
    private LatLng coordinates;

    TextView textViewCases, textViewRecovered, textViewDeaths, textViewDate, textViewDeathsTitle,
            textViewRecoveredTitle, textViewActive, textViewActiveTitle, textViewNewDeaths,
            textViewNewCases, textViewNewDeathsTitle, textViewNewCasesTitle ;
    EditText textSearchBox;
    Handler handler;
    String url = "https://www.worldometers.info/coronavirus/";
    String tmpCountry, tmpCases, tmpRecovered, tmpDeaths, tmpPercentage, germanResults, tmpNewCases, tmpNewDeaths;
    Document doc, germanDoc;
    org.jsoup.nodes.Element countriesTable, row, germanTable;
    Elements countriesRows, cols, germanRows;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Calendar myCalender;
    SimpleDateFormat myFormat;
    double tmpNumber;
    DecimalFormat generalDecimalFormat;
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    ListView listViewCountries;
    ListCountriesAdapter listCountriesAdapter;
    ArrayList<CountryModel> allCountriesResults, FilteredArrList;
    Intent sharingIntent;
    int colNumCountry, colNumCases, colNumRecovered, colNumDeaths, colNumActive, colNumNewCases, colNumNewDeaths;
    SwipeRefreshLayout mySwipeRefreshLayout;
    InputMethodManager inputMethodManager;
    Iterator<Element> rowIterator;
    ProgressBar countryProgressBar;
    FrameLayout dashProgress;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private View view;
    private String state;

    // end

    private TextView textTotalCases, textDeaths, textRecovered;
    private ProgressBar progressDeaths, progressRecovered;
    private ImageView buttonGlobal;
    private Fragment fragmentDashboard, fragmentMap, fragmentCountryView;
    private FragmentManager fragmentManager;
    private CardView cardDashBoardMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        initializeViews();
        loadData();

        return view;
    }

    private void initializeViews() {
        textTotalCases = view.findViewById(R.id.textTotalCases);
        textDeaths = view.findViewById(R.id.textDeathCases);
        textRecovered = view.findViewById(R.id.textRecoveredCases);
        progressDeaths = view.findViewById(R.id.progressDeathCases);
        progressRecovered = view.findViewById(R.id.progressRecoveredCases);
        buttonGlobal = view.findViewById(R.id.buttonGlobal);
        cardDashBoardMap = view.findViewById(R.id.cardDashboardMap);
        dashProgress = view.findViewById(R.id.dashProgress);
        buttonGlobal.setOnClickListener(this);
        cardDashBoardMap.setOnClickListener(this);


        fragmentDashboard = new DashboardFragment();
        fragmentCountryView = new FragmentCountryView();
        fragmentMap = new MapFragment();
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    private void loadData(){

        // All initial definitions
        textViewCases = (TextView)view.findViewById(R.id.textViewCases);
        textViewRecovered = (TextView)view.findViewById(R.id.textViewRecovered);
        textViewDeaths = (TextView)view.findViewById(R.id.textViewDeaths);
        textViewDate = (TextView)view.findViewById(R.id.textViewDate);
        textViewRecoveredTitle = (TextView)view.findViewById(R.id.textViewRecoveredTitle);
        textViewDeathsTitle = (TextView)view.findViewById(R.id.textViewDeathsTitle);
        textViewActiveTitle = (TextView)view.findViewById(R.id.textViewActiveTitle);
        textViewActive = (TextView)view.findViewById(R.id.textViewActive);
        textViewNewDeaths = (TextView)view.findViewById(R.id.textViewNewDeaths);
        textViewNewCases = (TextView)view.findViewById(R.id.textViewNewCases);
        textViewNewCasesTitle = (TextView)view.findViewById(R.id.textViewNewCasesTitle);
        textViewNewDeathsTitle = (TextView)view.findViewById(R.id.textViewNewDeathsTitle);
        listViewCountries = (ListView)view.findViewById(R.id.listViewCountries);
        textSearchBox = (EditText)view.findViewById(R.id.textSearchBox);
        countryProgressBar = (ProgressBar) view.findViewById(R.id.countryProgressBar);
        colNumCountry = 0; colNumCases = 1; colNumRecovered = 0; colNumDeaths = 0; colNumNewCases = 0; colNumNewDeaths = 0;
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();
        myFormat = new SimpleDateFormat("MMMM dd, yyyy, hh:mm:ss aaa", Locale.US);
        myCalender = Calendar.getInstance();
        handler = new Handler() ;
        generalDecimalFormat = new DecimalFormat("0.00", symbols);
        allCountriesResults = new ArrayList<CountryModel>();

        getByState();

        // Implement Swipe to Refresh
        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.coronaMainSwipeRefresh);
        mySwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshData();
                    }
                }
        );

        // fetch previously saved data in SharedPreferences, if any
        if(preferences.getString("textViewCases", null) != null ){
            textViewCases.setText(preferences.getString("textViewCases", null));
            textViewRecovered.setText(preferences.getString("textViewRecovered", null));
            textViewDeaths.setText(preferences.getString("textViewDeaths", null));
            textViewDate.setText(preferences.getString("textViewDate", null));
            textViewActive.setText(preferences.getString("textViewActive", null));

            textTotalCases.setText(preferences.getString("textViewCases", null));
            textDeaths.setText(preferences.getString("textViewDeaths", null));
            textRecovered.setText(preferences.getString("textViewRecovered", null));
        }

        textSearchBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence searchSequence, int start, int before, int count) {
                FilteredArrList = new ArrayList<CountryModel>();
                if (searchSequence == null || searchSequence.length() == 0) {
                    // back to original
                    setListViewCountries(allCountriesResults);
                } else {
                    searchSequence = searchSequence.toString().toLowerCase();
                    for (int i = 0; i < allCountriesResults.size(); i++) {
                        String data = allCountriesResults.get(i).countryName;
                        if (data.toLowerCase().startsWith(searchSequence.toString())) {
                            FilteredArrList.add(new CountryModel(
                                    allCountriesResults.get(i).countryName,
                                    allCountriesResults.get(i).cases,
                                    allCountriesResults.get(i).newCases,
                                    allCountriesResults.get(i).recovered,
                                    allCountriesResults.get(i).deaths,
                                    allCountriesResults.get(i).newDeaths));
                        }
                    }
                    // set the Filtered result to return
                    setListViewCountries(FilteredArrList);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });




        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String filtered = "";
                for (int i = start; i < end; i++) {
                    char character = source.charAt(i);
                    if (!Character.isWhitespace(character)) {
                        filtered += character;
                    }
                }

                return filtered;
            }

        };

        textSearchBox.setFilters(new InputFilter[] { filter });
        textSearchBox.clearFocus();
        // Call refreshData once the app is opened only one time, then user can request updates
        refreshData();


    }

    private void getIndia(ArrayList<CountryModel> allCountriesResults) {
        for (int i = 0; i < allCountriesResults.size(); i++) {
            String data = allCountriesResults.get(i).countryName;
            if (data.toLowerCase().equals("india")) {
                editor.putString("india_cases", allCountriesResults.get(i).cases);
                editor.putString("india_recovered",allCountriesResults.get(i).recovered);
                editor.putString("india_newCases",allCountriesResults.get(i).newCases);
                editor.putString("india_death",allCountriesResults.get(i).deaths);
                editor.putString("india_newDeath",allCountriesResults.get(i).newDeaths);
//                Toast.makeText(getContext(), "Data for India", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void setListViewCountries(ArrayList<CountryModel> allCountriesResults) {
        Collections.sort(allCountriesResults);
        listCountriesAdapter = new ListCountriesAdapter(getActivity(), allCountriesResults);
        listViewCountries.setAdapter(listCountriesAdapter);
        getIndia(allCountriesResults);
    }

    void calculate_percentages () {


        tmpNumber = Double.parseDouble(textRecovered.getText().toString().replaceAll(",", ""))
                / Double.parseDouble(textTotalCases.getText().toString().replaceAll(",", ""))
                * 100;

        progressRecovered.setProgress((int) tmpNumber);

//        textTotalCases.setText(textViewCases.getText().toString());
//        textRecovered.setText(textViewRecovered.getText().toString());
//        textDeaths.setText(textViewDeaths.getText().toString());
//        textViewRecoveredTitle.setText("Recovered   " + generalDecimalFormat.format(tmpNumber) + "%");

        tmpNumber = Double.parseDouble(textDeaths.getText().toString().replaceAll(",", ""))
                / Double.parseDouble(textTotalCases.getText().toString().replaceAll(",", ""))
                * 100 ;
//        textViewDeathsTitle.setText("Deaths   " + generalDecimalFormat.format(tmpNumber) + "%");
        progressDeaths.setProgress((int) tmpNumber);

//        tmpNumber = Double.parseDouble(textViewActive.getText().toString().replaceAll(",", ""))
//                / Double.parseDouble(textViewCases.getText().toString().replaceAll(",", ""))
//                * 100 ;
//        textViewActiveTitle.setText("Active   " + generalDecimalFormat.format(tmpNumber) + "%");
    }

    void refreshData() {
        mySwipeRefreshLayout.setRefreshing(true);
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {



                    doc = null; // Fetches the HTML document
                    doc = Jsoup.connect(url).timeout(10000).get();
                    // table id main_table_countries
                    countriesTable = doc.select("table").get(0);
                    countriesRows = countriesTable.select("tr");
                    //Log.e("TITLE", elementCases.text());
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            // get countries
                            dashProgress.setVisibility(View.GONE);
                            rowIterator = countriesRows.iterator();
                            allCountriesResults = new ArrayList<CountryModel>();

                            // read table header and find correct column number for each category
                            row = rowIterator.next();
                            cols = row.select("th");
                            //Log.e("COLS: ", cols.text());
                            if (cols.get(0).text().contains("Country")) {
                                for(int i=0; i < cols.size(); i++){
                                    if (cols.get(i).text().contains("Total") && cols.get(i).text().contains("Cases"))
                                    {
                                        colNumCases = i; Log.e("Cases: ", cols.get(i).text());
                                    }
                                    else if (cols.get(i).text().contains("Total") && cols.get(i).text().contains("Recovered"))
                                    {colNumRecovered = i; Log.e("Recovered: ", cols.get(i).text());}
                                    else if (cols.get(i).text().contains("Total") && cols.get(i).text().contains("Deaths"))
                                    {colNumDeaths = i; Log.e("Deaths: ", cols.get(i).text());}
                                    else if (cols.get(i).text().contains("Active") && cols.get(i).text().contains("Cases"))
                                    {colNumActive = i; Log.e("Active: ", cols.get(i).text());}
                                    else if (cols.get(i).text().contains("New") && cols.get(i).text().contains("Cases"))
                                    {colNumNewCases = i; Log.e("NewCases: ", cols.get(i).text());}
                                    else if (cols.get(i).text().contains("New") && cols.get(i).text().contains("Deaths"))
                                    {colNumNewDeaths = i; Log.e("NewDeaths: ", cols.get(i).text());}
                                }
                            }

                            while (rowIterator.hasNext()) {
                                row = rowIterator.next();
                                cols = row.select("td");

                                if (cols.get(0).text().contains("Total")) {
                                    textViewCases.setText(cols.get(colNumCases).text());
                                    textViewRecovered.setText(cols.get(colNumRecovered).text());
                                    textViewDeaths.setText(cols.get(colNumDeaths).text());

                                    if (cols.get(colNumActive).hasText()) {textViewActive.setText(cols.get(colNumActive).text());}
                                    else {textViewActive.setText("0");}
                                    if (cols.get(colNumNewCases).hasText()) {textViewNewCases.setText(cols.get(colNumNewCases).text());}
                                    else {textViewNewCases.setText("0");}
                                    if (cols.get(colNumNewDeaths).hasText()) {textViewNewDeaths.setText(cols.get(colNumNewDeaths).text());}
                                    else {textViewNewDeaths.setText("0");}
                                    break;
                                }

                                if (cols.get(colNumCountry).hasText()) {tmpCountry = cols.get(0).text();}
                                else {tmpCountry = "NA";}

                                if (cols.get(colNumCases).hasText()) {tmpCases = cols.get(colNumCases).text();}
                                else {tmpCases = "0";}

                                if (cols.get(colNumRecovered).hasText()){
                                    tmpRecovered = cols.get(colNumRecovered).text();
                                    if (!tmpRecovered.equals("N/A") && !tmpCases.equals("N/A"))
                                    tmpPercentage = (generalDecimalFormat.format(Double.parseDouble(tmpRecovered.replaceAll(",", ""))
                                            / Double.parseDouble(tmpCases.replaceAll(",", ""))
                                            * 100)) + "%";
                                    tmpRecovered = tmpRecovered + "\n" + tmpPercentage;
                                }
                                else {tmpRecovered = "0";}

                                if(cols.get(colNumDeaths).hasText()) {
                                    tmpDeaths = cols.get(colNumDeaths).text();
                                    tmpPercentage = (generalDecimalFormat.format(Double.parseDouble(tmpDeaths.replaceAll(",", ""))
                                            / Double.parseDouble(tmpCases.replaceAll(",", ""))
                                            * 100)) + "%";
                                    tmpDeaths = tmpDeaths + "\n" + tmpPercentage;
                                }
                                else {tmpDeaths = "0";}

                                if (cols.get(colNumNewCases).hasText()) {tmpNewCases = cols.get(colNumNewCases).text();}
                                else {tmpNewCases = "0";}

                                if (cols.get(colNumNewDeaths).hasText()) {tmpNewDeaths = cols.get(colNumNewDeaths).text();}
                                else {tmpNewDeaths = "0";}

                                allCountriesResults.add(new CountryModel(tmpCountry, tmpCases, tmpNewCases, tmpRecovered, tmpDeaths, tmpNewDeaths));
                            }

                            setListViewCountries(allCountriesResults);
                            textSearchBox.setText(null);
                            textSearchBox.clearFocus();





                            for (int i=0; i<allCountriesResults.size(); i++){
                                if (allCountriesResults.get(i).countryName.toLowerCase().trim().equals("world")){
                                    textTotalCases.setText(allCountriesResults.get(i).getCases());
                                    textRecovered.setText(allCountriesResults.get(i).getRecovered().split("\n")[0]);
                                    textDeaths.setText(allCountriesResults.get(i).getDeaths().split("\n")[0]);
//                                    Toast.makeText(getActivity(), allCountriesResults.get(i).getCases(), Toast.LENGTH_SHORT).show();

                                    textViewCases.setText(allCountriesResults.get(i).getCases());
                                    textViewRecovered.setText(allCountriesResults.get(i).getRecovered());
                                    textViewActive.setText(allCountriesResults.get(i).getCases());
                                    textViewDeaths.setText(allCountriesResults.get(i).getDeaths());

                                    // save results
                                    myCalender = Calendar.getInstance();
                                    editor.putString("textViewCases", allCountriesResults.get(i).getCases());
                                    editor.putString("textViewRecovered", allCountriesResults.get(i).getRecovered().split("\n")[0]);
                                    editor.putString("textViewActive", textViewActive.getText().toString());
                                    editor.putString("textViewDeaths", allCountriesResults.get(i).getDeaths().split("\n")[0]);
                                    editor.putString("textViewDate", "Last updated: " + myFormat.format(myCalender.getTime()));
                                    editor.apply();

                                    calculate_percentages();
                                    break;
                                }
                            }
                            myCalender = Calendar.getInstance();
                            textViewDate.setText("Last updated: " + myFormat.format(myCalender.getTime()));
                        }
                    });
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Network Connection Error!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
                finally {
                    doc = null;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mySwipeRefreshLayout.setRefreshing(false);
                    }});
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonGlobal:
                fragmentManager.beginTransaction().replace(R.id.dashboardFragment, fragmentCountryView).commit();
                break;
            case R.id.cardDashboardMap:
                fragmentManager.beginTransaction().replace(R.id.dashboardFragment, fragmentMap).commit();
                break;
        }
    }

    private void getByState(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        StateViewModel stateViewModel = ViewModelProviders.of(this).get(StateViewModel.class);
        stateViewModel.getData().observe(Objects.requireNonNull(getActivity()), data1 -> {
            if (data1 != null) {
                stateDatalist.clear();
                stateDatalist.addAll(data1);
            }
        });


        if (UtilMethod.checkLocationPermission(getContext())) {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                    new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();

                            coordinates = new LatLng(location.getLatitude(), location.getLongitude());
                            Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (addresses.size() > 0) {
                                state = addresses.get(0).getAdminArea();
                                Double count = 0.0;
                                Double safety = 1.0;
                                Double total = Double.valueOf(preferences.getString("india_cases", "1").replaceAll(",",""));
                                for (int i=0; i<stateDatalist.size(); i++){
                                    if (state.equals(stateDatalist.get(i).getStatename())){
                                        count = Double.valueOf(stateDatalist.get(i).getCases());

                                    }
                                }

                                if (count>0 && total>0){
                                    safety = (count/total)*100;
                                    Toast.makeText(getContext(), "Risk in your state : "+String.valueOf(safety).substring(0,5)+"%", Toast.LENGTH_LONG).show();
                                }

                            }
                            else {
                                // do your stuff
                            }

                        }
                    }
            );
        }


    }
}
