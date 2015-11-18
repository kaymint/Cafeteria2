package com.example.streethustling.cafeteria2;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.support.v4.app.ListFragment;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
public class MealListFragment extends ListFragment implements AdapterView.OnItemClickListener {
    ListView listView;
    CheckBox checkBox;

    List<HashMap<String,String>> listinfo = new ArrayList<HashMap<String,String>>();

    private static final String ARG_SECTION_NUMBER = "section_number";

    String[] from = {"mealName","mealPrice"};
    int[] to = {R.id.mealTxt, R.id.price};

    String[] myFriends = new String[] {
            "Sunil Gupta",
            "Abhishek Tripathi",
            "Awadhesh Diwakar",
            "Amit Verma",
            "Jitendra Singh",
            "Ravi Jhansi",
            "Ashish Jain",
            "Sandeep Pal",
            "Shishir Verma",
            "Ravi BBD"
    };

    /*Array of names*/
    String[] names=new String[] {
            "Sunil Gupta",
            "Abhishek Tripathi",
            "Awadhesh Diwakar",
            "Amit Verma",
            "Jitendra Singh",
            "Ravi Jhansi",
            "Ashish Jain",
            "Sandeep Pal",
            "Shishir Verma",
            "Ravi BBD"
    };

    public static MealListFragment newInstance(int sectionNumber) {
        MealListFragment fragment = new MealListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        readWebpage(getView());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meal_list_fragment, container, false);
        listView = (ListView) view.findViewById(android.R.id.list);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast toast;
        toast = Toast.makeText(getContext(), "position: " + position, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void readWebpage(View view) {
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[]{"http://cs.ashesi.edu.gh/~csashesi/class2016/kenneth-mensah/cafeteria/" +
                "controller/meals-controller.php?cmd=1"});
    }

    public void notify(View v){
        System.out.println("Help Me!!!");

    }




    public class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        GetMeals task;

        public static final String MEAL_NAME = "meal_name";
        public static final String MEAL_ID = "meal_id";
        public static final String MEAL_STATUS = "meal_status";
        public static final String MEAL_PRICE = "meal_price";
        public static final String TAG_MEALS = "meals";
        public JSONArray meals;
        List<HashMap<String,String>> mealList = new ArrayList<HashMap<String,String>>();


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
            parseJSONLocally(result);
            SimpleAdapter sAdapter = new SimpleAdapter(getContext(), listinfo,
                    R.layout.meal_list,from, to);
            listView.setAdapter(sAdapter);
            listView.setAdapter(sAdapter);
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    System.out.println("I am here");
                    Toast toast = Toast.makeText(getContext(), "position: " + position, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

        }




        public void parseJSONLocally(String response){
            System.out.println("inside parse local" + response);
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
                        listinfo.add(hm);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
