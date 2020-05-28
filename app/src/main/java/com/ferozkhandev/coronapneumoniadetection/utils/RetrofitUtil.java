package com.ferozkhandev.coronapneumoniadetection.utils;

import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {
    //Retrofit Builder Initialization
    private static retrofit2.Retrofit.Builder retrofitBuilder = new retrofit2.Retrofit.Builder()
            .baseUrl(URLs.host)
            .addConverterFactory(GsonConverterFactory.create());
    public static retrofit2.Retrofit retrofit = retrofitBuilder.build();
}
