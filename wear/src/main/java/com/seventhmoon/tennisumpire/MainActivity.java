package com.seventhmoon.tennisumpire;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;


public class MainActivity extends WearableActivity {
    private static final String TAG = MainActivity.class.getName();
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
        }

        Intent intent = new Intent(MainActivity.this, SetupMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        /*setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        //mTextView = (TextView) findViewById(R.id.text);
        mClockView = (TextView) findViewById(R.id.clock);*/
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
