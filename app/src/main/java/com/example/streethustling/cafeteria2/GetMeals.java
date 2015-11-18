package com.example.streethustling.cafeteria2;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by StreetHustling on 11/18/15.
 */

public class GetMeals extends AsyncTask<String, Void, List<HashMap<String,String>>> {

    public static final String MEAL_NAME = "meal_name";
    public static final String MEAL_ID = "meal_id";
    public static final String MEAL_STATUS = "meal_status";
    public static final String MEAL_PRICE = "meal_price";
    public static final String TAG_MEALS = "meals";
    public JSONArray meals;
    List<HashMap<String,String>> mealList = new ArrayList<HashMap<String,String>>();

    public AsynResponse delegate = null;

    public GetMeals(AsynResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected List<HashMap<String,String>> doInBackground(String ... jsonResponse) {

        for (String response : jsonResponse) {
            if(response != null){
                try{
                    JSONObject jsonObj = new JSONObject(response);

                    meals = jsonObj.getJSONArray(TAG_MEALS);

                    for(int i = 0; i < meals.length(); i++){
                        JSONObject m = meals.getJSONObject(i);

                        HashMap<String,String> hm = new HashMap<String, String>();
                        hm.put("mealName", m.getString(MEAL_NAME));
                        hm.put("mealId", m.getString(MEAL_ID));
                        hm.put("mealPrice", m.getString(MEAL_PRICE));
                        hm.put("mealStatus", m.getString(MEAL_STATUS));
                        mealList.add(hm);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        return mealList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String,String>> s) {
        super.onPostExecute(s);
        delegate.processFinish(s);
    }
}
