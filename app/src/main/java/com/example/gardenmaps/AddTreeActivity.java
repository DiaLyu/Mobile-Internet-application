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
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gardenmaps.data.GardenMapsContract.*;

public class AddTreeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDIT_TREE_LOADER = 679;
    Uri currentTreeUri;

    private EditText nameTreeEditText;
    private EditText varietyEditText;
    private EditText dataPlantingEditText;
    private EditText wikipediaTreeEditText;

    private int idPlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tree);

        Intent intent = getIntent();
        Bundle arguments = intent.getExtras();
        idPlot = arguments.getInt("idPlot");

        currentTreeUri = intent.getData();

        if(currentTreeUri == null) {
            setTitle("Добавить дерево");
            invalidateOptionsMenu();
        } else {
            setTitle("Редактировать данные");
            LoaderManager.getInstance(this).initLoader(EDIT_TREE_LOADER, null, this);
        }

        nameTreeEditText = findViewById(R.id.nameTreeEditText);
        varietyEditText = findViewById(R.id.varietyEditText);
        dataPlantingEditText = findViewById(R.id.dataPlantingEditText);
        wikipediaTreeEditText = findViewById(R.id.wikipediaTreeEditText);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(currentTreeUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_tree);
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_tree_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.save_tree:
                saveTree();
                return true;
            case R.id.delete_tree:
                showDeleteTree();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteTree() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы хотите удалить дерево?");
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
        if(currentTreeUri != null){
            int rowsDeleted = getContentResolver().delete(currentTreeUri, null, null);

            if(rowsDeleted == 0){
                Toast.makeText(this, "Deleting of data from the table failed", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Tree is deleted", Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }

    private void saveTree(){

        String nameTree = nameTreeEditText.getText().toString().trim();
        String variety = varietyEditText.getText().toString().trim();
        String dataPlanting = dataPlantingEditText.getText().toString().trim();
        String wikipedia = wikipediaTreeEditText.getText().toString().trim();
        if(TextUtils.isEmpty(nameTree)){
            Toast.makeText(this, "Введите название дерева", Toast.LENGTH_LONG).show();
            return;
        } else if(TextUtils.isEmpty(variety)){
            Toast.makeText(this, "Введите сорт", Toast.LENGTH_LONG).show();
            return;
        }

        int x_location = 50;
        int y_location = 50;

        Log.d("Ошибка 1", "error");

        ContentValues contentValues = new ContentValues();
        contentValues.put(TreeInfo.KEY_PLOT_ID, idPlot);
        contentValues.put(TreeInfo.KEY_TREE_NAME, nameTree);
        contentValues.put(TreeInfo.KEY_TREE_VARIETY, variety);
        contentValues.put(TreeInfo.KEY_TREE_DATA_PLANTING, dataPlanting);
        contentValues.put(TreeInfo.KEY_TREE_WIKIPEDIA, wikipedia);
        contentValues.put(TreeInfo.KEY_TREE_X_LOCATION, x_location);
        contentValues.put(TreeInfo.KEY_TREE_Y_LOCATION, y_location);

        Log.d("Ошибка 2", "error");

        if(currentTreeUri == null) {
            // для того, чтобы поместить ContentValues используем
            // ContentResolver - определяет, какой контент провайдер использовать в зависимости от authority
            ContentResolver contentResolver = getContentResolver();
            Uri uri = contentResolver.insert(TreeInfo.CONTENT_URI, contentValues);
            Log.d("Ошибка 3", "error");

            if(uri == null){
                Toast.makeText(this, "Inserting of data in the table failed", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Data saved", Toast.LENGTH_LONG).show();
//                Intent intent2 = new Intent(AddPlotActivity.this, PlotMapActivity.class);
//                startActivity(intent2);
            }
        } else {
            int rowsChanged = getContentResolver().update(currentTreeUri, contentValues, null, null);

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
                TreeInfo.KEY_ID,
                TreeInfo.KEY_PLOT_ID,
                TreeInfo.KEY_TREE_NAME,
                TreeInfo.KEY_TREE_VARIETY,
                TreeInfo.KEY_TREE_DATA_PLANTING,
                TreeInfo.KEY_TREE_WIKIPEDIA,
                TreeInfo.KEY_TREE_X_LOCATION,
                TreeInfo.KEY_TREE_Y_LOCATION
        };

        return new CursorLoader(this,
                currentTreeUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            Log.d("Error 6", "error 6");
            int nameTreeColumnIndex = data.getColumnIndex(TreeInfo.KEY_TREE_NAME);
            int varietyTreeColumnIndex = data.getColumnIndex(TreeInfo.KEY_TREE_VARIETY);
            int dataPlantingColumnIndex = data.getColumnIndex(TreeInfo.KEY_TREE_DATA_PLANTING);
            int wikipediaColumnIndex = data.getColumnIndex(TreeInfo.KEY_TREE_WIKIPEDIA);

            Log.d("Error 7", "error 7");
            String nameTree = data.getString(nameTreeColumnIndex);
            String varietyTree = data.getString(varietyTreeColumnIndex);
            String dataPlanting = data.getString(dataPlantingColumnIndex);
            String wikipedia = data.getString(wikipediaColumnIndex);

            Log.d("Error 8", "error 8");
            nameTreeEditText.setText(nameTree);
            varietyEditText.setText(varietyTree);
            dataPlantingEditText.setText(dataPlanting);
            wikipediaTreeEditText.setText(wikipedia);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}