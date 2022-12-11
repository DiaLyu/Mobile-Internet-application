package com.example.gardenmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gardenmaps.data.GardenMapsContract.PlotLand;

// экран с добавлением нового участка
public class AddPlotActivity extends AppCompatActivity {

    private EditText namePlotEditText;        // название участка
    private EditText locationEditText;        // расположение участка
    private EditText widthPlotEditText;       // ширина участка
    private EditText lengthPlotEditText;      // длина участка
    private EditText descriptionPlotEditText; // описание участка
    //private ArrayAdapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plot);

        // связываем переменные с элементами на экране
        namePlotEditText = findViewById(R.id.namePlotEditText);
        locationEditText = findViewById(R.id.locationEditText);
        widthPlotEditText = findViewById(R.id.widthPlotEditText);
        lengthPlotEditText = findViewById(R.id.lengthPlotEditText);
        descriptionPlotEditText = findViewById(R.id.descriptionPlotEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_plot_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.save_plot:
                insertPlot();
                Intent intent = new Intent(AddPlotActivity.this, PlotMapActivity.class);
                startActivity(intent);
                return true;
            case R.id.delete_plot:
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertPlot(){

        String namePlot = namePlotEditText.getText().toString().trim();
        String locationPlot = locationEditText.getText().toString().trim();
        int widthPlot = Integer.parseInt(widthPlotEditText.getText().toString().trim());
        int lengthPlot = Integer.parseInt(lengthPlotEditText.getText().toString().trim());
        String descrPlot = descriptionPlotEditText.getText().toString().trim();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlotLand.KEY_PLOT_NAME, namePlot);
        contentValues.put(PlotLand.KEY_PLOT_LOCATION, locationPlot);
        contentValues.put(PlotLand.KEY_PLOT_WIDTH, widthPlot);
        contentValues.put(PlotLand.KEY_PLOT_LENGTH, lengthPlot);
        contentValues.put(PlotLand.KEY_PLOT_DESCR, descrPlot);

        // для того, чтобы поместить ContentValues используем
        // ContentResolver - определяет, какой контент провайдер использовать в зависимости от authority
        ContentResolver contentResolver = getContentResolver();
        Uri uri = contentResolver.insert(PlotLand.CONTENT_URI, contentValues);

        if(uri == null){
            Toast.makeText(this, "Inserting of data in the table failed", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Data saved", Toast.LENGTH_LONG).show();
        }
    }
}