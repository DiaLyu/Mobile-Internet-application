package com.example.gardenmaps;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.gardenmaps.data.GardenMapsContract;
import com.example.gardenmaps.data.GardenMapsContract.TreeInfo;

// работа с выводом информации с помощью ListView
public class TreeCursorAdapter extends CursorAdapter {


    public TreeCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.tree_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView idPlotTextView = view.findViewById(R.id.idPlotTextView);
        TextView nameTreeTextView = view.findViewById(R.id.nameTreeTextView);
        TextView varietyTreeTextView = view.findViewById(R.id.varietyTreeTextView);

        int idPlot = cursor.getInt(cursor.getColumnIndexOrThrow(TreeInfo.KEY_PLOT_ID));
        String nameTree = cursor.getString(cursor.getColumnIndexOrThrow(TreeInfo.KEY_TREE_NAME));
        String varietyTree = cursor.getString(cursor.getColumnIndexOrThrow(TreeInfo.KEY_TREE_VARIETY));

            // устанавливаем в элементы списка значения из базы данных
        idPlotTextView.setText(""+idPlot);
        nameTreeTextView.setText(nameTree);
        varietyTreeTextView.setText(varietyTree);
    }
}
