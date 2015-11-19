package com.example.streethustling.cafeteria2;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.support.v4.app.FragmentManager;

import org.json.JSONArray;

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
public class AddMeal extends Fragment implements View.OnClickListener{


    Button cancel, add;
    EditText mName, mPrice;
    String name, price;
    AddMealListener mCallbacks;

    public static AddMeal newInstance(int sectionNumber) {
        AddMeal fragment = new AddMeal();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_layout, container, false);
        cancel = (Button) view.findViewById(R.id.cancelMealBtn);
        add = (Button) view.findViewById(R.id.addMealBtn);
        mName = (EditText) view.findViewById(R.id.mealName);
        mPrice = (EditText) view.findViewById(R.id.mealPrice);

        cancel.setOnClickListener(this);
        add.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //getListView().setOnItemClickListener(this);
    }

    public interface AddMealListener{
        public void onMealAdded();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallbacks = (AddMealListener ) context ;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    public void sendMealInfo(View view) {
        AddMealTask task = new AddMealTask();
        task.execute(new String[]{"http://cs.ashesi.edu.gh/~csashesi/class2016/kenneth-mensah/cafeteria/controller" +
                "/meals-controller.php?cmd=2&name="+name+"&price="+price+"&status=1"});
    }


    @Override
    public void onClick(View v) {
        if(v == cancel){

        }
        if(v == add){
            name = mName.getText().toString();
            name = name.replace(" ", "%20");
            price = mPrice.getText().toString();
            sendMealInfo(v);
            mCallbacks.onMealAdded();
        }
    }


    public class AddMealTask extends AsyncTask<String, Void, String> {

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
            //show meal added and redirect to fragment 2
            Toast toast;
            toast = Toast.makeText(getContext(), "meal added", Toast.LENGTH_SHORT);
            toast.show();

        }
    }
}
