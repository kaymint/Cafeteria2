package com.example.streethustling.cafeteria2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

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
public class CustomListAdapter extends BaseAdapter {

    private Activity activity;
    private List<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;



    public CustomListAdapter (Activity a, List<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{

        public TextView mealName;
        public TextView mealPrice;
        public CheckBox checkBox;

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        final ViewHolder holder;
        System.out.println(parent);
        if(convertView==null){
            vi = inflater.inflate(R.layout.meal_list, null);

            holder = new ViewHolder();
            holder.mealName = (TextView) vi.findViewById(R.id.mealTxt);
            holder.mealPrice = (TextView) vi.findViewById(R.id.price);
            holder.checkBox = (CheckBox) vi.findViewById(R.id.notifyChk);

            vi.setTag(holder);
        }else {
            holder = (ViewHolder)vi.getTag();
        }

        if(data.size() <= 0){
            holder.mealName.setText("No Data");
        }else {
            final HashMap<String, String> listitem ;
            listitem = data.get(position);


            holder.mealName.setText(listitem.get("mealName"));
            holder.mealPrice.setText(listitem.get("mealPrice"));
            String status = listitem.get("mealStatus");
            if(status.equals("1")){
                holder.checkBox.setChecked(false);
            }else{
                holder.checkBox.setChecked(true);
            }

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String,String> test = new HashMap<String,String>();
                    test = data.get(position);
                    String mId = test.get("mealId");
                    if(holder.checkBox.isChecked()){
                        Toast.makeText(activity, " available", Toast.LENGTH_SHORT).show();
                        updateMeal(mId, "2" );
                    }else{
                        Toast.makeText(activity, "not available", Toast.LENGTH_SHORT).show();
                        updateMeal(mId, "1" );
                    }

                }
            });

        }
        return vi;
    }

    public void updateMeal(String id, String status){
        UpdateMealStatus task = new UpdateMealStatus();
        task.execute(new String[]{"http://50.63.128.135/~csashesi/class2016/kenneth-mensah/cafeteria/" +
                "controller/meals-controller.php?cmd=3&id="+id+"&status="+status});
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
            toast = Toast.makeText(activity, "meal status updated", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
