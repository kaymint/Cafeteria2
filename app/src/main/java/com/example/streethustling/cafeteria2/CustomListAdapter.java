package com.example.streethustling.cafeteria2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by StreetHustling on 11/19/15.
 */
public class CustomListAdapter extends BaseAdapter {

    private Context activity;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolder holder;
        if(convertView==null){
            vi = inflater.inflate(R.layout.meal_list, null);

            holder = new ViewHolder();
            holder.mealName = (TextView) vi.findViewById(R.id.mealName);
            holder.mealPrice = (TextView) vi.findViewById(R.id.mealPrice);
            holder.checkBox = (CheckBox) vi.findViewById(R.id.notifyChk);

            vi.setTag(holder);
        }else {
            holder = (ViewHolder)vi.getTag();
        }

        if(data.size() <= 0){
            holder.mealName.setText("No Data");
        }else {
            HashMap<String, String> listitem ;
            listitem = data.get(position);

            holder.mealName.setText(listitem.get("mealName"));
            holder.mealPrice.setText(listitem.get("mealPrice"));
            String status = listitem.get("mealStatus");
            if(status.equals("1")){
                holder.checkBox.setChecked(false);
            }else{
                holder.checkBox.setChecked(true);
            }

        }
        return vi;
    }

}
