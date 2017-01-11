package com.dagger.trucktrack.api;

import com.dagger.trucktrack.model.TruckObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Harshit on 11/01/17.
 */

public interface ApiInterface {

    @GET("data")
    Call<List<TruckObject>> getLocation(@Query("deviceID") String id);

}
