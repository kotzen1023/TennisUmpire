package com.seventhmoon.tennisumpire.Data;

import android.bluetooth.BluetoothAdapter;

import com.seventhmoon.tennisumpire.Bluetooth.BluetoothService;

public class InitData {
    public static boolean mode;
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
}
