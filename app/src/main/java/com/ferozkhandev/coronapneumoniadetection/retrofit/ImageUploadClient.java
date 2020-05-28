package com.ferozkhandev.coronapneumoniadetection.retrofit;

import com.ferozkhandev.coronapneumoniadetection.utils.URLs;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ImageUploadClient {
    @Multipart
    @POST(URLs.IMAGE_UPLOAD)
    Call<String> uploadimage(@Part MultipartBody.Part file);
}
