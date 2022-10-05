package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherTimeAdapter extends RecyclerView.Adapter<WeatherTimeAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherTimeModel> weatherTimeModelArrayList;

    public WeatherTimeAdapter(Context context, ArrayList<WeatherTimeModel> weatherTimeModelArrayList) {
        this.context = context;
        this.weatherTimeModelArrayList = weatherTimeModelArrayList;
    }

    @NonNull
    @Override
    public WeatherTimeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weather_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherTimeAdapter.ViewHolder holder, int position) {
        WeatherTimeModel modal = weatherTimeModelArrayList.get(position);
        Glide.with(context).load("http:".concat(modal.getIcon())).into(holder.imgTemperatureB);
        holder.txtTemperatureA.setText(String.format("%s%s", modal.getTemperature(), context.getString(R.string.celsius_symbol)));
        holder.txtWindSpeed.setText(String.format("%s%s", modal.getWindSpeed(), "Km/s"));
        SimpleDateFormat formatIn = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat formatOut = new SimpleDateFormat("hh:mm aa");
        try {
            Date date = formatIn.parse(modal.getTime());
            holder.txtTime.setText(formatOut.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherTimeModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTime, txtTemperatureA, txtWindSpeed;
        private ImageView imgTemperatureB;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtTemperatureA = itemView.findViewById(R.id.txtTemperatureA);
            txtWindSpeed = itemView.findViewById(R.id.txtWindSpeed);
            imgTemperatureB = itemView.findViewById(R.id.imgTemperatureB);
        }
    }
}
