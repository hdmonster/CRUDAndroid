package com.genius.crudsec;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.genius.crudsec.CRUD.Create;
import com.genius.crudsec.CRUD.Update;
import com.genius.crudsec.Encapsulation.Profile;
import com.genius.crudsec.Encapsulation.ProfileId;
import com.genius.crudsec.Helper.ProfileAdapter;
import com.genius.crudsec.Helper.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String URL_PRODUCTS = "http://ubkplus.org/haydardzaky/crud-app/read.php";

    TextView txtId;

    //a list to store all the profiles
    List<Profile> profileList;

    RecyclerView recyclerView;

    Context context;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshSwipe();

        context = MainActivity.this;
        progressDialog = new ProgressDialog(context);


        //getting the recyclerview from xml
        recyclerView = findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        final String txtId = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.txtId)).getText().toString();
                        final CharSequence[] dialogitem = {"Edit Data", "Delete Data"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Action");
                        builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item) {
                                    case 0:
                                        Intent edit = new Intent(context, Update.class);
                                        edit.putExtra("id", txtId);
                                        finish();
                                        startActivity(edit);
                                        break;
                                    case 1:
                                        deleteProfile(txtId);
                                        break;
                                }
                            }
                        });
                        builder.create().show();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        final String txtId = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.txtId)).getText().toString();
                        final CharSequence[] dialogitem = {"Edit Data", "Delete Data"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Action");
                        builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item) {
                                    case 0:
                                        Intent edit = new Intent(context, Update.class);
                                        edit.putExtra("id", txtId);
                                        finish();
                                        startActivity(edit);
                                        break;
                                    case 1:
                                        deleteProfile(txtId);
                                        break;
                                }
                            }
                        });
                        builder.create().show();
                    }
                })
        );

        //initializing the productlist
        profileList = new ArrayList<>();

        //this method will fetch and parse json
        //to display it in recyclerview
        loadProducts();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Create.class);
                startActivity(intent);
            }
        });
    }

    private void loadProducts() {

        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject profile = array.getJSONObject(i);

                                //adding the product to product list
                                profileList.add(new Profile(
                                        profile.getInt("id"),
                                        profile.getString("name"),
                                        profile.getString("class"),
                                        profile.getString("school"),
                                        profile.getString("email"),
                                        profile.getString("avatar")
                                ));
                            }

                            //creating adapter object and setting it to recyclerview
                            ProfileAdapter adapter = new ProfileAdapter(MainActivity.this, profileList);
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void deleteProfile(final String id) {

        String url = "http://ubkplus.org/haydardzaky/crud-app/delete.php";
        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        progressDialog.setMessage("Deleting data...");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response){
                if(response.contains("fail")){
                    hideDialog();
                    Toast.makeText(context, "Failed to delete!", Toast.LENGTH_LONG).show();
                }else {
                    hideDialog();
                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(getIntent());

                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                hideDialog();
                Toast.makeText(context, "The server is unreachable", Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("id", id);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void showDialog(){
        if(!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog(){
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }



    private void refreshSwipe(){
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh,R.color.refresh1,R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        recreate();
                    }
                },2000);
            }
        });
    }

}
