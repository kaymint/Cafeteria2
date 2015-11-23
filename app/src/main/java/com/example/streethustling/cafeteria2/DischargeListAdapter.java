package com.example.streethustling.cafeteria2;

/**
 * Created by StreetHustling on 11/23/15.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.HashMap;
import java.util.List;



/**
 * Created by StreetHustling on 11/19/15.
 */
public class DischargeListAdapter extends BaseAdapter {

    private Activity activity;
    private List<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;



    public DischargeListAdapter (Activity a, List<HashMap<String, String>> d) {
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

        public TextView dischargedMeal;
        public TextView dischargedPrice;
        public TextView dischargedTime;
        public TextView dischargedDate;

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        final ViewHolder holder;
        System.out.println(parent);
        if(convertView==null){
            vi = inflater.inflate(R.layout.discharge_list_item, null);

            holder = new ViewHolder();
            holder.dischargedMeal = (TextView) vi.findViewById(R.id.dischargeMeal);
            holder.dischargedPrice = (TextView) vi.findViewById(R.id.dischargePrice);
            holder.dischargedTime = (TextView) vi.findViewById(R.id.dischargedTime);
            holder.dischargedDate = (TextView) vi.findViewById(R.id.dischargeDate);

            vi.setTag(holder);
        }else {
            holder = (ViewHolder)vi.getTag();
        }

        if(data.size() <= 0){
            holder.dischargedMeal.setText("No Data");
        }else {
            final HashMap<String, String> listitem ;
            listitem = data.get(position);


            holder.dischargedMeal.setText(listitem.get("meal_name"));
            holder.dischargedPrice.setText(listitem.get("meal_price"));
            holder.dischargedDate.setText(listitem.get("order_date"));
            holder.dischargedTime.setText(listitem.get("discharge_time"));


        }
        return vi;
    }

}

