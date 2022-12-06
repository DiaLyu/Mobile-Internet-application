package com.example.gardenmaps;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.gardenmaps.data.GardenMapsContract.PlotLand;

// работа с выводом информации с помощью ListView
public class PlotCursorAdapter extends CursorAdapter {

    public PlotCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.plot_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView namePlotTextView = view.findViewById(R.id.namePlotTextView);
        TextView locationPlotTextView = view.findViewById(R.id.locationPlotTextView);

        String namePlot = cursor.getString(cursor.getColumnIndexOrThrow(PlotLand.KEY_PLOT_NAME));
        String locationPlot = cursor.getString(cursor.getColumnIndexOrThrow(PlotLand.KEY_PLOT_LOCATION));

        // устанавливаем в элементы списка значения из базы данных
        namePlotTextView.setText(namePlot);
        locationPlotTextView.setText(locationPlot);
    }
}
