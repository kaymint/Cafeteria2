package com.example.streethustling.cafeteria2;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by StreetHustling on 11/18/15.
 */
public class DownloadWebPageTask extends AsyncTask<String, Void, String> {
    GetMeals task;
    AsynResponse del = null;

    public static final String MEAL_NAME = "meal_name";
    public static final String MEAL_ID = "meal_id";
    public static final String MEAL_STATUS = "meal_status";
    public static final String MEAL_PRICE = "meal_price";
    public static final String TAG_MEALS = "meals";
    public JSONArray meals;
    List<HashMap<String,String>> mealList = new ArrayList<HashMap<String,String>>();

    public DownloadWebPageTask(AsynResponse m){
        del = m;
    }

    @Override
    protected String doInBackground(String ... urls) {
        String response = "";
        HttpURLConnection conn = null;
        for (String url : urls) {
            try{
                URL theUrl = new URL(url);

                conn = (HttpURLConnection) theUrl.openConnection();
                System.out.println(theUrl);

                InputStream content = new BufferedInputStream(conn.getInputStream());

                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    System.out.println(s);
                    response += s;
                }
                System.out.println(response);
                //return response;

            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                conn.disconnect();
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
//            textView.setText(result);
        //Toast.makeText(, result, Toast.LENGTH_LONG).show();
        //parseJSON(result);
        parseJSONLocally(result);
    }

    private void parseJSON(String result){
        System.out.println(result);
        task = new GetMeals(del);
        task.execute(new String[]{result});
    }

    public void parseJSONLocally(String response){
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
        del.processFinish(mealList);
    }

}
