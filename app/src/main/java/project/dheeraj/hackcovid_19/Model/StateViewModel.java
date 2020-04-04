package project.dheeraj.hackcovid_19.Model;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
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

import static android.content.Context.MODE_PRIVATE;

public class StateViewModel extends AndroidViewModel {

    private MutableLiveData<List<StateData>> dataMutableLiveData;
    private ArrayList<StateData> stateDataArrayList = new ArrayList<>();

    public StateViewModel(@NonNull Application application) {
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("API", MODE_PRIVATE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, sharedPreferences.getString("API", "http://ac41bf31.ngrok.io") + "/api/all", response -> {
            try {
                JSONObject json = new JSONObject(response);
                JSONArray jsonA = json.getJSONArray("all");
                for (int j = 0; j < jsonA.length(); j++) {
                    JSONObject js = jsonA.getJSONObject(j);
                    String statename = toTitleCase(js.getString("state"));
                    stateDataArrayList.add(new StateData(statename, js.getString("cases"),
                            js.getString("cured"), js.getString("death"), "Cases",
                            "Recovered", "Deaths"));
                    dataMutableLiveData.setValue(stateDataArrayList);
                }
                saveData(stateDataArrayList);
            } catch (JSONException e) {
                loadData();
                Log.e("Error", String.valueOf(e));
            }
        }, error -> {
            loadData();
            Log.d("Error", Objects.requireNonNull(error.toString()));
        });
        requestQueue.add(stringRequest);
    }

    public void saveData(ArrayList headerData) {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(headerData);
        editor.putString("statelist", json);
        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("DATA", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("statelist", null);
        Type type = new TypeToken<ArrayList<StateData>>() {
        }.getType();
        stateDataArrayList = gson.fromJson(json, type);

        if (stateDataArrayList == null) {
            stateDataArrayList = new ArrayList<>();
        }
        dataMutableLiveData.setValue(stateDataArrayList);
    }

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }
}

