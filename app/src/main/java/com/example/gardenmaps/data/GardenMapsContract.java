package com.example.gardenmaps.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

// класс с информацией о базе данных с участками
public final class GardenMapsContract {

    private GardenMapsContract(){}

    public static final String DATABASE_NAME = "GardenMaps"; // название базы данных
    public static final int DATABASE_VERSION = 1;            // версия юазы данных

    // работа с Uri
    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.example.gardenmaps";
    public static final String PATH_PLOTS = "plot_land";

    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);

    // таблица с данными участка
    public static final class PlotLand {

        // название таблицы
        public static final String TABLE_NAME = "plot_land";

        // свойства таблицы
        public static final String KEY_ID = BaseColumns._ID;
        public static final String KEY_PLOT_NAME = "plot_name";
        public static final String KEY_PLOT_LOCATION = "plot_location";
        public static final String KEY_PLOT_WIDTH = "plot_width";
        public static final String KEY_PLOT_LENGTH = "plot_length";
        public static final String KEY_PLOT_DESCR = "plot_descr";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PLOTS);

        // mime type, который будет использоваться при передаче для работы с несколькими строками
        public static final String CONTENT_MULTIPLE_ITEMS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_PLOTS;
        // mime type, который будет использоваться при передаче для работы с одной строкой
        public static final String CONTENT_SINGLE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + AUTHORITY + "/" + PATH_PLOTS;
    }

}
