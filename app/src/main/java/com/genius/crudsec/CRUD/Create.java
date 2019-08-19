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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.genius.crudsec.Helper.RequestHandler;
import com.genius.crudsec.MainActivity;
import com.genius.crudsec.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Create extends AppCompatActivity {

    public static final String UPLOAD_URL = "http://ubkplus.org/haydardzaky/crud-app/create.php";
    public static final String UPLOAD_KEY = "avatar";

    static final int PICK_IMAGE_REQUEST = 1;
    private Button btnSelectImage, btnInsert;
    private ImageView imageView;
    private EditText edtName, edtClass, edtUniv, edtEmail;
    private Bitmap bitmap;

    private Uri filePath;


    Context context;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context = Create.this;
        progressDialog = new ProgressDialog(context);
        imageView = findViewById(R.id.createAvatar);
        edtName = findViewById(R.id.createName);
        edtClass = findViewById(R.id.createClass);
        edtUniv = findViewById(R.id.createUniv);
        edtEmail = findViewById(R.id.createEmail);

        btnSelectImage = findViewById(R.id.createSelectImage);
        btnInsert = findViewById(R.id.createSubmit);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageBrowse();
            }
        });

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String Class = edtClass.getText().toString();
                String school = edtUniv.getText().toString();
                String email = edtEmail.getText().toString();
                String url = "http://ubkplus.org/haydardzaky/crud-app/create.php";

                if (name.equals("") || Class.equals("") || school.equals("") || email.equals("")){
                    Toast.makeText(context, "All forms required!", Toast.LENGTH_SHORT).show();
                }else{
                    uploadImage();
                }
            }
        });



    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
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
                loading = ProgressDialog.show(Create.this, "Saving Data...", null,true,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                Intent i = new Intent(Create.this, MainActivity.class);
                finish();
                startActivity(i);
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);
                String name = edtName.getText().toString();
                String Class = edtClass.getText().toString();
                String school = edtUniv.getText().toString();
                String email = edtEmail.getText().toString();

                HashMap<String,String> data = new HashMap<>();

                data.put(UPLOAD_KEY, uploadImage);
                data.put("name", name);
                data.put("class", Class);
                data.put("school", school);
                data.put("email", email);
                String result = rh.sendPostRequest(UPLOAD_URL,data);

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }


}
