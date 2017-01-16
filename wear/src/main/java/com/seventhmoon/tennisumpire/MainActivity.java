package com.seventhmoon.tennisumpire;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

import static com.seventhmoon.tennisumpire.Data.InitData.mAccelerometer;
import static com.seventhmoon.tennisumpire.Data.InitData.mGravity;
import static com.seventhmoon.tennisumpire.Data.InitData.mGyroscope;
import static com.seventhmoon.tennisumpire.Data.InitData.mGyroscope_uncalibrated;
import static com.seventhmoon.tennisumpire.Data.InitData.mLinearAcceration;
import static com.seventhmoon.tennisumpire.Data.InitData.mRotationVector;
import static com.seventhmoon.tennisumpire.Data.InitData.mSensorManager;
import static com.seventhmoon.tennisumpire.Data.InitData.mStepCounter;


public class MainActivity extends WearableActivity {
    private static final String TAG = MainActivity.class.getName();

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    //private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
    //        new SimpleDateFormat("HH:mm", Locale.TAIWAN);

    //private BoxInsetLayout mContainerView;
    //private TextView mTextView;
    //private TextView mClockView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "Device does not support Bluetooth");
        } else {
            if (mBluetoothAdapter.isEnabled()) {

                Log.d(TAG, "Bluetooth is enabled");
            } else {


            }
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mAccelerometer != null) {
            Log.e(TAG, "Has mAccelerometer sensor!");
        }

        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (mGravity != null) {
            Log.e(TAG, "Has gravity sensor!");
        }

        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (mGyroscope != null) {
            Log.e(TAG, "Has gyroscope sensor!");
        }

        mGyroscope_uncalibrated = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        if (mGyroscope_uncalibrated != null) {
            Log.e(TAG, "Has gyroscope uncalibrate sensor!");
        }

        mLinearAcceration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (mLinearAcceration != null) {
            Log.e(TAG, "Has linear acceleration sensor!");
        }

        mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (mRotationVector != null) {
            Log.e(TAG, "Has rotation vector sensor!");
        }

        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (mStepCounter != null) {
            Log.e(TAG, "Has step counter sensor!");
        }

        Intent intent = new Intent(MainActivity.this, SetupMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();



    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        //updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        //updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        //updateDisplay();
        super.onExitAmbient();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");


        super.onDestroy();

    }




}
