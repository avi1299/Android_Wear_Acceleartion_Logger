package com.example.avi12.test2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static java.lang.System.currentTimeMillis;

public class SQLiteDBHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = ("Accelerometer_Data V. "/* + Long.toString(currentTimeMillis())*/);
    public static final String COLUMN_NAME_TIME = "Time";
    public static final String COLUMN_NAME_X = "X-Axis";
    public static final String COLUMN_NAME_Y = "Y-Axis";
    public static final String COLUMN_NAME_Z = "Z-Axis";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "//mnt//sdcard//Accelerometer_Data.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_TIME + " BIGINT PRIMARY KEY," +
                    COLUMN_NAME_X + " REAL," +
                    COLUMN_NAME_Y + " REAL," +
                    COLUMN_NAME_Z + " REAL)";
    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void InsertData(long time, float x, float y, float z) {

    }

}
