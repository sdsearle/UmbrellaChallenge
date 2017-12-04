package com.example.admin.umbrellachallenge.data;

import com.example.admin.umbrellachallenge.BuildConfig;
import com.example.admin.umbrellachallenge.data.model.WeatherResponse;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by admin on 12/1/2017.
 */

public class RetrofitHelper {
    //http://api.wunderground.com/api/f0dab408c7bb0ffd/conditions/q/CA/San_Francisco.json\

    private final static String BASE_URL = "http://api.wunderground.com/";

    public static Retrofit create(){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Observable<Response<WeatherResponse>> callCurrent(String zip){
        Retrofit retrofit = create();
        ApiService apiService =  retrofit.create(ApiService.class);
        return apiService.getCurrent(zip);

    }

    interface ApiService{

        @GET("/api/" + BuildConfig.API_KEY + "/conditions/hourly/q/{zip}.json")
        Observable<Response<WeatherResponse>> getCurrent(@Path("zip") String zip);

    }

}
