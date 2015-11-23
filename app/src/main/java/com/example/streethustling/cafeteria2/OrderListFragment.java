package com.example.streethustling.cafeteria2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
 * Created by StreetHustling on 11/19/15.
 */
public class OrderListFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener{

    ListView listView;
    List<HashMap<String,String>> orders = new ArrayList<HashMap<String,String>>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private OrderListAdapter sAdapter;

    private static final String TAG_RESULT = "result";
    private static final String TAG_ORDERS = "orders";
    private static final String TAG_MEALNAME = "meal_name";
    private static final String TAG_MEALPRICE = "meal_price";
    private static final String TAG_USERID = "user_id";
    private static final String TAG_ORDERDATE = "order_date";
    private static final String TAG_ORDERTIME = "order_time";
    private static final String TAG_ORDERID = "order_id";
    private static final String TAG_DISCHARGETIME = "discharge_time";


    public static OrderListFragment newInstance(int sectionNumber) {
        OrderListFragment fragment = new OrderListFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getOrderList(getView(), 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.order_list_fragment, container, false);
        listView = (ListView) view.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        getOrderList(view, 1);
                                    }
                                }
        );
        return view;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sAdapter = new OrderListAdapter(getActivity(),orders);
        listView.setAdapter(sAdapter);
        listView = getListView();
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                CheckBox chk = (CheckBox) view.findViewById(R.id.readyNotify);
                HashMap<String, String> test = orders.get(position);
                String selectedMeal = test.get(TAG_MEALNAME);
                final String oId = test.get(TAG_ORDERID);
                String uId = test.get(TAG_USERID);
                if (!chk.isChecked()) {
                    chk.setChecked(true);

                } else {
                    chk.setChecked(false);
                    Toast.makeText(getContext(), selectedMeal + " unchecked", Toast.LENGTH_SHORT).show();
                    //updateMealStatus(getView(), mId, "1");
                }

                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.custom_dialog);
                dialog.setTitle("Title...");

                // set the custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.cIdTxt);
                text.setText("Customer Id: " + uId);

                TextView orderId = (TextView) dialog.findViewById(R.id.pIDTxt);
                orderId.setText("Purchase Id: " + oId);

                Button okButton = (Button) dialog.findViewById(R.id.dischargeBtn);
                // if button is clicked, close the custom dialog
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        dischargeOrder(3, oId);
                        refreshList(position);
                    }
                });

                Button cancelButton = (Button) dialog.findViewById(R.id.cancelDischargeBtn);
                // if button is clicked, close the custom dialog
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                dialog.show();
            }
        });
    }

    public void getOrderList(View view, int asyncDescriptor) {

        OrderPageTask task = new OrderPageTask(asyncDescriptor);
        orders.clear();
        task.execute(new String[]{"http://50.63.128.135/~csashesi/class2016/agatha-maison/" +
                "MWC/group_project/response.php?cmd=1"});
    }

    public void notifyReady(int asyncDescriptor, int id){
        OrderPageTask task = new OrderPageTask(asyncDescriptor);
        //notify
        task.execute(new String[]{"http://50.63.128.135/~csashesi/class2016/agatha-maison" +
                "/MWC/group_project/response.php?cmd=3&id="+id});
    }


    public void dischargeOrder(int asyncDescriptor, String id){
        OrderPageTask task = new OrderPageTask(asyncDescriptor);
        //discharge
        task.execute(new String[]{"http://50.63.128.135/~csashesi/class2016/agatha-maison" +
                "/MWC/group_project/response.php?cmd=2&id="+id});
    }

    public void showList(){
        listView = getListView();
        System.out.println(orders.size());
        sAdapter = new OrderListAdapter(getActivity(),orders);
        listView.setAdapter(sAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    public void refreshList(int position){
        listView = getListView();
        orders.remove(position);
        sAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        getOrderList(getView(), 1);
    }


    public class OrderPageTask extends AsyncTask<String, Void, String> {

        public JSONArray orderArray;
        List<HashMap<String,String>> mealList = new ArrayList<HashMap<String,String>>();
        public int asyncHandler;

        public OrderPageTask(int asyncHandler){
            this.asyncHandler = asyncHandler;
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
            switch (asyncHandler){
                case 1:
                    //show order list
                    parseJSONLocally(result);
                    showList();
                    break;
                case 3:
                    parseDischargeJSON(result);
                    break;

            }

        }


        public void parseDischargeJSON(String result){
            //discharge meal
            //notify that order is ready
            System.out.println("inside parse local" + result);
            if(result != null){
                try{

                    JSONObject jsonObj = new JSONObject(result);

                    String success = jsonObj.getString("result");
                    if(success.equals("1")){
                        Toast.makeText(getContext(), "meal discharged", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(getContext(), "could not send discharge notification", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }





        public void parseJSONLocally(String response){
            System.out.println("inside parse local" + response);
            if(response != null){
                try{

                    JSONObject jsonObj = new JSONObject(response);

                        System.out.println("Parsing JSON");
                        orderArray = jsonObj.getJSONArray(TAG_ORDERS);

                        for (int i = 0; i < orderArray.length(); i++) {
                            JSONObject m = orderArray.getJSONObject(i);

                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put(TAG_MEALNAME, m.getString(TAG_MEALNAME));
                            hm.put(TAG_USERID, m.getString(TAG_USERID));
                            hm.put(TAG_MEALPRICE, m.getString(TAG_MEALPRICE));
                            hm.put(TAG_ORDERDATE, m.getString(TAG_ORDERDATE));
                            hm.put(TAG_DISCHARGETIME, m.getString(TAG_DISCHARGETIME));
                            hm.put(TAG_ORDERDATE, m.getString(TAG_ORDERDATE));
                            hm.put(TAG_ORDERTIME, m.getString(TAG_ORDERTIME));
                            hm.put(TAG_ORDERID, m.getString(TAG_ORDERID));
                            orders.add(hm);
                        }
                    System.out.println("order list size:"+ orders.size());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }





}
