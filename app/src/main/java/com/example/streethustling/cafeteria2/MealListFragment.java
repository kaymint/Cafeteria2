package com.example.streethustling.cafeteria2;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.ListFragment;
import android.widget.CheckBox;
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
public class MealListFragment extends ListFragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener{
    ListView listView;
    OnMealSelectedListener mCallbacks;

    List<HashMap<String,String>> listinfo = new ArrayList<HashMap<String,String>>();
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String ARG_SECTION_NUMBER = "section_number";

    String[] from = {"mealName","mealPrice"};
    int[] to = {R.id.mealTxt, R.id.price};


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
        readWebpage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meal_list_fragment, container, false);
        listView = (ListView) view.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout2);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.swipeColor);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        readWebpage();
                                    }
                                }
        );
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CustomListAdapter sAdapter = new CustomListAdapter(getActivity(),listinfo);
        listView.setAdapter(sAdapter);
        listView = getListView();
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //mCallbacks.mealChecked(position, listinfo);
                CheckBox chk = (CheckBox) view.findViewById(R.id.notifyChk);
                HashMap<String, String> test = listinfo.get(position);
                String selectedMeal = test.get("mealName");
                String mId = test.get("mealId");
                if (!chk.isChecked()) {
                    chk.setChecked(true);
                    Toast.makeText(getContext(), selectedMeal + " checked", Toast.LENGTH_SHORT).show();
                    updateMealStatus(getView(),mId, "2" );
                } else {
                    chk.setChecked(false);
                    Toast.makeText(getContext(), selectedMeal + " unchecked", Toast.LENGTH_SHORT).show();
                    updateMealStatus(getView(), mId, "1");
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCallbacks.mealChecked(position, listinfo);
        HashMap<String, String> test = listinfo.get(position);
        String selectedMeal = test.get("mealName");

        Toast toast = Toast.makeText(getContext(), selectedMeal + "", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onRefresh() {
        readWebpage();
    }

    public interface OnMealSelectedListener{
        public void mealChecked(int position, List<HashMap<String,String>> list);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallbacks = (OnMealSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnMealSelectedListener");
        }
    }

    public void readWebpage() {
        DownloadWebPageTask task = new DownloadWebPageTask();
        listinfo.clear();
        task.execute(new String[]{"http://50.63.128.135/~csashesi/class2016/kenneth-mensah/cafeteria/" +
                "controller/meals-controller.php?cmd=1"});
    }

    public void updateMealStatus(View view, String id, String status) {
        UpdateMealStatus task = new UpdateMealStatus();
        task.execute(new String[]{"http://50.63.128.135/~csashesi/class2016/kenneth-mensah/cafeteria/" +
                "controller/meals-controller.php?cmd=3&id="+id+"&status="+status});
    }



    public void notify(View v){
        System.out.println("Help Me!!!");

    }

    public void showList(){
        CustomListAdapter sAdapter = new CustomListAdapter(getActivity(),listinfo);
        listView.setAdapter(sAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }


    private class UpdateMealStatus extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection conn = null;
            for (String url : urls) {
                try {
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

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    conn.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast toast;
            toast = Toast.makeText(getContext(), "meal status updated", Toast.LENGTH_SHORT);
            toast.show();
        }
    }




    public class DownloadWebPageTask extends AsyncTask<String, Void, String> {


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
//            SimpleAdapter sAdapter = new SimpleAdapter(getContext(), listinfo,
//                    R.layout.meal_list,from, to);
//            listView.setAdapter(sAdapter);
            showList();
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
