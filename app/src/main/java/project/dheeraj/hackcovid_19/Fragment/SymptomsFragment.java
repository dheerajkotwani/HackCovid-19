package project.dheeraj.hackcovid_19.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import project.dheeraj.hackcovid_19.Adapter.SymptomsAdapter;
import project.dheeraj.hackcovid_19.Model.SymptomsModel;
import project.dheeraj.hackcovid_19.R;

public class SymptomsFragment extends Fragment {

    private View view;
    private List<SymptomsModel> symptomsModels, precautionsModel;
    private RecyclerView symptomsRecyclerView, precautionsRecyclerView;
    private SymptomsAdapter symptomsAdapter, precautionsAdapet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_symptoms, container, false);

        symptomsRecyclerView = view.findViewById(R.id.symptomsRecyclerView);
        precautionsRecyclerView = view.findViewById(R.id.preventionRecyclerView);

        symptomsModels = new ArrayList<>();
        symptomsModels.add(new SymptomsModel(R.drawable.cold, "Cold"));
        symptomsModels.add(new SymptomsModel(R.drawable.cough, "Cough"));
        symptomsModels.add(new SymptomsModel(R.drawable.high_fever, "High Fever"));
        symptomsModels.add(new SymptomsModel(R.drawable.headache, "Headache"));
        symptomsModels.add(new SymptomsModel(R.drawable.vomiting, "Vomiting"));
        symptomsModels.add(new SymptomsModel(R.drawable.dyspnoea, "Dyspnoea"));

        precautionsModel = new ArrayList<>();
        precautionsModel.add(new SymptomsModel(R.drawable.use_mask, "Use Mask"));
        precautionsModel.add(new SymptomsModel(R.drawable.wash_hand, "Wash Hand Properly"));
        precautionsModel.add(new SymptomsModel(R.drawable.avoid_animal, "Avoid Animals"));
        precautionsModel.add(new SymptomsModel(R.drawable.avoid_meat, "Avoid Meat"));

        symptomsAdapter = new SymptomsAdapter(symptomsModels, getContext());
        precautionsAdapet = new SymptomsAdapter(precautionsModel, getContext());

        symptomsRecyclerView.setAdapter(symptomsAdapter);
        precautionsRecyclerView.setAdapter(precautionsAdapet);

        return view;
    }
}
