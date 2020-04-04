package project.dheeraj.hackcovid_19.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import project.dheeraj.hackcovid_19.Adapter.NearbyAdapter;
import project.dheeraj.hackcovid_19.Model.NearbyModel;
import project.dheeraj.hackcovid_19.R;

public class NearbyFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<NearbyModel> nearbyModelList, filterList;
    private View view;
    private NearbyAdapter nearbyAdapter;
    private EditText searchText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_nearby, container, false);
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.nearbyRecyclerView);
        searchText = view.findViewById(R.id.textSearchBox);
        nearbyModelList = new ArrayList<>();
        filterList = new ArrayList<>();
        getData();
        nearbyAdapter = new NearbyAdapter(filterList, getContext());
        nearbyAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(nearbyAdapter);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterList.clear();
                for (int j=0; j<nearbyModelList.size(); j++){
                    if (nearbyModelList.get(j).getCity().contains(editable)){
                        filterList.add(nearbyModelList.get(j));
                        nearbyAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        return view;

    }

    private void getData(){

        db.collection("nearby")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                nearbyModelList.add(new NearbyModel(
                                        documentSnapshot.get("name").toString(),
                                        documentSnapshot.get("address").toString(),
                                        documentSnapshot.get("mobile").toString(),
                                        documentSnapshot.get("type").toString(),
                                        documentSnapshot.get("city").toString()
                                ));
                                filterList.add(new NearbyModel(
                                        documentSnapshot.get("name").toString(),
                                        documentSnapshot.get("address").toString(),
                                        documentSnapshot.get("mobile").toString(),
                                        documentSnapshot.get("type").toString(),
                                        documentSnapshot.get("city").toString()
                                ));
                                nearbyAdapter.notifyDataSetChanged();
                            }
                        }
                        else {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}
