package project.dheeraj.hackcovid_19.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.dheeraj.hackcovid_19.Adapter.StateAdapter;
import project.dheeraj.hackcovid_19.Model.StateData;
import project.dheeraj.hackcovid_19.Model.StateViewModel;
import project.dheeraj.hackcovid_19.R;

public class IndiaFragment extends Fragment {

    private ArrayList<StateData> stateDatalist = new ArrayList<>();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.quote_layout)
    LinearLayout linearLayout;
    @BindView(R.id.announcementLayout)
    LinearLayout announcementLayout;
    @BindView(R.id.districtWise)
    TextView disytrictwise;

    private View root, view;
    private TextView textCases, textNewCases, textDeath, textNewDeath, textRecovered;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        StateViewModel stateViewModel = ViewModelProviders.of(this).get(StateViewModel.class);

        root = inflater.inflate(R.layout.fragment_india, container, false);
        textCases = root.findViewById(R.id.textCases);
        textNewCases = root.findViewById(R.id.textNewCases);
        textRecovered = root.findViewById(R.id.textRecovered);
        textDeath = root.findViewById(R.id.textDeath);
        textNewDeath = root.findViewById(R.id.textNewDeath);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        setData();


        ButterKnife.bind(this, root);
        progressBar.setVisibility(View.VISIBLE);
        StateAdapter stateAdapter = new StateAdapter(stateDatalist, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(stateAdapter);

        stateViewModel.getData().observe(Objects.requireNonNull(getActivity()), data1 -> {
            if (data1 != null) {
                stateDatalist.clear();
                stateDatalist.addAll(data1);
                stateAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
        return root;


    }

    private void setData(){
        if(preferences.getString("india_cases", null) != null ){
            textCases.setText(preferences.getString("india_cases", null));
            textRecovered.setText(preferences.getString("india_recovered", null));
            textDeath.setText(preferences.getString("india_death", null));
            textNewCases.setText(preferences.getString("india_newCases", null));
            textNewDeath.setText(preferences.getString("india_newDeath", null));
        }
    }
}
