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
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gardenmaps.data.GardenMapsContract.*;

public class AddTreeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDIT_TREE_LOADER = 679;
    Uri currentTreeUri;

    private EditText nameTreeEditText;
    private EditText varietyEditText;
    private EditText dataPlantingEditText;
    private EditText xLocationEditText;
    private EditText yLocationEditText;

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
        xLocationEditText = findViewById(R.id.xLocationEditText);
        yLocationEditText = findViewById(R.id.yLocationEditText);

        final WebView wv = findViewById(R.id.wikipediaWebView);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadData("<html><head></head><body><h1>Head</h1></body></html>", "text/html", "UTF-8");
        Button b = findViewById(R.id.searchButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstWord = nameTreeEditText.getText().toString().trim();
                String secondWord = varietyEditText.getText().toString().trim();

                if(firstWord == ""){
                    Toast.makeText(AddTreeActivity.this, "Не введено название рассады", Toast.LENGTH_LONG).show();
                } else if(secondWord == ""){
                    Toast.makeText(AddTreeActivity.this, "Не введен сорт рассады", Toast.LENGTH_LONG).show();
                } else {
                    String link = "https://ru.wikipedia.org/wiki/" + firstWord.replace(' ', '_') + '_' +
                            secondWord.replace(' ', '_');
                    wv.loadUrl(link);
                }
            }
        });
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
        if(TextUtils.isEmpty(nameTree)){
            Toast.makeText(this, "Введите название дерева", Toast.LENGTH_LONG).show();
            return;
        } else if(TextUtils.isEmpty(variety)){
            Toast.makeText(this, "Введите сорт", Toast.LENGTH_LONG).show();
            return;
        }
        String xLocationStr = xLocationEditText.getText().toString().trim();
        String yLocationStr = yLocationEditText.getText().toString().trim();
        int x_location;
        int y_location;

        try{
            x_location = Integer.parseInt(xLocationStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Некорректный ввод числа Х", Toast.LENGTH_LONG).show();
            return;
        }
        try{
            y_location = Integer.parseInt(yLocationStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Некорректный ввод числа Y", Toast.LENGTH_LONG).show();
            return;
        }


        Log.d("Ошибка 1", "error");

        ContentValues contentValues = new ContentValues();
        contentValues.put(TreeInfo.KEY_PLOT_ID, idPlot);
        contentValues.put(TreeInfo.KEY_TREE_NAME, nameTree);
        contentValues.put(TreeInfo.KEY_TREE_VARIETY, variety);
        contentValues.put(TreeInfo.KEY_TREE_DATA_PLANTING, dataPlanting);
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
            int xLocationColumnIndex = data.getColumnIndex(TreeInfo.KEY_TREE_X_LOCATION);
            int yLocationColumnIndex = data.getColumnIndex(TreeInfo.KEY_TREE_Y_LOCATION);

            Log.d("Error 7", "error 7");
            String nameTree = data.getString(nameTreeColumnIndex);
            String varietyTree = data.getString(varietyTreeColumnIndex);
            String dataPlanting = data.getString(dataPlantingColumnIndex);
            int xLocation = data.getInt(xLocationColumnIndex);
            int yLocation = data.getInt(yLocationColumnIndex);

            Log.d("Error 8", "error 8");
            nameTreeEditText.setText(nameTree);
            varietyEditText.setText(varietyTree);
            dataPlantingEditText.setText(dataPlanting);
            xLocationEditText.setText(xLocation + "");
            yLocationEditText.setText(yLocation +"");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}