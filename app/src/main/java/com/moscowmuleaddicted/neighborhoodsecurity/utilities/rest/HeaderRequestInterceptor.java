package com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.google.android.gms.internal.zzt.TAG;

/**
 * Created by Simone Ripamonti on 12/04/2017.
 */

public final class HeaderRequestInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String token = "";
        if (user != null){
            Task<GetTokenResult> getTokenTask = user.getToken(true);
            try {
                Tasks.await(getTokenTask);
                token = getTokenTask.getResult().getToken();
                Log.i(TAG, "interceptHeaderRequest:found user token "+token);
            } catch (ExecutionException | InterruptedException e) {
                Log.w(TAG, "interceptHeaderRequest:failure in finding user token", e);
            }

        }

        Request originalRequest = chain.request();
        Request newRequest = originalRequest.newBuilder()
                .header("Accept", "application/json")
                .header("service_key", "moscowmule")
                .header("auth_token", token).build();
        Log.i(TAG, "interceptHeaderRequest:proceeding request" );
        return chain.proceed(newRequest);
    }
}
