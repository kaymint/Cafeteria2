package com.example.streethustling.cafeteria2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

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
                        Toast.makeText(activity, " ready", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(activity, "not ready", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
        return vi;
    }

}
