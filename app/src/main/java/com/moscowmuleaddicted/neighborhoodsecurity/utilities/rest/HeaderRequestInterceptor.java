package com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Simone Ripamonti on 12/04/2017.
 */

public final class HeaderRequestInterceptor implements Interceptor {

    // Todo: set the correct auth_token

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request newRequest = originalRequest.newBuilder()
                .header("serivce_key", "moscowmule")
                .header("auth_token", "XXX").build();
        return chain.proceed(newRequest);
    }
}
