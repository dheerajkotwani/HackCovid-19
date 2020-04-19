package project.dheeraj.hackcovid_19.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

import project.dheeraj.hackcovid_19.Model.CountryModel;
import project.dheeraj.hackcovid_19.Adapter.ListCountriesAdapter;
import project.dheeraj.hackcovid_19.R;

public class FragmentCountryView extends Fragment {

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
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_country_view, container, false);


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

        // fix interference between scrolling in listView & parent SwipeRefreshLayout
        listViewCountries.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        if (!listIsAtTop()) mySwipeRefreshLayout.setEnabled(false);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mySwipeRefreshLayout.setEnabled(true);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
            private boolean listIsAtTop()   {
                if(listViewCountries.getChildCount() == 0) return true;
                return listViewCountries.getChildAt(0).getTop() == 0;
            }
        });


        // fetch previously saved data in SharedPreferences, if any
        if(preferences.getString("textViewCases", null) != null ){
            textViewCases.setText(preferences.getString("textViewCases", null));
            textViewRecovered.setText(preferences.getString("textViewRecovered", null));
            textViewDeaths.setText(preferences.getString("textViewDeaths", null));
            textViewDate.setText(preferences.getString("textViewDate", null));
            textViewActive.setText(preferences.getString("textViewActive", null));
            //calculate_percentages();
        }

        // Add Text Change Listener to textSearchBox to filter by Country
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


        return view;
    }

    void setListViewCountries(ArrayList<CountryModel> allCountriesResults) {
        Collections.sort(allCountriesResults);
        listCountriesAdapter = new ListCountriesAdapter(getActivity(), allCountriesResults);
        listViewCountries.setAdapter(listCountriesAdapter);
    }

    void calculate_percentages () {
        tmpNumber = Double.parseDouble(textViewRecovered.getText().toString().replaceAll(",", ""))
                / Double.parseDouble(textViewCases.getText().toString().replaceAll(",", ""))
                * 100;
        textViewRecoveredTitle.setText("Recovered   " + generalDecimalFormat.format(tmpNumber) + "%");

        tmpNumber = Double.parseDouble(textViewDeaths.getText().toString().replaceAll(",", ""))
                / Double.parseDouble(textViewCases.getText().toString().replaceAll(",", ""))
                * 100 ;
        textViewDeathsTitle.setText("Deaths   " + generalDecimalFormat.format(tmpNumber) + "%");

        tmpNumber = Double.parseDouble(textViewActive.getText().toString().replaceAll(",", ""))
                / Double.parseDouble(textViewCases.getText().toString().replaceAll(",", ""))
                * 100 ;
        textViewActiveTitle.setText("Active   " + generalDecimalFormat.format(tmpNumber) + "%");
    }

    void refreshData() {
        mySwipeRefreshLayout.setRefreshing(true);
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    doc = null;
                    doc = Jsoup.connect(url).timeout(10000).get();
                    countriesTable = doc.select("table").get(0);
                    countriesRows = countriesTable.select("tr");
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            rowIterator = countriesRows.iterator();
                            allCountriesResults = new ArrayList<CountryModel>();

                            row = rowIterator.next();
                            cols = row.select("th");
                            if (cols.get(0).text().contains("Country")) {
                                for(int i=1; i < cols.size(); i++){
                                    if (cols.get(i).text().contains("Total") && cols.get(i).text().contains("Cases"))
                                    {colNumCases = i; Log.e("Cases: ", cols.get(i).text());}
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

//                                    tmpPercentage = (generalDecimalFormat.format(Double.parseDouble(tmpRecovered.split("\n")[0].replaceAll(",", ""))
//                                            / Double.parseDouble(tmpCases.replaceAll(",", ""))
//                                            * 100)) + "%";
//                                    tmpRecovered = tmpRecovered + "\n" + tmpPercentage;
                                }
                                else {tmpRecovered = "0";}

                                if(cols.get(colNumDeaths).hasText()) {
                                    tmpDeaths = cols.get(colNumDeaths).text();

//                                    tmpPercentage = (generalDecimalFormat.format(Double.parseDouble(tmpDeaths.split("\n")[0].replaceAll(",", ""))
//                                            / Double.parseDouble(tmpCases.replaceAll(",", ""))
//                                            * 100)) + "%";
//                                    tmpDeaths = tmpDeaths + "\n" + tmpPercentage;
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

                            // save results
//                            editor.putString("textViewCases", textViewCases.getText().toString());
//                            editor.putString("textViewRecovered", textViewRecovered.getText().toString());
//                            editor.putString("textViewActive", textViewActive.getText().toString());
//                            editor.putString("textViewDeaths", textViewDeaths.getText().toString());
//                            editor.putString("textViewDate", textViewDate.getText().toString());
//                            editor.apply();

                            calculate_percentages();
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
}
