package com.example.avi12.test2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.wearable.activity.WearableActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import static java.lang.System.*;


public class MainActivity extends WearableActivity implements SensorEventListener, CompoundButton.OnCheckedChangeListener {
    private SensorManager mSensorManager;
    private Sensor mAcc;
    TextView mTextViewx,mTextViewy,mTextViewz,mTime;
    Switch simpleSwitch;
    Boolean switchState;
    SQLiteDBHelper openHelper;
    SQLiteDatabase db;
    int flg = 0;
    long time,x = 0;
    float ax, ay, az;//stores acceleration along x, y and z axes

    //Creating and opening a database that stores accelerometer values along with timestamp

    public class SQLiteDBHelper extends SQLiteOpenHelper {
        public static final String TABLE_NAME = "Accelerometer_Data";
        public static final String COLUMN_NAME_TIME = "Time";
        public static final String COLUMN_NAME_X = "XAxis";
        public static final String COLUMN_NAME_Y = "YAxis";
        public static final String COLUMN_NAME_Z = "ZAxis";
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "acc.db";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_TIME + " BIGINT PRIMARY KEY," +
                        COLUMN_NAME_X + " REAL," +
                        COLUMN_NAME_Y + " REAL," +
                        COLUMN_NAME_Z + " REAL)";

        public SQLiteDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }

        //function that inserts data into database

        public void insertData(SQLiteDatabase db, long time, float x, float y, float z) {
            ContentValues values = new ContentValues();
            values.put(SQLiteDBHelper.COLUMN_NAME_TIME, time);
            values.put(SQLiteDBHelper.COLUMN_NAME_X, x);
            values.put(SQLiteDBHelper.COLUMN_NAME_Y, y);
            values.put(SQLiteDBHelper.COLUMN_NAME_Z, z);
            db.insert(TABLE_NAME, null, values);
        }

    }

    //Runnable which will enter accelerometer data into the database on a worker thread

    public class DataInsertionRunnable implements Runnable {

        private long time;
        private float x, y, z;

        public DataInsertionRunnable(long time, float x, float y, float z) {
            this.time = time;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public void run() {
            openHelper.insertData(db,time,x,y,z);
        }
    }

    Handler dataInsertionHandler;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);//stetho integration
        setContentView(R.layout.activity_main);
        mTextViewx = (TextView) findViewById(R.id.sensor_data_x);
        mTextViewy = (TextView) findViewById(R.id.sensor_data_y);
        mTextViewz = (TextView) findViewById(R.id.sensor_data_z);
        mTime = (TextView) findViewById(R.id.time_now);
        simpleSwitch = (Switch) findViewById(R.id.data_switch);
        simpleSwitch.setOnCheckedChangeListener(this);
        switchState = simpleSwitch.isChecked();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        openHelper = new SQLiteDBHelper(this);
        db = openHelper.getWritableDatabase();
        setAmbientEnabled();
        HandlerThread ht = new HandlerThread("DataInsertHT");
        ht.start();
        dataInsertionHandler = new Handler(ht.getLooper());
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(this, "Data Collection is " + (isChecked ? "on" : "off"),
                Toast.LENGTH_SHORT).show();
        switchState = isChecked;
    }


    @Override
    public final void onSensorChanged(SensorEvent event) {
        ax = 0;
        ay = 0;
        az = 0;
        time= currentTimeMillis();
        if(time > x) {
            if (switchState) {
                ax = event.values[0];
                ay = event.values[1];
                az = event.values[2];
                dataInsertionHandler.post(new DataInsertionRunnable(time, ax, ay, az));
                flg = 1;
            } else {
                ax = 0;
                ay = 0;
                az = 0;
                if (flg == 1) {
                    dataInsertionHandler.post(new DataInsertionRunnable(time, ax, ay, az));
                    flg = 0;
                }
            }
            mTextViewx.setText(Float.toString(ax));
            mTextViewy.setText(Float.toString(ay));
            mTextViewz.setText(Float.toString(az));
            mTime.setText(Long.toString(time));
            x = time + 10;
        }
    }

    @Override
    public void onAccuracyChanged (Sensor mAcc, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}


