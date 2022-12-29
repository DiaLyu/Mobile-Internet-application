package com.example.gardenmaps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.FileProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gardenmaps.data.GardenMapsContract.*;
import com.example.gardenmaps.data.GardenMapsDBOpenHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class PlotMapActivity extends AppCompatActivity{

    private static final int MAP_PLOT_LOADER = 213;
    private static final int LIST_TREE_LOADER = 524;
    TreeCursorAdapter treeCursorAdapter;
    Uri currentPlotUri;

    private int idPlot;
    private String namePlot;
    private int widthPlot;
    private int lengthPlot;

    ListView dataListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_map);

        dataListView = findViewById(R.id.dataTreeListView);

        Intent intent = getIntent();

        currentPlotUri = intent.getData();

        Cursor cursor1 = getContentResolver().query(currentPlotUri, null, null, null, null);
        if(cursor1.moveToFirst()){
            int idTreeColumnIndex = cursor1.getColumnIndex(PlotLand.KEY_ID);
            Log.d("NEWWWW", idTreeColumnIndex + " - " + PlotLand.KEY_ID);
            idPlot = cursor1.getInt(idTreeColumnIndex);
        }

        // переменная с кнопкой добавления нового участка
        FloatingActionButton floatingActionButton = findViewById(R.id.addTreeFloatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_new_tree = new Intent(PlotMapActivity.this, AddTreeActivity.class);
                intent_new_tree.putExtra("idPlot", idPlot);
                startActivity(intent_new_tree);
            }
        });

        treeCursorAdapter = new TreeCursorAdapter(this, null, false);
        dataListView.setAdapter(treeCursorAdapter);
        getSupportLoaderManager().initLoader(MAP_PLOT_LOADER, null, loader);
        getSupportLoaderManager().initLoader(LIST_TREE_LOADER, null, loader);

        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent_update_tree = new Intent(PlotMapActivity.this, AddTreeActivity.class);
                Uri currentUri = ContentUris.withAppendedId(TreeInfo.CONTENT_URI, l);
                intent_update_tree.setData(currentUri);
                intent_update_tree.putExtra("idPlot", idPlot);
                startActivity(intent_update_tree);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.plot_map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.share_plot:

                return true;
            case R.id.info_plot:
                Intent intent_2 = new Intent(PlotMapActivity.this, AddPlotActivity.class);
                intent_2.setData(currentPlotUri);
                startActivity(intent_2);
                return true;

            case R.id.main_screen:

                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    LoaderManager.LoaderCallbacks<Cursor> loader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch(id){
                case LIST_TREE_LOADER:
                    String[] projection = {
                            TreeInfo.KEY_ID,
                            TreeInfo.KEY_PLOT_ID,
                            TreeInfo.KEY_TREE_NAME,
                            TreeInfo.KEY_TREE_VARIETY,
                            TreeInfo.KEY_TREE_DATA_PLANTING,
                            TreeInfo.KEY_TREE_X_LOCATION,
                            TreeInfo.KEY_TREE_Y_LOCATION
                    };

                    return new CursorLoader(PlotMapActivity.this,
                            TreeInfo.CONTENT_URI,
                            projection,
                            null,
                            null,
                            null
                    );
                case MAP_PLOT_LOADER:
                    String[] projection1 = {
                            PlotLand.KEY_ID,
                            PlotLand.KEY_PLOT_NAME,
                            PlotLand.KEY_PLOT_LOCATION,
                            PlotLand.KEY_PLOT_WIDTH,
                            PlotLand.KEY_PLOT_LENGTH,
                            PlotLand.KEY_PLOT_DESCR
                    };
                    Log.d("Error 5", "error 5");

                    return new CursorLoader(PlotMapActivity.this,
                            currentPlotUri,
                            projection1,
                            null,
                            null,
                            null
                    );
            };
            return null;
        }

        @Override
        public void onLoadFinished(Loader loader, Cursor data) {
            switch(loader.getId()){
                case LIST_TREE_LOADER:
                    Cursor data1 = rawQueryWhere(TreeInfo.TABLE_NAME, TreeInfo.KEY_PLOT_ID);
                    treeCursorAdapter.swapCursor(data1);
                    break;
                case MAP_PLOT_LOADER:
                    if(data.moveToFirst()){
                        int idPlotColumnIndex = data.getColumnIndex(PlotLand.KEY_ID);
                        int namePlotColumnIndex = data.getColumnIndex(PlotLand.KEY_PLOT_NAME);
                        int widthPlotColumnIndex = data.getColumnIndex(PlotLand.KEY_PLOT_WIDTH);
                        int lengthPlotColumnIndex = data.getColumnIndex(PlotLand.KEY_PLOT_LENGTH);

                        idPlot = data.getInt(idPlotColumnIndex);
                        namePlot = data.getString(namePlotColumnIndex);
                        widthPlot = data.getInt(widthPlotColumnIndex);
                        lengthPlot = data.getInt(lengthPlotColumnIndex);

                        setTitle(namePlot);
                    }
                    break;
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {
            switch(loader.getId()){
                case LIST_TREE_LOADER:
                    treeCursorAdapter.swapCursor(null);
                    break;
                case MAP_PLOT_LOADER:
                    break;
            }
        }
    };

    public Cursor rawQueryWhere(String tableName, String tableColumn){
        GardenMapsDBOpenHelper dbOpenHelper = new GardenMapsDBOpenHelper(getApplicationContext());;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + tableColumn + "=?", new String[]{""+idPlot});
        return cursor;
    }
}