package com.genius.crudsec.CRUD;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.genius.crudsec.Encapsulation.Profile;
import com.genius.crudsec.Helper.MySingleton;
import com.genius.crudsec.Helper.ProfileAdapter;
import com.genius.crudsec.Helper.RequestHandler;
import com.genius.crudsec.MainActivity;
import com.genius.crudsec.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Update extends AppCompatActivity {

    static final int PICK_IMAGE_REQUEST = 1;
    private Button btnSelectImage, btnInsert, btnUpload;
    private ImageView imageView;
    private EditText edtName, edtClass, edtSchool, edtEmail;
    private Bitmap bitmap;

    private Uri filePath;


    Context context;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        loadProducts();

        getSupportActionBar().setTitle("Edit Data");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context = Update.this;
        progressDialog = new ProgressDialog(context);
        imageView = findViewById(R.id.updateAvatar);
        edtName = findViewById(R.id.updateName);
        edtClass = findViewById(R.id.updateClass);
        edtSchool = findViewById(R.id.updateSchool);
        edtEmail = findViewById(R.id.updateEmail);

        btnSelectImage = findViewById(R.id.updateSelect);
        btnUpload = findViewById(R.id.updateUpload);
        btnInsert = findViewById(R.id.updateSave);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageBrowse();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = getIntent().getStringExtra("id");
                String name = edtName.getText().toString();
                String Class = edtClass.getText().toString();
                String school = edtSchool.getText().toString();
                String email = edtEmail.getText().toString();
                String url = "http://ubkplus.org/haydardzaky/crud-app/update.php";

                if (name.equals("") || Class.equals("") || school.equals("") || email.equals("")){
                    Toast.makeText(context, "All forms required!", Toast.LENGTH_SHORT).show();
                }else{
                    register(id, name, Class, school, email, url);
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    private void register(final String id, final String name, final String Class, final String school, final String email, final String url) {

        progressDialog.setMessage("Updating data...");
        showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response){
                if(response.contains("fail")){
                    hideDialog();
                    Toast.makeText(Update.this, "Failed to update!", Toast.LENGTH_LONG).show();
                }else {
                    hideDialog();
                    Toast.makeText(Update.this, "Updated Successfully", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(Update.this, MainActivity.class);
                    finish();
                    startActivity(i);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                hideDialog();
                Toast.makeText(Update.this, "The server is unreachable", Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("id", id);
                params.put("name",name );
                params.put("class", Class);
                params.put("school",school );
                params.put("email", email);
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

    private void imageBrowse() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        class UploadImage extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Update.this, "Updating avatar...", null,true,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                Intent i = new Intent(Update.this, MainActivity.class);
                startActivity(i);
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);
                String id = getIntent().getStringExtra("id");
                String UPLOAD_URL = "http://ubkplus.org/haydardzaky/crud-app/update-avatar.php";

                HashMap<String,String> data = new HashMap<>();

                data.put("avatar", uploadImage);
                data.put("id", id);
                String result = rh.sendPostRequest(UPLOAD_URL,data);

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }

    private void loadProducts() {

        String id = getIntent().getStringExtra("id");
        String URL_PRODUCTS = "http://ubkplus.org/haydardzaky/crud-app/read-data.php?id="+id;
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

                                //loading the image
                                Glide.with(context)
                                        .load(profile.getString("avatar"))
                                        .into(imageView);

                                edtName.setText(profile.getString("name"));
                                edtClass.setText(profile.getString("class"));
                                edtSchool.setText(profile.getString("school"));
                                edtEmail.setText(profile.getString("email"));

                            }

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
}
