package project.dheeraj.hackcovid_19.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.dheeraj.hackcovid_19.Model.StateData;
import project.dheeraj.hackcovid_19.R;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class StateAdapter extends RecyclerView.Adapter<StateAdapter.ViewHolder> {
    private List<StateData> stateDataList;
    private double[] predictedVal = new double[300];
    private Context mContext;
    private static DecimalFormat df2 = new DecimalFormat("#.##");

    public StateAdapter(List<StateData> stateDataList, Context mContext) {
        this.stateDataList = stateDataList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public StateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.state_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StateAdapter.ViewHolder holder, int position) {

        holder.statename.setText(stateDataList.get(position).getStatename());
        holder.cases.setText(stateDataList.get(position).getCases());
        holder.cured.setText(stateDataList.get(position).getCured());
        holder.deaths.setText(stateDataList.get(position).getDeath());
        holder.subheading1.setText(stateDataList.get(position).getSubheading1());
        holder.subheading3.setText(stateDataList.get(position).getSubheading2());
        holder.subheading2.setText(stateDataList.get(position).getSubheading3());
        if (predictedVal[position] != 0.0) {
            holder.case_predict.setText(df2.format(predictedVal[position]) + " %");
        } else {
            holder.case_predict.setText(R.string.prediction);
        }

    }

    @Override
    public int getItemCount() {
        return stateDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.heading)
        TextView statename;
        @BindView(R.id.totalcase)
        TextView cases;
        @BindView(R.id.subheading1)
        TextView subheading1;
        @BindView(R.id.totalcured)
        TextView cured;
        @BindView(R.id.subheading2)
        TextView subheading2;
        @BindView(R.id.newcase)
        TextView deaths;
        @BindView(R.id.subheading3)
        TextView subheading3;
        @BindView(R.id.predicted)
        TextView case_predict;
        @BindView(R.id.progress_bar)
        ProgressBar progressBar;
        @BindView(R.id.newCard)
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
