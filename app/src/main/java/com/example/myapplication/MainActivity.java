package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.myapplication.api.ApiInterface;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.Response;
import com.example.myapplication.file.RealPathUtil;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Magnifier;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {
    private Button fileBtn, uploadBtn;
    private EditText tokenField, userField, labelField;
    private TextView textView;
    private ProgressDialog progressDialog;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileBtn = findViewById(R.id.pickerFileBtn);
        uploadBtn = findViewById(R.id.uploadFileBtn);
        tokenField = findViewById(R.id.tokenTextField);
        userField = findViewById(R.id.userField);
        labelField = findViewById(R.id.labelField);
        textView = findViewById(R.id.textView);
        progressDialog = new ProgressDialog(this);

        tokenField.setText("eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxNjk1MTYyMi1hZTE5LTQyYzItYjJlNy1lNGM1MGE2NDkwMGUifQ.eyJleHAiOjE2NzU5MTQwMTIsImlhdCI6MTY3NTIyMjgxMiwianRpIjoiMDVhMDJkMDMtOThlOC00YTdjLThiZTUtOWUxZjM3NjJkNDNhIiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay5taGVhbHRodm4uY29tL2F1dGgvcmVhbG1zL21hc3RlciIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIzZGQ0OWMxMC1mMGNjLTRkNzQtODE5ZS1mMGZjNTk1NjI3OTgiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJtaGVhbHRodm4iLCJzZXNzaW9uX3N0YXRlIjoiNjM4NjQ5YmItNzMxMS00YmRmLWJjMTktYTFmYTNhZjY3ZWY2IiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJ3ZWxsY2FyZTp1c2VyIiwid2VsbGNhcmU6YWRtaW4iLCJkZWZhdWx0LXJvbGVzLW1hc3RlciIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiI2Mzg2NDliYi03MzExLTRiZGYtYmMxOS1hMWZhM2FmNjdlZjYiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6Ijg0Mzc0NTIxMjAyIiwidXNlcmlkIjoiNjI1OTRkOWRhZDExZTM2YWRkZWY5NDMyIn0.Gz8SJu6VttCAiBOaIESqNKyXo1A2XjsQLlWpb3nnFpo");
        userField.setText("62594d9dad11e36addef9432");
        labelField.setText("consultation:636b63c3e94081c7f3113529");
    }

    ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) return;
                        uri = data.getData();
                        textView.setText("Picker file: " + uri.getPath());
                    }
                }
            });

    public void pickerFile(View view) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mGetContent.launch(Intent.createChooser(intent, "Choose a file"));
    }

    public void uploadFile(View view) {
        progressDialog.setMessage("Uploading ...");
        progressDialog.show();
        String token = tokenField.getText().toString();
        String user = userField.getText().toString();
        String labels = labelField.getText().toString();
        String realPath = RealPathUtil.getRealPath(this, uri);
        File file = new File(realPath);
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String ISODate = df.format(new Date(file.lastModified()));
        RequestBody filenamePayload = RequestBody.create( file.getName(), MediaType.parse("multipart/form-data"));
        RequestBody fileSizePayload = RequestBody.create(String.valueOf(file.length()),MediaType.parse("multipart/form-data"));
        RequestBody lastModifiedPayload = RequestBody.create(ISODate,MediaType.parse("multipart/form-data"));
        RequestBody filePayload = RequestBody.create(file,MediaType.parse("multipart/form-data") );
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData("file", file.getName(), filePayload);
        RequestBody userPayload = RequestBody.create(user, MediaType.parse("multipart/form-data"));
        RequestBody folderPayload = RequestBody.create("/",MediaType.parse("multipart/form-data"));
        RequestBody labelsPayload = RequestBody.create(labels,MediaType.parse("multipart/form-data"));
        ApiInterface iApi = ApiService.getClient().create(ApiInterface.class);
        iApi
                .uploadFile(token,
                        fileBody,
                        folderPayload,
                        filenamePayload,
                        lastModifiedPayload,
                        fileSizePayload,
                        userPayload,
                        labelsPayload)
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        progressDialog.dismiss();
                        Response data = response.body();
                        if (data == null) return;
                        textView.setText("Response: "+ " status code - " + data.getStatus() + " - id " +data.getId());
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        progressDialog.dismiss();
                        textView.setText("Error: " + t.getMessage());
                    }
                });
    }
}