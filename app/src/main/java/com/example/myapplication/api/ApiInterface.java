package com.example.myapplication.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
    @Multipart
    @POST("upload")
    Call<Response> uploadFile(
            @Header("token") String token,
            @Part MultipartBody.Part file,
            @Part("folder") RequestBody folder,
            @Part("filename")
                    RequestBody filename,
            @Part("lastModified")
                    RequestBody lastModified,
            @Part("size")
                    RequestBody size,
            @Part("user")
                    RequestBody user,
            @Part("labels") RequestBody labels);
}
