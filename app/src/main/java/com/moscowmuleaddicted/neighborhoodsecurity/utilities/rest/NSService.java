package com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest;

import android.content.Context;

import java.lang.annotation.Annotation;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Simone Ripamonti on 12/04/2017.
 */

public class NSService {

    private static final String baseUrl = "thawing-taiga-87659.herokuapp.com";

    private static NSService instance;
    private static NSRestService restInterface;

    private NSService(Context context){

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HeaderRequestInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.omdbapi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        restInterface = retrofit.create(NSRestService.class);

    }

    public static synchronized NSService getInstance(Context context){
        if (instance == null){
            instance = new NSService(context);
        }
        return instance;
    }

}
