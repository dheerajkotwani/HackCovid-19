package project.dheeraj.hackcovid_19.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import project.dheeraj.hackcovid_19.R;

public class IndiaFragment extends Fragment {

    private View view;
    private TextView textCases, textNewCases, textDeath, textNewDeath, textRecovered;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_india, container, false);

        textCases = view.findViewById(R.id.textCases);
        textNewCases = view.findViewById(R.id.textNewCases);
        textRecovered = view.findViewById(R.id.textRecovered);
        textDeath = view.findViewById(R.id.textDeath);
        textNewDeath = view.findViewById(R.id.textNewDeath);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        setData();
        return view;
    }

    private void setData(){
        if(preferences.getString("india_cases", null) != null ){
            textCases.setText(preferences.getString("india_cases", null));
            textRecovered.setText(preferences.getString("india_recovered", null));
            textDeath.setText(preferences.getString("india_death", null));
            textNewCases.setText(preferences.getString("india_newCases", null));
            textNewDeath.setText(preferences.getString("india_newDeath", null));
            //calculate_percentages();
        }
    }
}
