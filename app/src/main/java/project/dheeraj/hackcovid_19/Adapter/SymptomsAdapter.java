package project.dheeraj.hackcovid_19.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.dheeraj.hackcovid_19.Model.SymptomsModel;
import project.dheeraj.hackcovid_19.R;

public class SymptomsAdapter extends RecyclerView.Adapter<SymptomsAdapter.ViewHolder> {


    private List<SymptomsModel> symptomsModelList;
    private Context context;

    public SymptomsAdapter(List<SymptomsModel> symptomsModelList, Context context) {
        this.symptomsModelList = symptomsModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_symptoms, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image.setImageResource(symptomsModelList.get(position).getImage());
        holder.text.setText(symptomsModelList.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return symptomsModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.itemImage)
        ImageView image;
        @BindView(R.id.itemText)
        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
