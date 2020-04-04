package project.dheeraj.hackcovid_19.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import project.dheeraj.hackcovid_19.Model.CountryModel;
import project.dheeraj.hackcovid_19.R;

public class ListCountriesAdapter extends BaseAdapter {

    Activity context;
    ArrayList<CountryModel> allCountriesResults;
    LayoutInflater inflater;

    public ListCountriesAdapter(Activity context, ArrayList<CountryModel> allCountriesResults) {
        super();
        this.context = context;
        this.allCountriesResults = allCountriesResults;
        inflater =  context.getLayoutInflater();
    }


    @Override
    public int getCount() {
        return allCountriesResults.size();
    }

    @Override
    public Object getItem(int position) {
        return allCountriesResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView colCountryName;
        TextView colCases;
        TextView colNewCases;
        TextView colRecovered;
        TextView colDeaths;
        TextView colNewDeaths;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.item_county, null);
            holder = new ViewHolder();

            holder.colCountryName = (TextView) convertView.findViewById(R.id.colCountryName);
            holder.colCases = (TextView) convertView.findViewById(R.id.colCases);
            holder.colRecovered = (TextView) convertView.findViewById(R.id.colRecovered);
            holder.colDeaths = (TextView) convertView.findViewById(R.id.colDeaths);
            holder.colNewCases = (TextView) convertView.findViewById(R.id.colNewCases);
            holder.colNewDeaths = (TextView) convertView.findViewById(R.id.colNewDeaths);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.colCountryName.setText(allCountriesResults.get(position).countryName);
        holder.colCases.setText(allCountriesResults.get(position).cases);
        holder.colNewCases.setText(allCountriesResults.get(position).newCases);
        holder.colRecovered.setText(allCountriesResults.get(position).recovered);
        holder.colDeaths.setText(allCountriesResults.get(position).deaths);
        holder.colNewDeaths.setText(allCountriesResults.get(position).newDeaths);


        return convertView;
    }


}
