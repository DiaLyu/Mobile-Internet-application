package com.example.gardenmaps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gardenmaps.data.GardenMapsContract.*;
import com.example.gardenmaps.data.GardenMapsDBOpenHelper;

// экран с добавлением нового участка
public class AddPlotActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDIT_PLOT_LOADER = 344;
    Uri currentPlotUri;

    private EditText namePlotEditText;        // название участка
    private EditText locationEditText;        // расположение участка
    private EditText widthPlotEditText;       // ширина участка
    private EditText lengthPlotEditText;      // длина участка
    private EditText descriptionPlotEditText; // описание участка

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plot);

        intent = getIntent();
        Log.d("Error 1", "error 1");

        currentPlotUri = intent.getData();

        if(currentPlotUri == null) {
            setTitle("Добавить участок");
            invalidateOptionsMenu();
        } else {
            Log.d("Error 2", "error 2");
            setTitle("Редактировать участок");
            LoaderManager.getInstance(this).initLoader(EDIT_PLOT_LOADER, null, this);
        }

        // связываем переменные с элементами на экране
        namePlotEditText = findViewById(R.id.namePlotEditText);
        locationEditText = findViewById(R.id.locationEditText);
        widthPlotEditText = findViewById(R.id.widthPlotEditText);
        lengthPlotEditText = findViewById(R.id.lengthPlotEditText);
        descriptionPlotEditText = findViewById(R.id.descriptionPlotEditText);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(currentPlotUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_plot);
            menuItem.setVisible(false);
        }

        return true;
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
                savePlot();
                return true;
            case R.id.delete_plot:
                showDeletePlotDialog();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeletePlotDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы хотите удалить участок?");
        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletePlot();
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePlot() {
        if(currentPlotUri != null){
            int idPlot = 0;
            Cursor cursor1 = getContentResolver().query(currentPlotUri, null, null, null, null);
            if(cursor1.moveToFirst()){
                int idTreeColumnIndex = cursor1.getColumnIndex(PlotLand.KEY_ID);
                idPlot = cursor1.getInt(idTreeColumnIndex);
            }

            GardenMapsDBOpenHelper dbOpenHelper = new GardenMapsDBOpenHelper(getApplicationContext());;
            SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

            db.execSQL("DELETE FROM " + TreeInfo.TABLE_NAME + " WHERE " + TreeInfo.KEY_PLOT_ID + "=?", new String[]{""+idPlot});

            int rowsDeleted = getContentResolver().delete(currentPlotUri, null, null);

            if(rowsDeleted == 0){
                Toast.makeText(this, "Deleting of data from the table failed", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Member is deleted", Toast.LENGTH_LONG).show();
            }
            finish();
            Intent intent_2 = new Intent(AddPlotActivity.this, MainActivity.class);
            intent_2.setData(currentPlotUri);
            startActivity(intent_2);
        }
    }

    private void savePlot(){

        String namePlot = namePlotEditText.getText().toString().trim();
        String locationPlot = locationEditText.getText().toString().trim();
        if(TextUtils.isEmpty(namePlot)){
            Toast.makeText(this, "Введите название", Toast.LENGTH_LONG).show();
            return;
        } else if(TextUtils.isEmpty(locationPlot)){
            Toast.makeText(this, "Введите расположение участка", Toast.LENGTH_LONG).show();
            return;
        }

        int widthPlot;
        try{
            widthPlot = Integer.parseInt(widthPlotEditText.getText().toString().trim());
        } catch (NumberFormatException e){
            if(widthPlotEditText.getText().toString().trim().equals("")){
                widthPlot = 0;
            } else {
                Toast.makeText(this, "Введите корректную ширину", Toast.LENGTH_LONG).show();
                return;
            }
        }
        int lengthPlot;
        try{
            lengthPlot = Integer.parseInt(lengthPlotEditText.getText().toString().trim());
        } catch (NumberFormatException e){
            if(lengthPlotEditText.getText().toString().trim().equals("")){
                lengthPlot = 0;
            } else {
                Toast.makeText(this, "Введите корректную длину", Toast.LENGTH_LONG).show();
                return;
            }
        }
        String descrPlot = descriptionPlotEditText.getText().toString().trim();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlotLand.KEY_PLOT_NAME, namePlot);
        contentValues.put(PlotLand.KEY_PLOT_LOCATION, locationPlot);
        contentValues.put(PlotLand.KEY_PLOT_WIDTH, widthPlot);
        contentValues.put(PlotLand.KEY_PLOT_LENGTH, lengthPlot);
        contentValues.put(PlotLand.KEY_PLOT_DESCR, descrPlot);

        if(currentPlotUri == null) {
            // для того, чтобы поместить ContentValues используем
            // ContentResolver - определяет, какой контент провайдер использовать в зависимости от authority
            ContentResolver contentResolver = getContentResolver();
            Uri uri = contentResolver.insert(PlotLand.CONTENT_URI, contentValues);

            if(uri == null){
                Toast.makeText(this, "Inserting of data in the table failed", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Data saved", Toast.LENGTH_LONG).show();
//                Intent intent2 = new Intent(AddPlotActivity.this, PlotMapActivity.class);
//                startActivity(intent2);
            }
        } else {
            int rowsChanged = getContentResolver().update(currentPlotUri, contentValues, null, null);

            if(rowsChanged == 0) {
                Toast.makeText(this, "Saving of data in the table failed", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Plot updated", Toast.LENGTH_LONG).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {
                PlotLand.KEY_ID,
                PlotLand.KEY_PLOT_NAME,
                PlotLand.KEY_PLOT_LOCATION,
                PlotLand.KEY_PLOT_WIDTH,
                PlotLand.KEY_PLOT_LENGTH,
                PlotLand.KEY_PLOT_DESCR
        };
        Log.d("Error 5", "error 5");

        return new CursorLoader(this,
                currentPlotUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d("Error 10", "error 10");
        if(data.moveToFirst()){
            Log.d("Error 6", "error 6");
            int namePlotColumnIndex = data.getColumnIndex(PlotLand.KEY_PLOT_NAME);
            int locationPlotColumnIndex = data.getColumnIndex(PlotLand.KEY_PLOT_LOCATION);
            int widthPlotColumnIndex = data.getColumnIndex(PlotLand.KEY_PLOT_WIDTH);
            int lengthPlotColumnIndex = data.getColumnIndex(PlotLand.KEY_PLOT_LENGTH);
            int descrPlotColumnIndex = data.getColumnIndex(PlotLand.KEY_PLOT_DESCR);

            Log.d("Error 7", "error 7");
            String namePlot = data.getString(namePlotColumnIndex);
            String locationPlot = data.getString(locationPlotColumnIndex);
            int widthPlot = data.getInt(widthPlotColumnIndex);
            int lengthPlot = data.getInt(lengthPlotColumnIndex);
            String descrPlot = data.getString(descrPlotColumnIndex);

            Log.d("Error 8", "error 8");
            namePlotEditText.setText(namePlot);
            locationEditText.setText(locationPlot);
            widthPlotEditText.setText(String.valueOf(widthPlot));
            lengthPlotEditText.setText(String.valueOf(lengthPlot));
            descriptionPlotEditText.setText(descrPlot);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}