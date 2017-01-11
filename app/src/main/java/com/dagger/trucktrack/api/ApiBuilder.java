package com.dagger.trucktrack.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Harshit on 11/01/17.
 */

public final class ApiBuilder {

    private static final String BASE_URL = "http://139.59.9.255:1880/";

    private static Retrofit retrofit;

    public static ApiInterface getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiInterface.class);
    }

}
