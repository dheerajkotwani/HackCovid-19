package project.dheeraj.hackcovid_19.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.dheeraj.hackcovid_19.Model.NearbyModel;
import project.dheeraj.hackcovid_19.R;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.ViewHolder> {

    private List<NearbyModel> nearbyModelList;
    private Context context;

    public NearbyAdapter(List<NearbyModel> nearbyModelList, Context context) {
        this.nearbyModelList = nearbyModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nearby, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.storeName.setText(nearbyModelList.get(position).getName());
        holder.storeAddress.setText(nearbyModelList.get(position).getAddress());
        holder.storeMobile.setText(nearbyModelList.get(position).getMobile());
        holder.storeType.setText(nearbyModelList.get(position).getType());
    }


    @Override
    public int getItemCount() {
        return nearbyModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.store_name)
        TextView storeName;
        @BindView(R.id.store_address)
        TextView storeAddress;
        @BindView(R.id.store_type)
        TextView storeType;
        @BindView(R.id.store_mobile)
        TextView storeMobile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
