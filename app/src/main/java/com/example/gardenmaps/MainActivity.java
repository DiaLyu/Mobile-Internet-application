package com.example.gardenmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.gardenmaps.data.GardenMapsContract.PlotLand;


// главный экран
public class MainActivity extends AppCompatActivity {

    ListView dataListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataListView = findViewById(R.id.dataListView);

        // переменная с кнопкой добавления нового участка
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPlotActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayData();
    }

    private void displayData() {
        String[] projection = {
                PlotLand.KEY_ID,
                PlotLand.KEY_PLOT_NAME,
                PlotLand.KEY_PLOT_LOCATION,
                PlotLand.KEY_PLOT_WIDTH,
                PlotLand.KEY_PLOT_LENGTH,
                PlotLand.KEY_PLOT_DESCR
        };

        Cursor cursor = getContentResolver().query(
                PlotLand.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        PlotCursorAdapter cursorAdapter = new PlotCursorAdapter(this, cursor, false);
        dataListView.setAdapter(cursorAdapter);

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
}