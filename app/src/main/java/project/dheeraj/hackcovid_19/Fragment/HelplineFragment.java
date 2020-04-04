package project.dheeraj.hackcovid_19.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.dheeraj.hackcovid_19.Adapter.HelplineAdapter;
import project.dheeraj.hackcovid_19.Model.HelplineViewModel;
import project.dheeraj.hackcovid_19.Model.StateData;
import project.dheeraj.hackcovid_19.R;

public class HelplineFragment extends Fragment {
    private ArrayList<StateData> headerDatalist = new ArrayList<>();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HelplineViewModel notificationsViewModel = ViewModelProviders.of(this).get(HelplineViewModel.class);
        View root = inflater.inflate(R.layout.fragment_helpline, container, false);
        ButterKnife.bind(this, root);
        progressBar.setVisibility(View.VISIBLE);
        HelplineAdapter helplineAdapter = new HelplineAdapter(headerDatalist, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(helplineAdapter);

        notificationsViewModel.getData().observe(Objects.requireNonNull(getActivity()), data1 -> {
            if (data1 != null) {
                headerDatalist.clear();
                headerDatalist.addAll(data1);
                helplineAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });

        return root;
    }
}
