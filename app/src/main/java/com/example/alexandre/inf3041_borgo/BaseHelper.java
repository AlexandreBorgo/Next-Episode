package com.example.alexandre.inf3041_borgo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alexandre on 19/12/2016.
 */

public class BaseHelper extends SQLiteOpenHelper {

    protected final static int VERSION = 42;

    public static final String SERIES_ENTRYID = "entry_id";
    public static final String SERIES_ID = "id";
    public static final String SERIES_TITLE = "title";
    public static final String SERIES_LAST = "last";
    public static final String SERIES_NEXT = "next";
    public static final String SERIES_LASTID = "last_id";
    public static final String SERIES_NEXTID = "next_id";
    public static final String SERIES_IMAGE = "image";
    public static final String SERIES_BANNER = "banner";
    public static final String SERIES_SEEN = "seen";

    public static final String SERIES_TABLE_NAME = "series";
    public static final String SERIES_TABLE_CREATE =
            "CREATE TABLE " + SERIES_TABLE_NAME + " (" +
                    SERIES_ENTRYID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SERIES_ID + " REAL, " +
                    SERIES_TITLE + " TEXT, " +
                    SERIES_IMAGE + " TEXT, " +
                    SERIES_BANNER + " TEXT, " +
                    SERIES_LASTID + " INTEGER, " +
                    SERIES_NEXTID + " INTEGER, " +
                    SERIES_SEEN + " INTEGER, " +
                    SERIES_LAST + " TEXT, " +
                    SERIES_NEXT + " DATE);";
    public static final String SERIES_TABLE_DROP = "DROP TABLE IF EXISTS " + SERIES_TABLE_NAME + ";";

    public BaseHelper(Context context) {
        super(context, SERIES_TABLE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SERIES_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SERIES_TABLE_DROP);
        onCreate(sqLiteDatabase);
    }
}
