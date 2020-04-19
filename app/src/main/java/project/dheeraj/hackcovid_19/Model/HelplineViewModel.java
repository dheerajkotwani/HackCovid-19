package project.dheeraj.hackcovid_19.Model;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import project.dheeraj.hackcovid_19.BuildConfig;

import static android.content.Context.MODE_PRIVATE;

public class HelplineViewModel extends AndroidViewModel {
    private MutableLiveData<List<StateData>> dataMutableLiveData;
    private ArrayList<StateData> headerData = new ArrayList<>();

    public HelplineViewModel(Application application) {
        super(application);
    }

    public LiveData<List<StateData>> getData() {
        if (dataMutableLiveData == null) {
            dataMutableLiveData = new MutableLiveData<>();
            refreshData();
        }
        return dataMutableLiveData;
    }

    private void refreshData() {
        //predict and state => POST
        RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("API", MODE_PRIVATE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, sharedPreferences.getString("API", BuildConfig.STATE_API) + "/api/helpline", response -> {
            try {
                JSONObject json = new JSONObject(response);
                JSONArray jsonArray = json.getJSONArray("helpline");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String sr;
                    if (jsonObject.getString("state").contains("&amp;")) {
                        sr = jsonObject.getString("state").replace("&amp;", "&");
                        headerData.add(new StateData(sr, jsonObject.getString("phone")));
                        saveData(headerData);
                        dataMutableLiveData.setValue(headerData);
                    } else {
                        headerData.add(new StateData(jsonObject.getString("state"), jsonObject.getString("phone")));
                        saveData(headerData);
                        dataMutableLiveData.setValue(headerData);
                    }
                }
            } catch (JSONException e) {
                loadData();
                Log.e("Error", String.valueOf(e));
            }
        }, error -> {
            loadData();
            Log.d("Error", Objects.requireNonNull(error.toString()));
        });
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    public void saveData(ArrayList headerData) {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(headerData);
        editor.putString("helplist", json);
        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("DATA", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("helplist", null);
        Type type = new TypeToken<ArrayList<StateData>>() {
        }.getType();
        headerData = gson.fromJson(json, type);

        if (headerData == null) {
            headerData = new ArrayList<>();
        }
        dataMutableLiveData.setValue(headerData);
    }
}