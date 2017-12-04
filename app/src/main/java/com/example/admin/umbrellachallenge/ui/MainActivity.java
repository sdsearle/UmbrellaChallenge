package com.example.admin.umbrellachallenge.ui;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.admin.umbrellachallenge.R;
import com.example.admin.umbrellachallenge.data.RetrofitHelper;
import com.example.admin.umbrellachallenge.data.model.CurrentObservation;
import com.example.admin.umbrellachallenge.data.model.HourlyForecast;
import com.example.admin.umbrellachallenge.data.model.WeatherResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private String zipCode;
    private String TAG = "MainTag";
    private List<HourlyForecast> hourlyForecasts = new ArrayList<>();
    private Observer<Response<WeatherResponse>> currentObserver = new Observer<Response<WeatherResponse>>() {
        @Override
        public void onSubscribe(Disposable d) {
            Log.d(TAG, "onSubscribe: ");
        }

        @Override
        public void onNext(Response<WeatherResponse> weatherResponseResponse) {
            if (weatherResponseResponse != null) {
                Log.d(TAG, "onNext: " + weatherResponseResponse.raw().request().url().toString());
                currentObservation = weatherResponseResponse.body().getCurrentObservation();
                hourlyForecasts = weatherResponseResponse.body().getHourlyForecast();
            } else {
                Log.d(TAG, "onNext: isNull");
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "onError: " + e.toString());
        }

        @Override
        public void onComplete() {
            if(hourlyForecasts != null) {
                setAdapter(hourlyForecasts);
                setTemp();
            }else{
                Toast.makeText(MainActivity.this, "Please provide a valid Zip Code", Toast.LENGTH_SHORT).show();
                alertDialog.show();
            }
        }
    };


    private CurrentObservation currentObservation;
    private CardView cvCard;
    private EditText etZip;
    private RecyclerView recyclerView;
    private HourlyRecyclerViewAdapter hourlyRecyclerViewAdapter;
    private TextView tvTemp;
    private TextView tvCondditon;
    private ImageView ivWeather;
    private int metric = 0;
    private View zipPrompt;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        LayoutInflater li = LayoutInflater.from(this);
        zipPrompt = li.inflate(R.layout.zipcode_alertdialog, null);

        EditText etZip = zipPrompt.findViewById(R.id.etZip);
        cvCard = findViewById(R.id.cvCard);
        tvTemp = findViewById(R.id.tvTemp);
        tvCondditon = findViewById(R.id.tvConddition);
        ivWeather = findViewById(R.id.ivWeather);

        recyclerView = findViewById(R.id.rvHourly);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        hourlyRecyclerViewAdapter = new HourlyRecyclerViewAdapter(hourlyForecasts, this);
        //recyclerView.getLayoutManager().setMeasuredDimension(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        recyclerView.setAdapter(hourlyRecyclerViewAdapter);


        //check for zip
        if (zipCode == null || zipCode == "") {
            createZipDiag(zipPrompt, etZip);
            alertDialog.show();
        } else {
            //get api
            Observable<Response<WeatherResponse>> responseObservable = RetrofitHelper.callCurrent(zipCode);
            responseObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(currentObserver);
        }
    }

    private void createZipDiag(View zipPrompt, EditText etZip) {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
        adBuilder.setView(zipPrompt)
                .setCancelable(false)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if(etZip.getText().length() != 5){
                            alertDialog.show();
                        }
                    }
                })
                .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        zipCode = etZip.getText().toString();
                        Log.d(TAG, "onClick: " + zipCode);

                        //get api
                        if (etZip.getText().length() == 5) {
                            Observable<Response<WeatherResponse>> responseObservable = RetrofitHelper.callCurrent(zipCode);
                            responseObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(currentObserver);
                        }else{
                            alertDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Please enter a valid zip", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialog = adBuilder.create();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    private void setTemp() {
        double temp = currentObservation.getTempF();
        if (temp < 60) {
            cvCard.setBackgroundColor(getResources().getColor(R.color.weather_cool));
        } else {
            cvCard.setBackgroundColor(getResources().getColor(R.color.weather_warm));
        }
        switch (metric) {
            case 0:
                tvTemp.setText(currentObservation.getTempF().toString()+"℉");
                break;

            case 1:
                tvTemp.setText(currentObservation.getTempC().toString()+"℃");
                break;
        }

        tvCondditon.setText(currentObservation.getWeather());
        if (currentObservation.getIcon().contains("cloudy")) {
            Glide.with(this).load(R.drawable.weather_cloudy).into(ivWeather);
        } else if (currentObservation.getIcon().contains("clear")) {
            Glide.with(this).load(R.drawable.weather_partlycloudy).into(ivWeather);
        } else if (currentObservation.getIcon().contains("sunny")) {
            Glide.with(this).load(R.drawable.weather_sunny).into(ivWeather);
        } else if (currentObservation.getIcon().contains("rain") && currentObservation.getIcon().contains("lightning")) {
            Glide.with(this).load(R.drawable.weather_lightning_rainy).into(ivWeather);
        } else if (currentObservation.getIcon().contains("lightning")) {
            Glide.with(this).load(R.drawable.weather_lightning).into(ivWeather);
        } else if (currentObservation.getIcon().contains("rain") && currentObservation.getIcon().contains("snow")) {
            Glide.with(this).load(R.drawable.weather_snowy_rainy).into(ivWeather);
        } else if (currentObservation.getIcon().contains("snow")) {
            Glide.with(this).load(R.drawable.weather_snowy).into(ivWeather);
        } else if (currentObservation.getIcon().contains("rain")) {
            Glide.with(this).load(R.drawable.weather_rainy).into(ivWeather);
        } else if (currentObservation.getIcon().contains("hail")) {
            Glide.with(this).load(R.drawable.weather_snowy).into(ivWeather);
        } else if (currentObservation.getIcon().contains("fog")) {
            Glide.with(this).load(R.drawable.weather_snowy).into(ivWeather);
        } else if (currentObservation.getIcon().contains("wind")) {
            Glide.with(this).load(R.drawable.weather_snowy).into(ivWeather);
        }
        getSupportActionBar().setTitle(currentObservation.getDisplayLocation().getFull());
    }

    private void setAdapter(List<HourlyForecast> hourlyForecasts) {
        Log.d(TAG, "setAdapter: " + hourlyForecasts.size());
        hourlyRecyclerViewAdapter.updateList(hourlyForecasts);
    }

    public void updateMetric(MenuItem item) {
        switch (item.getItemId()){
            case R.id.celsius:
                if (metric != 1) {
                    hourlyRecyclerViewAdapter.updateMetric(1);
                    tvTemp.setText(currentObservation.getTempC().toString()+"℃");
                    metric = 1;
                }
                break;

            case R.id.fahrenheit:
                if (metric != 0) {
                    hourlyRecyclerViewAdapter.updateMetric(0);
                    tvTemp.setText(currentObservation.getTempF().toString()+"℉");
                    metric = 0;
                }

                break;
        }
    }

    public void updateZip(MenuItem item) {
        /*if(zipPrompt.getParent()!= null) {
            ((ViewGroup) zipPrompt.getParent()).removeView(zipPrompt);
            etZip = zipPrompt.findViewById(R.id.etZip);
            createZipDiag(zipPrompt, etZip);
        }*/
        alertDialog.show();
    }
}
