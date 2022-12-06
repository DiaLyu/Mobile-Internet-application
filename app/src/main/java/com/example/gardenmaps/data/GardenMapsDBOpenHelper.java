package com.example.gardenmaps.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gardenmaps.data.GardenMapsContract.PlotLand;

// открытие базы данных
public class GardenMapsDBOpenHelper extends SQLiteOpenHelper {


    public GardenMapsDBOpenHelper(Context context) {
        super(context, GardenMapsContract.DATABASE_NAME, null, GardenMapsContract.DATABASE_VERSION);
    }

    // создаем таблицу с участками
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_PLOTS_TABLE = "CREATE TABLE " + PlotLand.TABLE_NAME + " ("
                + PlotLand.KEY_ID + " INTEGER PRIMARY KEY, "
                + PlotLand.KEY_PLOT_NAME + " TEXT, "
                + PlotLand.KEY_PLOT_LOCATION + " TEXT, "
                + PlotLand.KEY_PLOT_WIDTH + " INTEGER NOT NULL, "
                + PlotLand.KEY_PLOT_LENGTH + " INTEGER NOT NULL, "
                + PlotLand.KEY_PLOT_DESCR + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_PLOTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GardenMapsContract.DATABASE_NAME);
        onCreate(sqLiteDatabase);
    }
}
