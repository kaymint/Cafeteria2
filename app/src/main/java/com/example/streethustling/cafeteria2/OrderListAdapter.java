package com.example.streethustling.cafeteria2;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
public class OrderListAdapter extends BaseAdapter {

    private Activity activity;
    private List<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;



    public OrderListAdapter (Activity a, List<HashMap<String, String>> d) {
        activity = a;
        data=d;
        System.out.println(data.size());
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        // Define a way to determine which layout to use, here it's just evens and odds.
        return position % 2;
    }

    @Override
    public int getViewTypeCount() {
        return 2; // Count of different layouts
    }

    public static class ViewHolder{

        public TextView ordermeal;
        public TextView orderPrice;
        public TextView orderTime;
        public CheckBox readyChkBtn;

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        final ViewHolder holder;
        System.out.println(parent);
        if(convertView==null){
            vi = inflater.inflate(R.layout.order_list_item, null);

            holder = new ViewHolder();
            holder.ordermeal = (TextView) vi.findViewById(R.id.orderMeal);
            holder.orderPrice = (TextView) vi.findViewById(R.id.orderPrice);
            holder.orderTime = (TextView) vi.findViewById(R.id.orderTime);
            holder.readyChkBtn = (CheckBox) vi.findViewById(R.id.readyNotify);

            vi.setTag(holder);
        }else {
            holder = (ViewHolder)vi.getTag();
        }

        if(data.size() <= 0){
            holder.ordermeal.setText("No Data");
        }else {
            final HashMap<String, String> listitem ;
            listitem = data.get(position);


            holder.ordermeal.setText(listitem.get("meal_name"));
            holder.orderPrice.setText(listitem.get("meal_price"));
            holder.orderTime.setText(listitem.get("order_time"));


            holder.readyChkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String,String> test = new HashMap<String,String>();
                    test = data.get(position);
                    String mId = test.get("order_id");
                    if(holder.readyChkBtn.isChecked()){
                        ReadyPageTask task = new ReadyPageTask();
                        //notify
                        task.execute(new String[]{"http://50.63.128.135/~csashesi/class2016/agatha-maison" +
                                "/MWC/group_project/response.php?cmd=2&id="+mId});
                    }
                }
            });

        }
        return vi;
    }

    public class ReadyPageTask extends AsyncTask<String, Void, String> {


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
            parseReadyJSON(result);
        }

        public void parseReadyJSON(String result) {
            //notify that order is ready
            System.out.println("inside parse local" + result);
            if (result != null) {
                try {

                    JSONObject jsonObj = new JSONObject(result);

                    String success = jsonObj.getString("result");
                    if (success.equals("1")) {
                        Toast.makeText(activity, "meal ready", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "could not send notification", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
