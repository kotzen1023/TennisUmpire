package com.seventhmoon.tennisumpire.Data;


import android.bluetooth.BluetoothAdapter;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.seventhmoon.tennisumpire.Bluetooth.BluetoothService;

public class InitData {
    public static SensorManager mSensorManager;
    public static Sensor mAccelerometer;
    public static Sensor mGravity;
    public static Sensor mGyroscope;
    public static Sensor mGyroscope_uncalibrated;
    public static Sensor mLinearAcceration;
    public static Sensor mRotationVector;
    public static Sensor mStepCounter;
    public static boolean is_running;
    public static SensorEventListener accelerometerListener;
    public static SensorEventListener rotationVectorListener;

    //bluetooth
    /**
     * Name of the connected device
     */
    public static String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    //private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    public static StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    public static BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    public static BluetoothService mChatService = null;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
}
