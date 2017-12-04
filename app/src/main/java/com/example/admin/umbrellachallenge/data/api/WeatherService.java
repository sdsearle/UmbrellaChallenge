package com.example.admin.umbrellachallenge.data.api;


import com.example.admin.umbrellachallenge.BuildConfig;
import com.example.admin.umbrellachallenge.data.model.WeatherResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Retrofit interface for fetching weather data
 */
public interface WeatherService {

    /**
     * Get the forecast for a given zip code using {@link Call}
     */
    @GET("/api/" + BuildConfig.API_KEY + "/conditions/hourly/q/{zip}.json")
    Call<WeatherResponse> forecastForZipCallable(@Path("zip") String zipCode);

    /**
     * Get the forecast for a given zip code using {@link Observable}
     */
    @GET("/api/" + BuildConfig.API_KEY + "/conditions/hourly/q/{zip}.json")
    Observable<Response<WeatherResponse>> forecastForZipObservable(@Path("zip") String zipCode);
}
