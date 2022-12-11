package com.example.gardenmaps.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.gardenmaps.data.GardenMapsContract.*;

// работа с данными из базы данных
public class GardenMapsContentProvider extends ContentProvider {

    GardenMapsDBOpenHelper dbOpenHelper;

    private static final int PLOTS = 111;   // код для работы со всей таблицей
    private static final int PLOT_ID = 222; // код для работы с отдельной записью в таблице PLOTS
    private static final int TREES = 333;
    private static final int TREES_ID = 444;

    // Creates a UriMatcher object.
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        uriMatcher.addURI(GardenMapsContract.AUTHORITY, GardenMapsContract.PATH_PLOTS, PLOTS);
        uriMatcher.addURI(GardenMapsContract.AUTHORITY, GardenMapsContract.PATH_PLOTS + "/#", PLOT_ID);
        uriMatcher.addURI(GardenMapsContract.AUTHORITY, GardenMapsContract.PATH_TREES, TREES);
        uriMatcher.addURI(GardenMapsContract.AUTHORITY, GardenMapsContract.PATH_TREES + "/#", TREES_ID );
    }

    @Override
    public boolean onCreate() {
        dbOpenHelper = new GardenMapsDBOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor;

        int match = uriMatcher.match(uri);

        switch (match) {
            case PLOTS:
                cursor = db.query(PlotLand.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;
            case PLOT_ID:
                s = PlotLand.KEY_ID + "=?"; // параметр отбора
                strings1 = new String[] {String.valueOf(ContentUris.parseId(uri))}; // преобразование uri
                                                                                    // в значение типа long
                cursor = db.query(PlotLand.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;
            case TREES:
                cursor = db.query(TreeInfo.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;
            case TREES_ID:
                s = TreeInfo.KEY_ID + "=?"; // параметр отбора
                strings1 = new String[] {String.valueOf(ContentUris.parseId(uri))}; // преобразование uri
                // в значение типа long
                cursor = db.query(TreeInfo.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;
            default:
                Toast.makeText(getContext(), "Incorrect URI", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Can't query incorrect URI " + uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case PLOTS:
                return PlotLand.CONTENT_MULTIPLE_ITEMS;

            case PLOT_ID:
                return PlotLand.CONTENT_SINGLE_ITEM;

            case TREES:
                return TreeInfo.CONTENT_MULTIPLE_ITEMS;

            case TREES_ID:
                return TreeInfo.CONTENT_SINGLE_ITEM;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        String plotName = contentValues.getAsString(PlotLand.KEY_PLOT_NAME);
        if(plotName == null) {
            throw new IllegalArgumentException("Вы должны ввести название участка");
        }

        String plotLocation = contentValues.getAsString(PlotLand.KEY_PLOT_LOCATION);
        if(plotLocation == null) {
            throw new IllegalArgumentException("Вы должны ввести расположение участка");
        }

        Integer plotWidth = contentValues.getAsInteger(PlotLand.KEY_PLOT_WIDTH);
        if(plotWidth == null) {
            throw new IllegalArgumentException("Вы должны ввести ширину участка");
        }

        Integer plotLength = contentValues.getAsInteger(PlotLand.KEY_PLOT_LENGTH);
        if(plotLength == null) {
            throw new IllegalArgumentException("Вы должны ввести длину участка");
        }

        String plotDescription = contentValues.getAsString(PlotLand.KEY_PLOT_DESCR);
        if(plotDescription == null) {
            throw new IllegalArgumentException("Вы должны ввести описание участка");
        }

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        switch (match) {
            case PLOTS:
                long id = db.insert(PlotLand.TABLE_NAME, null, contentValues);
                if(id == -1){
                    Log.e("insertMethod", "Inserting of data in the table failed for " + uri);
                    return null;
                }
                return ContentUris.withAppendedId(uri, id);

            case TREES:
                long id_tree = db.insert(TreeInfo.TABLE_NAME, null, contentValues);
                if(id_tree == -1){
                    Log.e("insertMethod", "Inserting of data in the table failed for " + uri);
                    return null;
                }
                return ContentUris.withAppendedId(uri, id_tree);

            default:
                throw new IllegalArgumentException("Inserting of data in the table failed for " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);

        switch (match) {
            case PLOTS:
                return db.delete(PlotLand.TABLE_NAME, s, strings);

            case PLOT_ID:
                s = PlotLand.KEY_ID + "=?"; // параметр отбора
                strings = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return db.delete(PlotLand.TABLE_NAME, s, strings);

            case TREES:
                return db.delete(TreeInfo.TABLE_NAME, s, strings);

            case TREES_ID:
                s = TreeInfo.KEY_ID + "=?"; // параметр отбора
                strings = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return db.delete(TreeInfo.TABLE_NAME, s, strings);

            default:
                throw new IllegalArgumentException("Can't delete this URI " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {

        if (contentValues.containsKey(PlotLand.KEY_PLOT_NAME)) {
            String plotName = contentValues.getAsString(PlotLand.KEY_PLOT_NAME);
            if(plotName == null) {
                throw new IllegalArgumentException("Вы должны ввести название участка");
            }
        }

        if(contentValues.containsKey(PlotLand.KEY_PLOT_LOCATION)) {
            String plotLocation = contentValues.getAsString(PlotLand.KEY_PLOT_LOCATION);
            if(plotLocation == null) {
                throw new IllegalArgumentException("Вы должны ввести расположение участка");
            }
        }

        if(contentValues.containsKey(PlotLand.KEY_PLOT_WIDTH)){
            Integer plotWidth = contentValues.getAsInteger(PlotLand.KEY_PLOT_WIDTH);
            if(plotWidth == null) {
                throw new IllegalArgumentException("Вы должны ввести ширину участка");
            }
        }

        if(contentValues.containsKey(PlotLand.KEY_PLOT_LENGTH)) {
            Integer plotLength = contentValues.getAsInteger(PlotLand.KEY_PLOT_LENGTH);
            if(plotLength == null) {
                throw new IllegalArgumentException("Вы должны ввести длину участка");
            }
        }

        if(contentValues.containsKey(PlotLand.KEY_PLOT_DESCR)) {
            String plotDescription = contentValues.getAsString(PlotLand.KEY_PLOT_DESCR);
            if(plotDescription == null) {
                throw new IllegalArgumentException("Вы должны ввести описание участка");
            }
        }

        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);

        switch (match) {
            case PLOTS:
                return db.update(PlotLand.TABLE_NAME, contentValues, s, strings);

            case PLOT_ID:
                s = PlotLand.KEY_ID + "=?"; // параметр отбора
                strings = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return db.update(PlotLand.TABLE_NAME, contentValues, s, strings);

            case TREES:
                return db.update(TreeInfo.TABLE_NAME, contentValues, s, strings);

            case TREES_ID:
                s = TreeInfo.KEY_ID + "=?"; // параметр отбора
                strings = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return db.update(TreeInfo.TABLE_NAME, contentValues, s, strings);

            default:
                throw new IllegalArgumentException("Can't update this URI " + uri);
        }
    }
}

// uri - Unified Resource Identifier (постоянный идентификатор ресурса)
// content://com.android.uraall.clubolymbus/members

// URL - Unified Resource Locator
// http://google.com
