package com.example.admin.umbrellachallenge.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.admin.umbrellachallenge.R;
import com.example.admin.umbrellachallenge.data.model.HourlyForecast;

import java.util.List;

public class HourlyRecyclerViewAdapter extends RecyclerView.Adapter<HourlyRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RedditRecyclerViewTag";
    private final Context context;
    int min = 0;
    int max = 0;
    private List<HourlyForecast> mValues;
    private int metric;

    public HourlyRecyclerViewAdapter(List<HourlyForecast> items, Context context) {
        mValues = items;
        this.context = context;
        for (int i = 1; i < mValues.size(); i++) {
            if(Integer.parseInt(mValues.get(i).getTemp().getEnglish()) < Integer.parseInt(mValues.get(min).getTemp().getEnglish())){
                min  = i;
            }
            if(Integer.parseInt(mValues.get(i).getTemp().getEnglish()) > Integer.parseInt(mValues.get(max).getTemp().getEnglish())){
                max  = i;
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hourly_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if(max != min) {
            if (position == min) {
                holder.cvCard.setBackgroundColor(context.getResources().getColor(R.color.weather_cool));
            } else if(position == max) {
                holder.cvCard.setBackgroundColor(context.getResources().getColor(R.color.weather_warm));
            }else{
                holder.cvCard.setBackgroundColor(Color.WHITE);
            }
        }
        switch (metric) {
            case 0:
                holder.tvTemp.setText(holder.mItem.getTemp().getEnglish()+"℉");
                break;

            case 1:
                holder.tvTemp.setText(holder.mItem.getTemp().getMetric()+"℃");
                break;
        }

        holder.tvTime.setText(holder.mItem.getFCTTIME().getCivil());
        if(holder.mItem.getIcon().contains("cloudy")) {
            Glide.with(context).load(R.drawable.weather_cloudy).into(holder.ivWeather);
        }else if(holder.mItem.getIcon().contains("clear")) {
            Glide.with(context).load(R.drawable.weather_partlycloudy).into(holder.ivWeather);
        }else if(holder.mItem.getIcon().contains("sunny")) {
            Glide.with(context).load(R.drawable.weather_sunny).into(holder.ivWeather);
        }else if(holder.mItem.getIcon().contains("rain") && holder.mItem.getIcon().contains("lightning")) {
            Glide.with(context).load(R.drawable.weather_lightning_rainy).into(holder.ivWeather);
        }else if(holder.mItem.getIcon().contains("lightning")) {
            Glide.with(context).load(R.drawable.weather_lightning).into(holder.ivWeather);
        }else if(holder.mItem.getIcon().contains("rain") && holder.mItem.getIcon().contains("snow")) {
            Glide.with(context).load(R.drawable.weather_snowy_rainy).into(holder.ivWeather);
        }else if(holder.mItem.getIcon().contains("snow")) {
            Glide.with(context).load(R.drawable.weather_snowy).into(holder.ivWeather);
        }else if(holder.mItem.getIcon().contains("rain")) {
            Glide.with(context).load(R.drawable.weather_rainy).into(holder.ivWeather);
        }else if(holder.mItem.getIcon().contains("hail")) {
            Glide.with(context).load(R.drawable.weather_snowy).into(holder.ivWeather);
        }else if(holder.mItem.getIcon().contains("fog")) {
            Glide.with(context).load(R.drawable.weather_snowy).into(holder.ivWeather);
        }else if(holder.mItem.getIcon().contains("wind")) {
            Glide.with(context).load(R.drawable.weather_snowy).into(holder.ivWeather);
        }
    }

    @Override
    public int getItemCount() {
        if(mValues != null) {
            Log.d(TAG, "getItemCount: " + mValues.size());
            return mValues.size();
        }
        return 0;
    }

    public void updateList(List<HourlyForecast> newlist) {
        mValues.clear();
        Log.d(TAG, "updateList: ");
        mValues = newlist;
        notifyDataSetChanged();
        for (int i = 1; i < mValues.size(); i++) {
            if(Integer.parseInt(mValues.get(i).getTemp().getEnglish()) < Integer.parseInt(mValues.get(min).getTemp().getEnglish())){
                min  = i;
            }
            if(Integer.parseInt(mValues.get(i).getTemp().getEnglish()) > Integer.parseInt(mValues.get(max).getTemp().getEnglish())){
                max  = i;
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private final CardView cvCard;
        private final TextView tvTemp;
        private final TextView tvTime;
        private final ImageView ivWeather;
        public HourlyForecast mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            cvCard = view.findViewById(R.id.cvCard);
            tvTemp = view.findViewById(R.id.tvTemp);
            tvTime = view.findViewById(R.id.tvTime);
            ivWeather = view.findViewById(R.id.ivWeather);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "example" + "'";
        }
    }

    public void updateMetric(int z) {
        metric = z;
        notifyDataSetChanged();
    }

}
