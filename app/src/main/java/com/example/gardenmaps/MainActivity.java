package com.example.gardenmaps;

import static java.lang.Math.round;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.gardenmaps.data.GardenMapsContract.PlotLand;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


// главный экран
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private double latPerm = 56.00;
    private double lonPerm = 56.14;

    private TextView tempTextView;
    private TextView speedwindTextView;
    private TextView humidityTextView;
    private ImageView weatherImageView;

    private static final int PLOT_LOADER = 123;
    PlotCursorAdapter plotCursorAdapter;

    ListView dataListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempTextView = findViewById(R.id.tempTextView);
        speedwindTextView = findViewById(R.id.speedwindTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        weatherImageView = findViewById(R.id.weatherImageView);
        new Weather(latPerm, lonPerm).execute();

        dataListView = findViewById(R.id.dataListView);

        // получение всех данных с нажатой записью в списке и переход к главной карте
        plotCursorAdapter = new PlotCursorAdapter(this, null, false);
        dataListView.setAdapter(plotCursorAdapter);

        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, PlotMapActivity.class);
                Uri currentPlotUri = ContentUris.withAppendedId(PlotLand.CONTENT_URI, l);
                intent.setData(currentPlotUri);
                startActivity(intent);
            }
        });

        // переменная с кнопкой добавления нового участка
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPlotActivity.class);
                startActivity(intent);
            }
        });

        LoaderManager.getInstance(this).initLoader(PLOT_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.downloadNewPlot:
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {
                PlotLand.KEY_ID,
                PlotLand.KEY_PLOT_NAME,
                PlotLand.KEY_PLOT_LOCATION
        };

        CursorLoader cursorLoader = new CursorLoader(this,
                PlotLand.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        plotCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        plotCursorAdapter.swapCursor(null);
    }

    private class Weather extends AsyncTask<Void, Void, String> {
        private double lat;
        private double lon;
        private String key = "";

        public Weather (double lat, double lon){
            this.lat = lat;
            this.lon = lon;
        }


        @Override
        protected String doInBackground(Void... voids) {
            String content = getContent("https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon +"&appid=" + key);
            return content;
        }

        protected void onPostExecute(String content) {
            try {
                JSONObject json = new JSONObject(content);
                Log.d("JSON", "---------------------------------------------");
                Log.d("JSON", content);
                double temp = json.getJSONObject("main").getDouble("temp") - 274.15;
                Log.d("JSON", String.valueOf(temp));
                Log.d("JSON", String.valueOf(json.getJSONArray("weather").getJSONObject(0).getString("main")));
                String weath = json.getJSONArray("weather").getJSONObject(0).getString("main");
                double speed = json.getJSONObject("wind").getDouble("speed");
                int humidity = json.getJSONObject("main").getInt("humidity");
                String ruWeather;

//                weath = "Clear";

                if(weath == "Thunderstorm"){
                    weatherImageView.setImageResource(R.drawable.blustery);
                    ruWeather = "Гроза";
                } else if(weath == "Drizzle" || weath == "Rain") {
                    weatherImageView.setImageResource(R.drawable.rainy);
                    ruWeather = "Дождь";
                } else if (weath == "Snow"){
                    weatherImageView.setImageResource(R.drawable.snowy);
                    ruWeather = "Снег";
                } else if (weath == "Atmosphere"){
                    weatherImageView.setImageResource(R.drawable.windy);
                    ruWeather = "Туман";
                } else if(weath == "Clear"){
                    weatherImageView.setImageResource(R.drawable.sun);
                    ruWeather = "Безоблачно";
                } else {
                    weatherImageView.setImageResource(R.drawable.cloudy);
                    ruWeather = "Облачно";
                }

                tempTextView.setText(String.valueOf((int)temp) + "º " + ruWeather);
                speedwindTextView.setText("Ветер " + String.valueOf(speed) + "м/с");
                humidityTextView.setText("Влажность " + String.valueOf(humidity) + "%");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String getContent(String path) {

            try {
                URL url = new URL(path);
                HttpsURLConnection c = (HttpsURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setReadTimeout(20000);
                c.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
                String content = "";
                String line = "";

                while((line = reader.readLine()) != null){
                    content += line + "\n";
                }

                return content;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
