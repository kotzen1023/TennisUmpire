package com.seventhmoon.tennisumpire;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Bundle;

import android.os.Handler;

import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.seventhmoon.tennisumpire.Bluetooth.BluetoothService;
import com.seventhmoon.tennisumpire.Data.InitData;
import com.seventhmoon.tennisumpire.Data.State;
import com.seventhmoon.tennisumpire.Data.StateAction;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.TimeZone;

import static com.seventhmoon.tennisumpire.Bluetooth.Data.Constants.DEVICE_NAME;
import static com.seventhmoon.tennisumpire.Bluetooth.Data.Constants.MESSAGE_DEVICE_NAME;
import static com.seventhmoon.tennisumpire.Bluetooth.Data.Constants.MESSAGE_READ;
import static com.seventhmoon.tennisumpire.Bluetooth.Data.Constants.MESSAGE_STATE_CHANGE;
import static com.seventhmoon.tennisumpire.Bluetooth.Data.Constants.MESSAGE_TOAST;
import static com.seventhmoon.tennisumpire.Bluetooth.Data.Constants.MESSAGE_WRITE;
import static com.seventhmoon.tennisumpire.Bluetooth.Data.Constants.TOAST;
import static com.seventhmoon.tennisumpire.Data.FileOperation.append_record;
import static com.seventhmoon.tennisumpire.Data.FileOperation.check_file_exist;
import static com.seventhmoon.tennisumpire.Data.FileOperation.clear_record;
import static com.seventhmoon.tennisumpire.Data.FileOperation.read_record;
import static com.seventhmoon.tennisumpire.Data.InitData.mBluetoothAdapter;
import static com.seventhmoon.tennisumpire.Data.InitData.mChatService;
import static com.seventhmoon.tennisumpire.Data.InitData.mConnectedDeviceName;
import static com.seventhmoon.tennisumpire.Data.InitData.mOutStringBuffer;
import static com.seventhmoon.tennisumpire.Data.StateAction.OPPT_SCORE;
import static com.seventhmoon.tennisumpire.Data.StateAction.OPPT_SERVE;
import static com.seventhmoon.tennisumpire.Data.StateAction.YOU_SCORE;
import static com.seventhmoon.tennisumpire.Data.StateAction.YOU_SERVE;
import static java.lang.Math.sqrt;


public class GameActivity extends AppCompatActivity{
    private static final String TAG = GameActivity.class.getName();

    //private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
    //        new SimpleDateFormat("HH:mm", Locale.TAIWAN);

    //private BoxInsetLayout mContainerView;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private TextView gameUp;
    private TextView gameDown;
    private TextView pointUp;
    private TextView pointDown;
    private ImageView imgServeUp;
    private ImageView imgServeDown;
    private LinearLayout setLayout;
    //private LinearLayout nameLayout;
    private TextView setUp;
    private TextView setDown;
    private ImageView imgWinCheckUp;
    private ImageView imgWinCheckDown;

    private ImageView imgPlayOrPause;

    //private TextView mClockView;


    private TextView textCurrentTime;
    private TextView textGameTime;

    private static String set;
    //private static String game;
    private static String tiebreak;
    private static String deuce;
    private static String serve;
    //private static String duration;

    private static String filename;
    private static String playerUp;
    private static String playerDown;

    //private static long startTime;
    private static Handler handler;
    private static long time_use = 0;

    public static Deque<State> stack = new ArrayDeque<>();

    //public static File RootDirectory = new File("/");

    private static boolean is_pause = false;

    ProgressDialog loadDialog = null;

    //for state
    //private static boolean is_ace = false;
    //private static boolean is_double_fault = false;
    private static boolean is_second_serve = false;
    private static boolean is_break_point = false;
    //private static boolean is_unforced_error = false;
    //private static boolean is_forehand_winner = false;
    //private static boolean is_backhand_winner = false;
    //private static boolean is_forehand_volley = false;
    //private static boolean is_backhand_volley = false;
    private static byte ace_count = 0;
    private static byte double_faults_count = 0;
    private static short unforced_errors_count = 0;
    private static short forehand_winner_count = 0;
    private static short backhand_winner_count = 0;
    private static short forehand_volley_count = 0;
    private static short backhand_volley_count = 0;
    private static byte foul_to_lose_count = 0;
    private static short first_serve_count = 0;
    private static short first_serve_miss = 0;
    private static short second_serve_count = 0;

    private static byte first_serve_won = 0;
    private static byte first_serve_lost = 0;
    private static byte second_serve_won = 0;
    private static byte second_serve_lost = 0;

    private MenuItem item_bluetooth;


    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGravity;
    private Sensor mGyroscope;
    private Sensor mGyroscope_uncalibrated;
    private Sensor mLinearAcceration;
    private Sensor mRotationVector;
    private Sensor mStepCounter;


    private SensorEventListener accelerometerListener;

    private static double PI = 3.1415926535897932384626433832795;
    private static double gravity = 9806.65;

    private static long previous_time = 0;
    private static long current_time = 0;
    private static double previous_accel = 0.0;
    private static double current_accel = 0.0;

    //private static double x_coordinate = 0.0;
    //private static double y_coordinate = 0.0;
    //private static double z_coordinate = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
        Log.d(TAG, "onCreate");

        //mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter.isEnabled()) {
            if (mChatService == null) {
                Log.d(TAG, "mChatService = null");
                setupChat();
            }
        }

        //sensor


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



        accelerometerListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                current_time = System.currentTimeMillis();
                current_accel = sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2]);
                //Log.d(TAG, "accel = "+accel+" sec = "+(current_time-previous_time));
                //Log.d(TAG, "System.currentTimeMillis() = "+System.currentTimeMillis());
                //Log.d(TAG, "X: " + String.valueOf(event.values[0]));
                //Log.d(TAG, "Y: " + String.valueOf(event.values[1]));
                //Log.d(TAG, "Z: " + String.valueOf(event.values[2]));
                double time = (((double)(current_time-previous_time))/1000);

                //x_coordinate = x_coordinate + (event.values[0] * time * time)*100;
                //y_coordinate = x_coordinate + (event.values[1] * time * time)*100;
                //z_coordinate = z_coordinate + (event.values[2] * time * time)*100;



                //double distance = Accel2mms(accel, time);
                //if (distance > 0.1)
                double accel_diff;
                if (current_accel > previous_accel)
                    accel_diff = current_accel - previous_accel;
                else
                    accel_diff = previous_accel - current_accel;

                double distance = accel_diff * time * time;

                if (accel_diff > 0.2)
                   Log.d(TAG, "accel_diff = "+accel_diff+" sec = "+time+" distance = "+distance+" move = "+distance/accel_diff);
                //Log.d(TAG, "x = "+x_coordinate+", y = "+y_coordinate+", z = "+z_coordinate);
                previous_time = current_time;
                previous_accel = current_accel;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {


            }
        };

        mSensorManager.registerListener(accelerometerListener, mLinearAcceration, SensorManager.SENSOR_DELAY_NORMAL);

        //for action bar
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ball_icon);
        }
        //is_ace = false;
        //is_double_fault = false;
        is_second_serve = false;
        is_break_point = false;
        //is_unforced_error = false;
        //is_forehand_winner = false;
        //is_backhand_winner = false;
        //is_forehand_volley = false;
        //is_backhand_volley = false;
        first_serve_count = 0;
        first_serve_miss = 0;
        second_serve_count = 0;

        first_serve_won = 0;
        first_serve_lost = 0;
        second_serve_won = 0;
        second_serve_lost = 0;

        Button btnYouScore;
        Button btnBack;
        Button btnOpptScore;
        Button btnReset;
        Button btnSave;
        Button btnLoad;

        LinearLayout nameLayout;

        handler = new Handler();

        //startTime = System.currentTimeMillis();

        handler.removeCallbacks(updateTimer);
        handler.postDelayed(updateTimer, 1000);

        Intent intent = getIntent();

        set = intent.getStringExtra("SETUP_SET");
        tiebreak = intent.getStringExtra("SETUP_TIEBREAK");
        deuce = intent.getStringExtra("SETUP_DEUCE");
        serve = intent.getStringExtra("SETUP_SERVE");

        filename = intent.getStringExtra("FILE_NAME");
        playerUp = intent.getStringExtra("PLAYER_UP");
        playerDown = intent.getStringExtra("PLAYER_DOWN");
        //duration = intent.getStringExtra("GAME_DURATION");

        Log.e(TAG, "SET = "+set);
        //Log.e(TAG, "GAME = "+game);
        Log.e(TAG, "TIEBREAK = "+tiebreak);
        Log.e(TAG, "DEUCE = "+deuce);
        Log.e(TAG, "SERVE = "+serve);

        Log.e(TAG, "filename = "+filename);
        Log.e(TAG, "playerUp = "+playerUp);
        Log.e(TAG, "playerDown = "+playerDown);

        //mContainerView = (BoxInsetLayout) findViewById(R.id.container);

        gameUp = (TextView) findViewById(R.id.textViewGameUp);
        gameDown = (TextView) findViewById(R.id.textViewGameDown);
        pointUp = (TextView) findViewById(R.id.textViewPointUp);
        pointDown = (TextView) findViewById(R.id.textViewPointDown);

        imgServeUp = (ImageView) findViewById(R.id.imageViewServeUp);
        imgServeDown = (ImageView) findViewById(R.id.imageViewServeDown);

        textCurrentTime = (TextView) findViewById(R.id.currentTime);
        textGameTime = (TextView) findViewById(R.id.gameTime);

        setLayout = (LinearLayout) findViewById(R.id.setLayout);
        nameLayout = (LinearLayout) findViewById(R.id.nameLayout);
        setUp = (TextView) findViewById(R.id.textViewSetUp);
        setDown = (TextView) findViewById(R.id.textViewSetDown);

        imgWinCheckUp = (ImageView) findViewById(R.id.imageWincheckUp);
        imgWinCheckDown = (ImageView) findViewById(R.id.imageWincheckDown);

        imgPlayOrPause = (ImageView) findViewById(R.id.imageViewPlayOrPause);

        //mConversationView = (ListView) findViewById(R.id.);


        //init score board
        gameUp.setText("0");
        gameDown.setText("0");
        pointUp.setText("0");
        pointDown.setText("0");

        if (serve != null) {
            if (serve.equals("0")) { //you serve first
                imgServeUp.setVisibility(View.INVISIBLE);
                imgServeDown.setVisibility(View.VISIBLE);
            } else {
                imgServeUp.setVisibility(View.VISIBLE);
                imgServeDown.setVisibility(View.INVISIBLE);
            }
        } else {
            serve = "0";
            imgServeUp.setVisibility(View.INVISIBLE);
            imgServeDown.setVisibility(View.VISIBLE);
        }

        if (playerUp != null && playerDown != null) {
            if (playerUp.equals(""))
                playerUp = "Player1";
            if (playerDown.equals(""))
                playerDown = "Player2";
            nameLayout.setVisibility(View.VISIBLE);
        } else {
            if (playerUp == null)
                playerUp = "Player1";
            if (playerDown == null)
                playerDown = "Player2";
            nameLayout.setVisibility(View.VISIBLE);
        }

        //load file to stack

        stack.clear();
        if (check_file_exist(filename)) {
            Log.d(TAG, "load file success!");
            loadDialog = new ProgressDialog(this);
            loadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadDialog.setTitle("Loading...");
            loadDialog.setIndeterminate(false);
            loadDialog.setCancelable(false);

            loadDialog.show();

            String message = read_record(filename);
            Log.d(TAG, "message = "+ message);
            String msg[] = message.split("\\|");

            Log.d(TAG, "msg[0] = "+ msg[0]);

            String info[] = msg[0].split(";");

            if (info.length > 1) {

                playerUp = info[0];
                playerDown = info[1];

                if (playerUp != null && playerDown != null) {
                    if (!playerUp.equals("") && !playerDown.equals(""))
                        nameLayout.setVisibility(View.VISIBLE);
                    else
                        nameLayout.setVisibility(View.GONE);
                }

                if (Boolean.valueOf(info[2])) { //tiebreak
                    tiebreak = "0";
                } else {
                    tiebreak = "1";
                }

                if (Boolean.valueOf(info[3])) { //deuce
                    deuce = "0";
                } else {
                    deuce = "1";
                }

                if (Boolean.valueOf(info[4])) { //first serve
                    serve = "0";
                    imgServeUp.setVisibility(View.INVISIBLE);
                    imgServeDown.setVisibility(View.VISIBLE);
                } else {
                    serve = "1";
                    imgServeUp.setVisibility(View.VISIBLE);
                    imgServeDown.setVisibility(View.INVISIBLE);
                }

                //set
                set = info[5];
            } else {
                playerUp = "Player1";
                playerDown = "Player2";
                tiebreak = "0";
                deuce = "0";
                serve = "0";
                set = "0";


            }

            if (msg.length > 1) {

                String stat[] = msg[1].split("&");


                for (String s : stat) {
                    String data[] = s.split(";");
                    State new_state = new State();

                    new_state.setCurrent_set(Byte.valueOf(data[0]));
                    new_state.setServe(Boolean.valueOf(data[1]));
                    new_state.setInTiebreak(Boolean.valueOf(data[2]));
                    new_state.setFinish(Boolean.valueOf(data[3]));
                    new_state.setSecondServe(Boolean.valueOf(data[4]));
                    new_state.setInBreakPoint(Boolean.valueOf(data[5]));
                    new_state.setSetsUp(Byte.valueOf(data[6]));
                    new_state.setSetsDown(Byte.valueOf(data[7]));
                    new_state.setDuration(Long.valueOf(data[8]));

                    //ace
                    new_state.setAceCountUp(Byte.valueOf(data[9]));
                    new_state.setAceCountDown(Byte.valueOf(data[10]));
                    //first serve
                    new_state.setFirstServeUp(Short.valueOf(data[11]));
                    new_state.setFirstServeDown(Short.valueOf(data[12]));
                    //first serve miss
                    new_state.setFirstServeMissUp(Short.valueOf(data[13]));
                    new_state.setFirstServeMissDown(Short.valueOf(data[14]));
                    //second serve
                    new_state.setSecondServeUp(Short.valueOf(data[15]));
                    new_state.setSecondServeDown(Short.valueOf(data[16]));
                    //break point
                    new_state.setBreakPointUp(Byte.valueOf(data[17]));
                    new_state.setBreakPointDown(Byte.valueOf(data[18]));
                    //break point miss
                    new_state.setBreakPointMissUp(Byte.valueOf(data[19]));
                    new_state.setBreakPointMissDown(Byte.valueOf(data[20]));
                    //first serve won
                    new_state.setFirstServeWonUp(Short.valueOf(data[21]));
                    new_state.setFirstServeWonDown(Short.valueOf(data[22]));
                    //first serve lost
                    new_state.setFirstServeLostUp(Short.valueOf(data[23]));
                    new_state.setFirstServeLostDown(Short.valueOf(data[24]));
                    //second serve won
                    new_state.setSecondServeWonUp(Short.valueOf(data[25]));
                    new_state.setSecondServeWonDown(Short.valueOf(data[26]));
                    //second serve lost
                    new_state.setSecondServeLostUp(Short.valueOf(data[27]));
                    new_state.setSecondServeLostDown(Short.valueOf(data[28]));
                    //double faults
                    new_state.setDoubleFaultUp(Byte.valueOf(data[29]));
                    new_state.setDoubleFaultDown(Byte.valueOf(data[30]));
                    //unforced error
                    new_state.setUnforceErrorUp(Byte.valueOf(data[31]));
                    new_state.setUnforceErrorDown(Byte.valueOf(data[32]));
                    //forehand winner
                    new_state.setForehandWinnerUp(Byte.valueOf(data[33]));
                    new_state.setForehandWinnerDown(Byte.valueOf(data[34]));
                    //backhand winner
                    new_state.setBackhandWinnerUp(Byte.valueOf(data[35]));
                    new_state.setBackhandWinnerDown(Byte.valueOf(data[36]));
                    //forehand volley
                    new_state.setForehandVolleyUp(Byte.valueOf(data[37]));
                    new_state.setForehandVolleyDown(Byte.valueOf(data[38]));
                    new_state.setBackhandVolleyUp(Byte.valueOf(data[39]));
                    new_state.setBackhandVolleyDown(Byte.valueOf(data[40]));
                    //foul to lose
                    new_state.setFoulToLoseUp(Byte.valueOf(data[41]));
                    new_state.setFoulToLoseDown(Byte.valueOf(data[42]));

                    new_state.setSet_game_up((byte) 0x1, Byte.valueOf(data[43]));
                    new_state.setSet_game_down((byte) 0x1, Byte.valueOf(data[44]));
                    new_state.setSet_point_up((byte) 0x1, Byte.valueOf(data[45]));
                    new_state.setSet_point_down((byte) 0x1, Byte.valueOf(data[46]));
                    new_state.setSet_tiebreak_point_up((byte) 0x1, Byte.valueOf(data[47]));
                    new_state.setSet_tiebreak_point_down((byte) 0x1, Byte.valueOf(data[48]));

                    new_state.setSet_game_up((byte) 0x2, Byte.valueOf(data[49]));
                    new_state.setSet_game_down((byte) 0x2, Byte.valueOf(data[50]));
                    new_state.setSet_point_up((byte) 0x2, Byte.valueOf(data[51]));
                    new_state.setSet_point_down((byte) 0x2, Byte.valueOf(data[52]));
                    new_state.setSet_tiebreak_point_up((byte) 0x2, Byte.valueOf(data[53]));
                    new_state.setSet_tiebreak_point_down((byte) 0x2, Byte.valueOf(data[54]));

                    new_state.setSet_game_up((byte) 0x3, Byte.valueOf(data[55]));
                    new_state.setSet_game_down((byte) 0x3, Byte.valueOf(data[56]));
                    new_state.setSet_point_up((byte) 0x3, Byte.valueOf(data[57]));
                    new_state.setSet_point_down((byte) 0x3, Byte.valueOf(data[58]));
                    new_state.setSet_tiebreak_point_up((byte) 0x3, Byte.valueOf(data[59]));
                    new_state.setSet_tiebreak_point_down((byte) 0x3, Byte.valueOf(data[60]));

                    new_state.setSet_game_up((byte) 0x4, Byte.valueOf(data[61]));
                    new_state.setSet_game_down((byte) 0x4, Byte.valueOf(data[62]));
                    new_state.setSet_point_up((byte) 0x4, Byte.valueOf(data[63]));
                    new_state.setSet_point_down((byte) 0x4, Byte.valueOf(data[64]));
                    new_state.setSet_tiebreak_point_up((byte) 0x4, Byte.valueOf(data[65]));
                    new_state.setSet_tiebreak_point_down((byte) 0x4, Byte.valueOf(data[66]));

                    new_state.setSet_game_up((byte) 0x5, Byte.valueOf(data[67]));
                    new_state.setSet_game_down((byte) 0x5, Byte.valueOf(data[68]));
                    new_state.setSet_point_up((byte) 0x5, Byte.valueOf(data[69]));
                    new_state.setSet_point_down((byte) 0x5, Byte.valueOf(data[70]));
                    new_state.setSet_tiebreak_point_up((byte) 0x5, Byte.valueOf(data[71]));
                    new_state.setSet_tiebreak_point_down((byte) 0x5, Byte.valueOf(data[72]));

                    stack.addLast(new_state);
                }

                /*for (int i = 0; i < stat.length; i++) {
                    String data[] = stat[i].split(";");
                    State new_state = new State();
                    new_state.setCurrent_set(Byte.valueOf(data[0]));
                    new_state.setServe(Boolean.valueOf(data[1]));
                    new_state.setInTiebreak(Boolean.valueOf(data[2]));
                    new_state.setFinish(Boolean.valueOf(data[3]));
                    //new_state.setDeuce(Boolean.valueOf(data[4]));
                    //new_state.setFirstServe(Boolean.valueOf(data[5]));
                    //new_state.setSetLimit(Byte.valueOf(data[6]));
                    new_state.setSetsUp(Byte.valueOf(data[4]));
                    new_state.setSetsDown(Byte.valueOf(data[5]));
                    new_state.setDuration(Long.valueOf(data[6]));

                    new_state.setSet_game_up((byte) 0x1, Byte.valueOf(data[7]));
                    new_state.setSet_game_down((byte) 0x1, Byte.valueOf(data[8]));
                    new_state.setSet_point_up((byte) 0x1, Byte.valueOf(data[9]));
                    new_state.setSet_point_down((byte) 0x1, Byte.valueOf(data[10]));
                    new_state.setSet_tiebreak_point_up((byte) 0x1, Byte.valueOf(data[11]));
                    new_state.setSet_tiebreak_point_down((byte) 0x1, Byte.valueOf(data[12]));

                    new_state.setSet_game_up((byte) 0x2, Byte.valueOf(data[13]));
                    new_state.setSet_game_down((byte) 0x2, Byte.valueOf(data[14]));
                    new_state.setSet_point_up((byte) 0x2, Byte.valueOf(data[15]));
                    new_state.setSet_point_down((byte) 0x2, Byte.valueOf(data[16]));
                    new_state.setSet_tiebreak_point_up((byte) 0x2, Byte.valueOf(data[17]));
                    new_state.setSet_tiebreak_point_down((byte) 0x2, Byte.valueOf(data[18]));

                    new_state.setSet_game_up((byte) 0x3, Byte.valueOf(data[19]));
                    new_state.setSet_game_down((byte) 0x3, Byte.valueOf(data[20]));
                    new_state.setSet_point_up((byte) 0x3, Byte.valueOf(data[21]));
                    new_state.setSet_point_down((byte) 0x3, Byte.valueOf(data[22]));
                    new_state.setSet_tiebreak_point_up((byte) 0x3, Byte.valueOf(data[23]));
                    new_state.setSet_tiebreak_point_down((byte) 0x3, Byte.valueOf(data[24]));

                    new_state.setSet_game_up((byte) 0x4, Byte.valueOf(data[25]));
                    new_state.setSet_game_down((byte) 0x4, Byte.valueOf(data[26]));
                    new_state.setSet_point_up((byte) 0x4, Byte.valueOf(data[27]));
                    new_state.setSet_point_down((byte) 0x4, Byte.valueOf(data[28]));
                    new_state.setSet_tiebreak_point_up((byte) 0x4, Byte.valueOf(data[29]));
                    new_state.setSet_tiebreak_point_down((byte) 0x4, Byte.valueOf(data[30]));

                    new_state.setSet_game_up((byte) 0x5, Byte.valueOf(data[31]));
                    new_state.setSet_game_down((byte) 0x5, Byte.valueOf(data[32]));
                    new_state.setSet_point_up((byte) 0x5, Byte.valueOf(data[33]));
                    new_state.setSet_point_down((byte) 0x5, Byte.valueOf(data[34]));
                    new_state.setSet_tiebreak_point_up((byte) 0x5, Byte.valueOf(data[35]));
                    new_state.setSet_tiebreak_point_down((byte) 0x5, Byte.valueOf(data[36]));

                    stack.addLast(new_state);

                }*/

                //get top

                //State top = new State();

                State top = stack.peek();
                if (top != null) {
                    byte current_set = top.getCurrent_set();


                    if (top.getSetsUp() > 0 || top.getSetsDown() > 0) {
                        setLayout.setVisibility(View.VISIBLE);
                        setUp.setText(String.valueOf(top.getSetsUp()));
                        setDown.setText(String.valueOf(top.getSetsDown()));
                    } else {
                        setLayout.setVisibility(View.GONE);
                        setUp.setText("0");
                        setDown.setText("0");
                    }

                    gameUp.setText(String.valueOf(top.getSet_game_up(current_set)));
                    gameDown.setText(String.valueOf(top.getSet_game_down(current_set)));

                    if (top.isFinish()) {
                        imgServeUp.setVisibility(View.INVISIBLE);
                        imgServeDown.setVisibility(View.INVISIBLE);

                        if (top.getSetsUp() > top.getSetsDown()) {
                            imgWinCheckUp.setVisibility(View.VISIBLE);
                            imgWinCheckDown.setVisibility(View.GONE);
                        } else {
                            imgWinCheckUp.setVisibility(View.GONE);
                            imgWinCheckDown.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (top.isServe()) {
                            imgServeUp.setVisibility(View.INVISIBLE);
                            imgServeDown.setVisibility(View.VISIBLE);
                        } else {
                            imgServeUp.setVisibility(View.VISIBLE);
                            imgServeDown.setVisibility(View.INVISIBLE);
                        }
                    }

                    if (top.isSecondServe()) {
                        is_second_serve = true;
                        imgServeUp.setImageResource(R.drawable.ball_icon_red);
                        imgServeDown.setImageResource(R.drawable.ball_icon_red);
                    }
                    else {
                        is_second_serve = false;
                        imgServeUp.setImageResource(R.drawable.ball_icon);
                        imgServeDown.setImageResource(R.drawable.ball_icon);
                    }

                    if (top.isInBreakPoint()) {
                        is_break_point = true;
                    } else {
                        is_break_point = false;
                    }


                    if (!top.isInTiebreak()) { //not in tiebreak
                        if (top.getSet_point_up(current_set) == 1) {
                            pointUp.setText(String.valueOf(15));
                        } else if (top.getSet_point_up(current_set) == 2) {
                            pointUp.setText(String.valueOf(30));
                        } else if (top.getSet_point_up(current_set) == 3) {
                            pointUp.setText(String.valueOf(40));
                        } else if (top.getSet_point_up(current_set) == 4) {
                            String score = String.valueOf(40) + "A";
                            pointUp.setText(score);
                        } else {
                            pointUp.setText("0");
                        }
                    } else { //tie break;
                        pointUp.setText(String.valueOf(top.getSet_point_up(current_set)));
                    }

                    if (!top.isInTiebreak()) { //not in tiebreak
                        if (top.getSet_point_down(current_set) == 1) {
                            pointDown.setText(String.valueOf(15));
                        } else if (top.getSet_point_down(current_set) == 2) {
                            pointDown.setText(String.valueOf(30));
                        } else if (top.getSet_point_down(current_set) == 3) {
                            pointDown.setText(String.valueOf(40));
                        } else if (top.getSet_point_down(current_set) == 4) {
                            String score = String.valueOf(40) + "A";
                            pointDown.setText(score);
                        } else {
                            pointDown.setText("0");
                        }
                    } else {
                        pointDown.setText(String.valueOf(top.getSet_point_down(current_set)));
                    }

                    //get back duration
                    time_use = top.getDuration();
                    if (top.isFinish()) {
                        handler.removeCallbacks(updateTimer);
                        imgServeUp.setVisibility(View.INVISIBLE);
                        imgServeDown.setVisibility(View.INVISIBLE);

                        is_pause = false;
                        imgPlayOrPause.setVisibility(View.GONE);
                    }

                    Log.d(TAG, "########## top state start ##########");
                    Log.d(TAG, "current set : " + top.getCurrent_set());
                    Log.d(TAG, "Serve : " + top.isServe());
                    Log.d(TAG, "In tiebreak : " + top.isInTiebreak());
                    Log.d(TAG, "Finish : " + top.isFinish());
                    Log.d(TAG, "second serve : " + top.isSecondServe());
                    Log.d(TAG, "In break point : "+ top.isInBreakPoint());
                    //Log.d(TAG, "deuce : " + top.isDeuce());
                    //Log.d(TAG, "First serve : "+ top.isFirstServe());
                    Log.d(TAG, "duration : " + top.getDuration());

                    int set_limit;
                    switch (set) {
                        case "0":
                            set_limit = 1;
                            break;
                        case "1":
                            set_limit = 3;
                            break;
                        case "2":
                            set_limit = 5;
                            break;
                        default:
                            set_limit = 1;
                            break;
                    }


                    for (int i = 1; i <= set_limit; i++) {
                        Log.d(TAG, "================================");
                        Log.d(TAG, "[set " + i + "]");
                        Log.d(TAG, "[Game : " + top.getSet_game_up((byte) i) + " / " + top.getSet_game_down((byte) i) + "]");
                        Log.d(TAG, "[Point : " + top.getSet_point_up((byte) i) + " / " + top.getSet_point_down((byte) i) + "]");
                        Log.d(TAG, "[tiebreak : " + top.getSet_tiebreak_point_up((byte) i) + " / " + top.getSet_tiebreak_point_down((byte) i) + "]");
                    }

                    Log.d(TAG, "########## top state end ##########");

                } else {
                    gameUp.setText("0");
                    gameDown.setText("0");

                    imgServeUp.setVisibility(View.INVISIBLE);
                    imgServeDown.setVisibility(View.INVISIBLE);

                    pointUp.setText("0");
                    pointDown.setText("0");

                    if (serve.equals("0")) { //you server first
                        imgServeUp.setVisibility(View.INVISIBLE);
                        imgServeDown.setVisibility(View.VISIBLE);
                    } else {
                        imgServeUp.setVisibility(View.VISIBLE);
                        imgServeDown.setVisibility(View.INVISIBLE);
                    }
                }
            }

            loadDialog.dismiss();
        }


        //mClockView = (TextView) findViewById(R.id.clock);

        btnYouScore = (Button) findViewById(R.id.btnYouScore);
        btnOpptScore = (Button) findViewById(R.id.btnOpptScore);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnReset = (Button) findViewById(R.id.btnReset);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnLoad = (Button) findViewById(R.id.btnLoad);

        TextView textViewPlayerUp = (TextView) findViewById(R.id.textViewPlayerUp);
        final TextView textViewPlayerDown = (TextView) findViewById(R.id.textViewPlayerDown);

        textViewPlayerUp.setText(playerUp);
        textViewPlayerDown.setText(playerDown);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imgWinCheckUp.setVisibility(View.GONE);
                imgWinCheckDown.setVisibility(View.GONE);

                is_pause = false;
                imgPlayOrPause.setVisibility(View.VISIBLE);
                imgPlayOrPause.setImageResource(R.drawable.ic_pause_white_48dp);

                if (stack.isEmpty()) {
                    Log.d(TAG, "stack is empty!");

                } else {
                    Log.d(TAG, "stack is not empty, pop top state");
                    byte current_set;
                    //stack.pop();
                    State popState = stack.pop();
                    time_use = popState.getDuration();

                    handler.removeCallbacks(updateTimer);
                    handler.postDelayed(updateTimer, 1000);


                    if (popState != null) { //pop out current
                        State back_state = stack.peek();
                        if (back_state != null) {
                            Log.d(TAG, "back_state not null");
                            current_set = back_state.getCurrent_set();


                            if (back_state.getSetsUp() > 0 || back_state.getSetsDown() > 0) {
                                setLayout.setVisibility(View.VISIBLE);
                                setUp.setText(String.valueOf(back_state.getSetsUp()));
                                setDown.setText(String.valueOf(back_state.getSetsDown()));
                            } else {
                                setLayout.setVisibility(View.GONE);
                                setUp.setText("0");
                                setDown.setText("0");
                            }

                            gameUp.setText(String.valueOf(back_state.getSet_game_up(current_set)));
                            gameDown.setText(String.valueOf(back_state.getSet_game_down(current_set)));

                            if (back_state.isServe()) {
                                imgServeUp.setVisibility(View.INVISIBLE);
                                imgServeDown.setVisibility(View.VISIBLE);
                            } else {
                                imgServeUp.setVisibility(View.VISIBLE);
                                imgServeDown.setVisibility(View.INVISIBLE);
                            }

                            if (back_state.isInBreakPoint()) {
                                is_break_point = true;
                            } else {
                                is_break_point = false;
                            }

                            if (!back_state.isInTiebreak()) { //not in tiebreak
                                if (back_state.getSet_point_up(current_set) == 1) {
                                    pointUp.setText(String.valueOf(15));
                                } else if (back_state.getSet_point_up(current_set) == 2) {
                                    pointUp.setText(String.valueOf(30));
                                } else if (back_state.getSet_point_up(current_set) == 3) {
                                    pointUp.setText(String.valueOf(40));
                                } else if (back_state.getSet_point_up(current_set) == 4) {
                                    String msg = String.valueOf(40)+"A";
                                    pointUp.setText(msg);
                                } else {
                                    pointUp.setText("0");
                                }
                            } else { //tie break;
                                pointUp.setText(String.valueOf(back_state.getSet_point_up(current_set)));
                            }

                            if (!back_state.isInTiebreak()) { //not in tiebreak
                                if (back_state.getSet_point_down(current_set) == 1) {
                                    pointDown.setText(String.valueOf(15));
                                } else if (back_state.getSet_point_down(current_set) == 2) {
                                    pointDown.setText(String.valueOf(30));
                                } else if (back_state.getSet_point_down(current_set) == 3) {
                                    pointDown.setText(String.valueOf(40));
                                } else if (back_state.getSet_point_down(current_set) == 4) {
                                    String msg = String.valueOf(40)+"A";
                                    pointDown.setText(msg);
                                } else {
                                    pointDown.setText("0");
                                }
                            } else {
                                pointDown.setText(String.valueOf(back_state.getSet_point_down(current_set)));
                            }

                            if (back_state.isSecondServe()) {
                                is_second_serve = true;
                                imgServeUp.setImageResource(R.drawable.ball_icon_red);
                                imgServeDown.setImageResource(R.drawable.ball_icon_red);
                            } else {
                                is_second_serve = false;
                                imgServeUp.setImageResource(R.drawable.ball_icon);
                                imgServeDown.setImageResource(R.drawable.ball_icon);
                            }

                            Log.d(TAG, "########## back state start ##########");
                            Log.d(TAG, "current set : " + back_state.getCurrent_set());
                            Log.d(TAG, "Serve : " + back_state.isServe());
                            Log.d(TAG, "In tiebreak : " + back_state.isInTiebreak());
                            Log.d(TAG, "Finish : " + back_state.isFinish());
                            Log.d(TAG, "Second Serve : " + back_state.isSecondServe());
                            Log.d(TAG, "In break point : " + back_state.isInBreakPoint());

                            int set_limit;
                            switch (set)
                            {
                                case "0":
                                    set_limit = 1;
                                    break;
                                case "1":
                                    set_limit = 3;
                                    break;
                                case "2":
                                    set_limit = 5;
                                    break;
                                default:
                                    set_limit = 1;
                                    break;
                            }


                            for (int i = 1; i <= set_limit; i++) {
                                Log.d(TAG, "================================");
                                Log.d(TAG, "[set " + i + "]");
                                Log.d(TAG, "[Game : " + back_state.getSet_game_up((byte) i) + " / " + back_state.getSet_game_down((byte) i) + "]");
                                Log.d(TAG, "[Point : " + back_state.getSet_point_up((byte) i) + " / " + back_state.getSet_point_down((byte) i) + "]");
                                Log.d(TAG, "[tiebreak : " + back_state.getSet_tiebreak_point_up((byte) i) + " / " + back_state.getSet_tiebreak_point_down((byte) i) + "]");
                            }


                            Log.d(TAG, "########## back state end ##########");

                        } else {
                            Log.d(TAG, "back_state is null");

                            gameUp.setText("0");
                            gameDown.setText("0");

                            imgServeUp.setVisibility(View.INVISIBLE);
                            imgServeDown.setVisibility(View.INVISIBLE);

                            pointUp.setText("0");
                            pointDown.setText("0");

                            imgServeUp.setImageResource(R.drawable.ball_icon);
                            imgServeDown.setImageResource(R.drawable.ball_icon);

                            if (serve.equals("0")) { //you serve first
                                imgServeUp.setVisibility(View.INVISIBLE);
                                imgServeDown.setVisibility(View.VISIBLE);
                            } else {
                                imgServeUp.setVisibility(View.VISIBLE);
                                imgServeDown.setVisibility(View.INVISIBLE);
                            }

                            is_second_serve = false;
                        }
                    } else {
                        Log.d(TAG, "popState null");
                    }
                }
            }
        });

        btnYouScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "You score";
                byte[] send = message.getBytes();
                if (mChatService != null) {
                    mChatService.write(send);

                    // Reset out string buffer to zero and clear the edit text field
                    mOutStringBuffer.setLength(0);
                }


                //calculateScore(true);
                if (imgWinCheckUp.getVisibility() == View.VISIBLE || imgWinCheckDown.getVisibility() == View.VISIBLE) {
                    Log.d(TAG, "Game is over!");
                    calculateScore(YOU_SCORE);
                } else {

                    ArrayList<String> items = new ArrayList<>();


                    if (imgServeDown.getVisibility() == View.VISIBLE) { //you serve
                        if (is_second_serve) {
                            items.add(getResources().getString(R.string.game_ace));
                            items.add(getResources().getString(R.string.game_double_faults));
                            items.add(getResources().getString(R.string.game_unforced_error));
                            items.add(getResources().getString(R.string.game_forehand_winner));
                            items.add(getResources().getString(R.string.game_backhand_winner));
                            items.add(getResources().getString(R.string.game_forehand_volley));
                            items.add(getResources().getString(R.string.game_backhand_volley));
                            items.add(getResources().getString(R.string.game_foul_to_lose));
                            items.add(getResources().getString(R.string.game_other_winner));
                            items.add(getResources().getString(R.string.game_serve_net));
                        } else {
                            items.add(getResources().getString(R.string.game_ace));
                            items.add(getResources().getString(R.string.game_second_serve));
                            items.add(getResources().getString(R.string.game_unforced_error));
                            items.add(getResources().getString(R.string.game_forehand_winner));
                            items.add(getResources().getString(R.string.game_backhand_winner));
                            items.add(getResources().getString(R.string.game_forehand_volley));
                            items.add(getResources().getString(R.string.game_backhand_volley));
                            items.add(getResources().getString(R.string.game_foul_to_lose));
                            items.add(getResources().getString(R.string.game_other_winner));
                            items.add(getResources().getString(R.string.game_serve_net));
                        }


                        ArrayAdapter<String> adapter = new ArrayAdapter<>(GameActivity.this, android.R.layout.select_dialog_item, items);

                        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                        builder.setTitle(playerDown + " " + getResources().getString(R.string.game_select_action));
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) { //pick from gamer
                                if (is_second_serve) { //in second serve
                                    second_serve_count = 1;
                                    if (item == 0) { //ace
                                        ace_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 1) { //double fault
                                        //is_double_fault = true;
                                        double_faults_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 2) { //unforced error
                                        unforced_errors_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 3) { //forehand winner
                                        forehand_winner_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 4) { //backhand winner
                                        backhand_winner_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 5) { //forehand volley
                                        forehand_volley_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 6) { //backhand volley
                                        backhand_volley_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 7) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 8) { //other winner
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 9) { //net in
                                        calculateScore(YOU_SERVE);
                                    }
                                } else { //first serve
                                    first_serve_count = 1;
                                    if (item == 0) { //ace
                                        //is_ace = true;
                                        ace_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 1) {
                                        Log.d(TAG, "second serve");
                                        first_serve_miss = 1;
                                        is_second_serve = true;
                                        calculateScore(YOU_SERVE);
                                    } else if (item == 2) { //unforced error
                                        unforced_errors_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 3) { //forehand winner
                                        forehand_winner_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 4) { //backhand winner
                                        backhand_winner_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 5) { //forehand volley
                                        forehand_volley_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 6) { //backhand volley
                                        backhand_volley_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 7) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 8) { //other winner
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 9) { //net in
                                        calculateScore(YOU_SERVE);
                                    }
                                }
                            }
                        });
                        builder.show();
                    } else { //oppt serve
                        items.add(getResources().getString(R.string.game_unforced_error));
                        items.add(getResources().getString(R.string.game_forehand_winner));
                        items.add(getResources().getString(R.string.game_backhand_winner));
                        items.add(getResources().getString(R.string.game_forehand_volley));
                        items.add(getResources().getString(R.string.game_backhand_volley));
                        items.add(getResources().getString(R.string.game_foul_to_lose));
                        items.add(getResources().getString(R.string.game_other_winner));

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(GameActivity.this, android.R.layout.select_dialog_item, items);

                        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                        builder.setTitle(playerDown + " " + getResources().getString(R.string.game_select_action));
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (is_second_serve) {
                                    second_serve_count = 1;
                                    if (item == 0) { //unforced error
                                        unforced_errors_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 1) { //forehand winner
                                        forehand_winner_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 2) { //backhand winner
                                        backhand_winner_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 3) { //forehand volley
                                        forehand_volley_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 4) { //backhand volley
                                        backhand_volley_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 5) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 6) { //other winner
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    }
                                } else { //first serve
                                    first_serve_count = 1;
                                    if (item == 0) { //unforced error
                                        unforced_errors_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 1) { //forehand winner
                                        forehand_winner_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 2) { //backhand winner
                                        backhand_winner_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 3) { //forehand volley
                                        forehand_volley_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 4) { //backhand volley
                                        backhand_volley_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 5) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 6) { //other winner
                                        first_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    }
                                }
                            }
                        });
                        builder.show();
                    }


                }
            }
        });

        btnOpptScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Oppt score";
                byte[] send = message.getBytes();
                if (mChatService != null) {
                    mChatService.write(send);

                    // Reset out string buffer to zero and clear the edit text field
                    mOutStringBuffer.setLength(0);
                }
                //calculateScore(false);
                if (imgWinCheckUp.getVisibility() == View.VISIBLE || imgWinCheckDown.getVisibility() == View.VISIBLE) {
                    Log.d(TAG, "Game is over!");
                    calculateScore(OPPT_SCORE);
                } else {
                    ArrayList<String> items = new ArrayList<>();

                    if (imgServeUp.getVisibility() == View.VISIBLE) { //oppt serve
                        if (is_second_serve) { //second serve
                            items.add(getResources().getString(R.string.game_ace));
                            items.add(getResources().getString(R.string.game_double_faults));
                            items.add(getResources().getString(R.string.game_unforced_error));
                            items.add(getResources().getString(R.string.game_forehand_winner));
                            items.add(getResources().getString(R.string.game_backhand_winner));
                            items.add(getResources().getString(R.string.game_forehand_volley));
                            items.add(getResources().getString(R.string.game_backhand_volley));
                            items.add(getResources().getString(R.string.game_foul_to_lose));
                            items.add(getResources().getString(R.string.game_other_winner));
                            items.add(getResources().getString(R.string.game_serve_net));
                        } else { //first serve
                            items.add(getResources().getString(R.string.game_ace));
                            items.add(getResources().getString(R.string.game_second_serve));
                            items.add(getResources().getString(R.string.game_unforced_error));
                            items.add(getResources().getString(R.string.game_forehand_winner));
                            items.add(getResources().getString(R.string.game_backhand_winner));
                            items.add(getResources().getString(R.string.game_forehand_volley));
                            items.add(getResources().getString(R.string.game_backhand_volley));
                            items.add(getResources().getString(R.string.game_foul_to_lose));
                            items.add(getResources().getString(R.string.game_other_winner));
                            items.add(getResources().getString(R.string.game_serve_net));
                        }


                        ArrayAdapter<String> adapter = new ArrayAdapter<>(GameActivity.this, android.R.layout.select_dialog_item, items);

                        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                        builder.setTitle(playerUp + " " + getResources().getString(R.string.game_select_action));
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) { //pick from gamer

                                if (is_second_serve) { //in second serve
                                    second_serve_count = 1;
                                    if (item == 0) { //ace
                                        ace_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 1) { //double fault
                                        //is_double_fault = true;
                                        double_faults_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 2) { //unforced error
                                        unforced_errors_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 3) { //forehand winner
                                        forehand_winner_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 4) { //backhand winner
                                        backhand_winner_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 5) { //forehand volley
                                        forehand_volley_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 6) { //backhand volley
                                        backhand_volley_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 7) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 8) { //other winner
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 9) { //net in
                                        calculateScore(OPPT_SERVE);
                                    }
                                } else { //first serve
                                    first_serve_count = 1;
                                    if (item == 0) { //ace
                                        //is_ace = true;
                                        ace_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 1) {
                                        Log.d(TAG, "second serve");
                                        first_serve_miss = 1;
                                        is_second_serve = true;
                                        calculateScore(OPPT_SERVE);
                                    } else if (item == 2) { //unforced error
                                        unforced_errors_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 3) { //forehand winner
                                        forehand_winner_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 4) { //backhand winner
                                        backhand_winner_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 5) { //forehand volley
                                        forehand_volley_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 6) { //backhand volley
                                        backhand_volley_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 7) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 8) { //other winner
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 9) { //net in
                                        calculateScore(OPPT_SERVE);
                                    }
                                }
                            }
                        });
                        builder.show();
                    } else { //you serve
                        items.add(getResources().getString(R.string.game_unforced_error));
                        items.add(getResources().getString(R.string.game_forehand_winner));
                        items.add(getResources().getString(R.string.game_backhand_winner));
                        items.add(getResources().getString(R.string.game_forehand_volley));
                        items.add(getResources().getString(R.string.game_backhand_volley));
                        items.add(getResources().getString(R.string.game_foul_to_lose));
                        items.add(getResources().getString(R.string.game_other_winner));

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(GameActivity.this, android.R.layout.select_dialog_item, items);

                        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                        builder.setTitle(playerUp + " " + getResources().getString(R.string.game_select_action));
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (is_second_serve) {
                                    second_serve_count = 1;
                                    if (item == 0) { //unforced error
                                        unforced_errors_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 1) { //forehand winner
                                        forehand_winner_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 2) { //backhand winner
                                        backhand_winner_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 3) { //forehand volley
                                        forehand_volley_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 4) { //backhand volley
                                        backhand_volley_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 5) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 6) { //other winner
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    }
                                } else {
                                    first_serve_count = 1;
                                    if (item == 0) { //unforced error
                                        unforced_errors_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 1) { //forehand winner
                                        forehand_winner_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 2) { //backhand winner
                                        backhand_winner_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 3) { //forehand volley
                                        forehand_volley_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 4) { //backhand volley
                                        backhand_volley_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 5) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 6) { //other winner
                                        first_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    }
                                }

                            }
                        });
                        builder.show();
                    }

                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_use = 0;
                stack.clear();
                handler.removeCallbacks(updateTimer);

                clear_record(filename);

                Intent intent = new Intent(GameActivity.this, SetupMain.class);
                intent.putExtra("FILE_NAME", filename);
                intent.putExtra("PLAYER_UP", playerUp);
                intent.putExtra("PLAYER_DOWN", playerDown);
                intent.putExtra("SETUP_SERVE", serve);
                //playerUp = intent.getStringExtra("PLAYER_UP");
                //playerDown = intent.getStringExtra("PLAYER_DOWN");
                startActivity(intent);
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadDialog = new ProgressDialog(GameActivity.this);
                loadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                loadDialog.setTitle("Saving...");
                loadDialog.setIndeterminate(false);
                loadDialog.setCancelable(false);

                loadDialog.show();
                //clear
                clear_record(filename);

                boolean is_tiebreak;
                boolean is_deuce;
                boolean is_firstserve;

                switch (tiebreak) {
                    case "0":
                        is_tiebreak = true;
                        break;
                    default:
                        is_tiebreak = false;
                        break;
                }

                /*if (tiebreak.equals("0")) {
                    is_tiebreak = true;
                } else {
                    is_tiebreak = false;
                }*/

                switch (deuce) {
                    case "0":
                        is_deuce = true;
                        break;
                    default:
                        is_deuce = false;
                        break;
                }

                /*if (deuce.equals("0")) {
                    is_deuce = true;
                } else {
                    is_deuce = false;
                }*/

                switch (serve) {
                    case "0":
                        is_firstserve = true;
                        break;
                    default:
                        is_firstserve = false;
                        break;
                }

                /*if (serve.equals("0")) {
                    is_firstserve = true;
                } else {
                    is_firstserve = false;
                }*/

                String msg = playerUp + ";" + playerDown + ";" + is_tiebreak + ";" + is_deuce + ";" +is_firstserve+ ";" +set+ "|";
                append_record(msg, filename);

                State top = stack.peek();
                if (top != null) {
                    top.setDuration(time_use);

                    int i = 0;
                    //load stack
                    for (State s : stack) {

                        if (i >= 1) {
                            append_record("&", filename);
                        }


                        String append_msg = s.getCurrent_set() + ";"
                                + s.isServe() + ";"
                                + s.isInTiebreak() + ";"
                                + s.isFinish() + ";"
                                + s.isSecondServe() + ";"
                                + s.isInBreakPoint() + ";"
                                + s.getSetsUp() + ";"
                                + s.getSetsDown() + ";"
                                + s.getDuration() + ";"
                                + s.getAceCountUp() + ";"
                                + s.getAceCountDown() + ";"
                                + s.getFirstServeUp() + ";"
                                + s.getFirstServeDown() + ";"
                                + s.getFirstServeMissUp() + ";"
                                + s.getFirstServeMissDown() + ";"
                                + s.getSecondServeUp() + ";"
                                + s.getSecondServeDown() + ";"
                                + s.getBreakPointUp() + ";"
                                + s.getBreakPointDown() + ";"
                                + s.getBreakPointMissUp() + ";"
                                + s.getBreakPointMissDown() + ";"
                                + s.getFirstServeWonUp() + ";"
                                + s.getFirstServeWonDown() + ";"
                                + s.getFirstServeLostUp() + ";"
                                + s.getFirstServeLostDown() + ";"
                                + s.getSecondServeWonUp() + ";"
                                + s.getSecondServeWonDown() + ";"
                                + s.getSecondServeLostUp() + ";"
                                + s.getSecondServeLostDown() + ";"
                                + s.getDoubleFaultUp() + ";"
                                + s.getDoubleFaultDown() + ";"
                                + s.getUnforceErrorUp() + ";"
                                + s.getUnforceErrorDown() + ";"
                                + s.getForehandWinnerUp() + ";"
                                + s.getForehandWinnerDown() + ";"
                                + s.getBackhandWinnerUp() + ";"
                                + s.getBackhandWinnerDown() + ";"
                                + s.getForehandVolleyUp() + ";"
                                + s.getForehandVolleyDown() + ";"
                                + s.getBackhandVolleyUp() + ";"
                                + s.getBackhandVolleyDown() + ";"
                                + s.getFoulToLoseUp() + ";"
                                + s.getFoulToLoseDown() + ";"
                                + s.getSet_game_up((byte) 0x1) + ";"
                                + s.getSet_game_down((byte) 0x1) + ";"
                                + s.getSet_point_up((byte) 0x1) + ";"
                                + s.getSet_point_down((byte) 0x1) + ";"
                                + s.getSet_tiebreak_point_up((byte) 0x1) + ";"
                                + s.getSet_tiebreak_point_down((byte) 0x1) + ";"
                                + s.getSet_game_up((byte) 0x2) + ";"
                                + s.getSet_game_down((byte) 0x2) + ";"
                                + s.getSet_point_up((byte) 0x2) + ";"
                                + s.getSet_point_down((byte) 0x2) + ";"
                                + s.getSet_tiebreak_point_up((byte) 0x2) + ";"
                                + s.getSet_tiebreak_point_down((byte) 0x2) + ";"
                                + s.getSet_game_up((byte) 0x3) + ";"
                                + s.getSet_game_down((byte) 0x3) + ";"
                                + s.getSet_point_up((byte) 0x3) + ";"
                                + s.getSet_point_down((byte) 0x3) + ";"
                                + s.getSet_tiebreak_point_up((byte) 0x3) + ";"
                                + s.getSet_tiebreak_point_down((byte) 0x3) + ";"
                                + s.getSet_game_up((byte) 0x4) + ";"
                                + s.getSet_game_down((byte) 0x4) + ";"
                                + s.getSet_point_up((byte) 0x4) + ";"
                                + s.getSet_point_down((byte) 0x4) + ";"
                                + s.getSet_tiebreak_point_up((byte) 0x4) + ";"
                                + s.getSet_tiebreak_point_down((byte) 0x4) + ";"
                                + s.getSet_game_up((byte) 0x5) + ";"
                                + s.getSet_game_down((byte) 0x5) + ";"
                                + s.getSet_point_up((byte) 0x5) + ";"
                                + s.getSet_point_down((byte) 0x5) + ";"
                                + s.getSet_tiebreak_point_up((byte) 0x5) + ";"
                                + s.getSet_tiebreak_point_down((byte) 0x5);
                        append_record(append_msg, filename);
                        i++;
                    }
                } else {
                    Log.d(TAG, "Top null");
                }

                loadDialog.dismiss();
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, LoadGame.class);
                intent.putExtra("CALL_ACTIVITY", "Game");
                intent.putExtra("PREVIOUS_FILENAME", filename);
                startActivity(intent);
                finish();
            }
        });

        imgPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_pause) {
                    is_pause = true;
                    imgPlayOrPause.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                    handler.removeCallbacks(updateTimer);
                } else {
                    is_pause = false;
                    imgPlayOrPause.setImageResource(R.drawable.ic_pause_white_48dp);
                    handler.removeCallbacks(updateTimer);
                    handler.postDelayed(updateTimer, 1000);
                }
            }
        });
    }



    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        time_use = 0;
        stack.clear();
        handler.removeCallbacks(updateTimer);

        if (mChatService != null) {
            mChatService.stop();
        }

        super.onDestroy();

    }

    private void calculateScore(StateAction action) {
        byte current_set;
        State new_state=null;
        //load top state first
        final State current_state = stack.peek();

        int set_limit;
        switch (set)
        {
            case "0":
                set_limit = 1;
                break;
            case "1":
                set_limit = 3;
                break;
            case "2":
                set_limit = 5;
                break;
            default:
                set_limit = 1;
                break;
        }

        if (current_state != null) {
            current_set = current_state.getCurrent_set();
            Log.d(TAG, "########## current state start ##########");
            Log.d(TAG, "default:");
            Log.d(TAG, "set = " + set);
            //Log.d(TAG, "game = " + game);
            Log.d(TAG, "tiebreak = " + tiebreak);
            Log.d(TAG, "deuce = " + deuce);
            Log.d(TAG, "serve = " + serve);
            Log.d(TAG, "second serve = "+ is_second_serve);

            Log.d(TAG, "Ace : up = "+current_state.getAceCountUp()+" down = "+current_state.getAceCountDown());
            Log.d(TAG, "First serve miss/count : up = "+current_state.getFirstServeMissUp()+"/"+current_state.getFirstServeUp()+
                    " down = "+current_state.getFirstServeMissDown()+"/"+current_state.getFirstServeDown());
            Log.d(TAG, "Second serve miss/count : up = "+current_state.getDoubleFaultUp()+"/"+current_state.getSecondServeUp()+
                    " down = "+current_state.getDoubleFaultDown()+"/"+current_state.getSecondServeDown());
            Log.d(TAG, "======================");
            Log.d(TAG, "Unforced Error : up = "+current_state.getUnforceErrorUp()+ " down = "+current_state.getUnforceErrorDown());
            Log.d(TAG, "Forehand winner : up = "+current_state.getForehandWinnerUp()+ " down = "+current_state.getForehandWinnerDown());
            Log.d(TAG, "Backhand winner : up = "+current_state.getBackhandWinnerUp()+ " down = "+current_state.getBackhandWinnerDown());
            Log.d(TAG, "Forehand Volley : up = "+current_state.getForehandVolleyUp()+ " down = "+current_state.getForehandVolleyDown());
            Log.d(TAG, "Backhand Volley : up = "+current_state.getBackhandVolleyUp()+ " down = "+current_state.getBackhandVolleyDown());
            Log.d(TAG, "Foul to lose : up = "+current_state.getFoulToLoseUp()+ " down = "+current_state.getFoulToLoseDown());

            Log.d(TAG, "current set : " + current_state.getCurrent_set());
            Log.d(TAG, "Serve : " + current_state.isServe());
            Log.d(TAG, "In tiebreak : " + current_state.isInTiebreak());
            Log.d(TAG, "Finish : " + current_state.isFinish());

            //Log.d(TAG, "set 1:");
            Log.d(TAG, "Game : " + current_state.getSet_game_up(current_set) + " / " + current_state.getSet_game_down(current_set));
            Log.d(TAG, "Point : " + current_state.getSet_point_up(current_set) + " / " + current_state.getSet_point_down(current_set));
            Log.d(TAG, "tiebreak : " + current_state.getSet_tiebreak_point_up(current_set) + " / " + current_state.getSet_tiebreak_point_down(current_set));
            Log.d(TAG, "########## current state end ##########");

            if (current_state.isFinish()) {
                Log.d(TAG, "*** Game is Over ***");
                //handler.removeCallbacks(updateTimer);
                //

                AlertDialog.Builder confirmdialog = new AlertDialog.Builder(GameActivity.this);
                confirmdialog.setTitle(getResources().getString(R.string.game_show_result_dalog));
                confirmdialog.setIcon(R.drawable.ball_icon);
                //confirmdialog.setMessage(request_split[0]+" "+getResources().getString(R.string.macauto_chat_dialog_want_to)+" "+request_file);
                confirmdialog.setCancelable(false);
                confirmdialog.setPositiveButton(getResources().getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(GameActivity.this, ResultActivity.class);
                        intent.putExtra("SET1_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x01)));
                        intent.putExtra("SET1_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x01)));
                        intent.putExtra("SET2_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x02)));
                        intent.putExtra("SET2_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x02)));
                        intent.putExtra("SET3_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x03)));
                        intent.putExtra("SET3_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x03)));
                        intent.putExtra("SET4_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x04)));
                        intent.putExtra("SET4_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x04)));
                        intent.putExtra("SET5_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x05)));
                        intent.putExtra("SET5_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x05)));

                        intent.putExtra("SET1_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x01)));
                        intent.putExtra("SET1_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x01)));
                        intent.putExtra("SET2_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x02)));
                        intent.putExtra("SET2_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x02)));
                        intent.putExtra("SET3_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x03)));
                        intent.putExtra("SET3_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x03)));
                        intent.putExtra("SET4_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x04)));
                        intent.putExtra("SET4_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x04)));
                        intent.putExtra("SET5_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x05)));
                        intent.putExtra("SET5_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x05)));

                        intent.putExtra("GAME_DURATION", String.valueOf(String.valueOf(time_use)));

                        intent.putExtra("PLAYER_UP", playerUp);
                        intent.putExtra("PLAYER_DOWN", playerDown);
                        if (current_state.getSetsUp() > current_state.getSetsDown()) {
                            intent.putExtra("WIN_PLAYER", playerUp);
                            intent.putExtra("LOSE_PLAYER", playerDown);
                        } else {
                            intent.putExtra("WIN_PLAYER", playerDown);
                            intent.putExtra("LOSE_PLAYER", playerUp);
                        }

                        startActivity(intent);

                    }
                });
                confirmdialog.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                confirmdialog.show();


            } else { //not finish
                if (is_pause) { //
                    is_pause = false;
                    imgPlayOrPause.setImageResource(R.drawable.ic_pause_white_48dp);
                    handler.removeCallbacks(updateTimer);
                    handler.postDelayed(updateTimer, 1000);
                }

                Log.d(TAG, "*** Game is running ***");
                new_state = new State();

                int first, first_miss, second;
                Log.d(TAG, "==>[Stack empty]");
                Log.d(TAG, "first_serve_count = "+first_serve_count);
                Log.d(TAG, "first_serve_miss = "+first_serve_miss);
                Log.d(TAG, "second_serve_count = "+second_serve_count);
                Log.d(TAG, "ace_count = "+ace_count);
                Log.d(TAG, "unforced_errors_count = "+unforced_errors_count);
                Log.d(TAG, "foul_to_lose_count = "+foul_to_lose_count);
                Log.d(TAG, "double_faults_count = "+double_faults_count);
                Log.d(TAG, "forehand_winner_count = "+forehand_winner_count);
                Log.d(TAG, "backhand_winner_count = "+backhand_winner_count);
                Log.d(TAG, "forehand_volley_count = "+forehand_volley_count);
                Log.d(TAG, "backhand_volley_count = "+backhand_volley_count);

                Log.d(TAG, "first_serve_won = "+first_serve_won);
                Log.d(TAG, "first_serve_lost = "+first_serve_lost);
                Log.d(TAG, "second_serve_won = "+second_serve_won);
                Log.d(TAG, "second_serve_lost = "+second_serve_lost);

                switch (action) {
                    case YOU_SERVE: //you serve
                        Log.d(TAG, "=== I serve start ===");
                        if (stack.isEmpty()) { //the state stack is empty
                            first = first_serve_count;
                            first_miss = first_serve_miss;
                            second = second_serve_count;

                            new_state.setFirstServeDown((short) first);
                            new_state.setFirstServeMissDown((short) first_miss);
                            new_state.setSecondServeDown((short) second);

                            if (serve.equals("0"))
                                new_state.setServe(true);
                            else
                                new_state.setServe(false);

                            if (is_second_serve) {
                                imgServeUp.setImageResource(R.drawable.ball_icon_red);
                                imgServeDown.setImageResource(R.drawable.ball_icon_red);
                                new_state.setSecondServe(true);
                            } else {
                                imgServeUp.setImageResource(R.drawable.ball_icon);
                                imgServeDown.setImageResource(R.drawable.ball_icon);
                                new_state.setSecondServe(false);
                            }

                            //set current set = 1
                            new_state.setCurrent_set((byte) 0x01);

                            new_state.setDuration(time_use);

                            new_state.setFirstServeDown(first_serve_count);
                            new_state.setFirstServeMissDown(first_serve_miss);
                        } else {
                            Log.d(TAG, "==>[Stack not empty]");
                            first = current_state.getFirstServeDown()+first_serve_count;
                            first_miss = current_state.getFirstServeMissDown()+first_serve_miss;
                            second = current_state.getSecondServeDown()+second_serve_count;


                            new_state.setFirstServeDown((short) first);
                            new_state.setFirstServeMissDown((short) first_miss);
                            new_state.setSecondServeDown((short) second);

                            new_state.setCurrent_set(current_state.getCurrent_set());
                            new_state.setServe(current_state.isServe());
                            new_state.setInTiebreak(current_state.isInTiebreak());
                            new_state.setFinish(current_state.isFinish());
                            if (is_second_serve) {
                                imgServeUp.setImageResource(R.drawable.ball_icon_red);
                                imgServeDown.setImageResource(R.drawable.ball_icon_red);
                                new_state.setSecondServe(true);
                            } else {
                                imgServeUp.setImageResource(R.drawable.ball_icon);
                                imgServeDown.setImageResource(R.drawable.ball_icon);
                                new_state.setSecondServe(false);
                            }


                            new_state.setSetsUp(current_state.getSetsUp());
                            new_state.setSetsDown(current_state.getSetsDown());

                            new_state.setDuration(time_use);

                            new_state.setAceCountUp(current_state.getAceCountUp());
                            new_state.setAceCountDown(current_state.getAceCountDown());
                            new_state.setFirstServeUp(current_state.getFirstServeUp());
                            //new_state.setFirstServeDown(current_state.getFirstServeDown());
                            new_state.setFirstServeMissUp(current_state.getFirstServeMissUp());
                            //new_state.setFirstServeMissDown(current_state.getFirstServeMissDown());
                            new_state.setSecondServeUp(current_state.getSecondServeUp());
                            //new_state.setSecondServeDown(current_state.getSecondServeDown());

                            new_state.setFirstServeWonUp(current_state.getFirstServeWonUp());
                            new_state.setFirstServeWonDown(current_state.getFirstServeWonDown());
                            new_state.setFirstServeLostUp(current_state.getFirstServeLostUp());
                            new_state.setFirstServeLostDown(current_state.getFirstServeLostDown());

                            new_state.setSecondServeWonUp(current_state.getSecondServeWonUp());
                            new_state.setSecondServeWonDown(current_state.getSecondServeWonDown());
                            new_state.setSecondServeLostUp(current_state.getSecondServeLostUp());
                            new_state.setSecondServeLostDown(current_state.getSecondServeLostDown());

                            new_state.setBreakPointUp(current_state.getBreakPointUp());
                            new_state.setBreakPointMissUp(current_state.getBreakPointMissUp());

                            new_state.setDoubleFaultUp(current_state.getDoubleFaultUp());
                            new_state.setDoubleFaultDown(current_state.getDoubleFaultDown());
                            new_state.setUnforceErrorUp(current_state.getUnforceErrorUp());
                            new_state.setUnforceErrorDown(current_state.getUnforceErrorDown());
                            new_state.setForehandWinnerUp(current_state.getForehandWinnerUp());
                            new_state.setForehandWinnerDown(current_state.getForehandWinnerDown());
                            new_state.setBackhandWinnerUp(current_state.getBackhandWinnerUp());
                            new_state.setBackhandWinnerDown(current_state.getBackhandWinnerDown());
                            new_state.setForehandVolleyUp(current_state.getForehandVolleyUp());
                            new_state.setForehandVolleyDown(current_state.getForehandVolleyDown());
                            new_state.setBackhandVolleyUp(current_state.getBackhandVolleyUp());
                            new_state.setBackhandVolleyDown(current_state.getBackhandVolleyDown());
                            new_state.setFoulToLoseUp(current_state.getFoulToLoseUp());
                            new_state.setFoulToLoseDown(current_state.getFoulToLoseDown());

                            for (byte i=1; i<=set_limit; i++) {
                                new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                            }

                            Log.d(TAG, "your first serve : miss/count = "+new_state.getFirstServeMissDown()+"/"+new_state.getFirstServeDown());
                            Log.d(TAG, "your second serve : miss/count = "+new_state.getDoubleFaultDown()+"/"+new_state.getSecondServeDown());
                        }

                        Log.d(TAG, "=== I serve end ===");
                        break;
                    case OPPT_SERVE: //oppt serve
                        Log.d(TAG, "=== oppt serve start ===");
                        if (stack.isEmpty()) { //the state stack is empty
                            Log.d(TAG, "==>[Stack empty]");
                            first = first_serve_count;
                            first_miss = first_serve_miss;
                            second = second_serve_count;

                            new_state.setFirstServeUp((short) first);
                            new_state.setFirstServeMissUp((short) first_miss);
                            new_state.setSecondServeUp((short) second);

                            if (serve.equals("0"))
                                new_state.setServe(true);
                            else
                                new_state.setServe(false);
                            if (is_second_serve) {
                                imgServeUp.setImageResource(R.drawable.ball_icon_red);
                                imgServeDown.setImageResource(R.drawable.ball_icon_red);
                                new_state.setSecondServe(true);
                            } else {
                                imgServeUp.setImageResource(R.drawable.ball_icon);
                                imgServeDown.setImageResource(R.drawable.ball_icon);
                                new_state.setSecondServe(false);
                            }

                            //set current set = 1
                            new_state.setCurrent_set((byte) 0x01);

                            new_state.setDuration(time_use);

                            new_state.setFirstServeUp(first_serve_count);
                            new_state.setFirstServeMissUp(first_serve_miss);
                        } else {
                            Log.d(TAG, "==>[Stack not empty]");
                            first = current_state.getFirstServeUp()+first_serve_count;
                            first_miss = current_state.getFirstServeMissUp()+first_serve_miss;
                            second = current_state.getSecondServeUp()+second_serve_count;

                            new_state.setFirstServeUp((short) first);
                            new_state.setFirstServeMissUp((short) first_miss);
                            new_state.setSecondServeUp((short) second);

                            new_state.setCurrent_set(current_state.getCurrent_set());
                            new_state.setServe(current_state.isServe());
                            new_state.setInTiebreak(current_state.isInTiebreak());
                            new_state.setFinish(current_state.isFinish());
                            if (is_second_serve) {
                                imgServeUp.setImageResource(R.drawable.ball_icon_red);
                                imgServeDown.setImageResource(R.drawable.ball_icon_red);
                                new_state.setSecondServe(true);
                            } else {
                                imgServeUp.setImageResource(R.drawable.ball_icon);
                                imgServeDown.setImageResource(R.drawable.ball_icon);
                                new_state.setSecondServe(false);
                            }

                            new_state.setSetsUp(current_state.getSetsUp());
                            new_state.setSetsDown(current_state.getSetsDown());

                            new_state.setDuration(time_use);

                            new_state.setAceCountUp(current_state.getAceCountUp());
                            new_state.setAceCountDown(current_state.getAceCountDown());
                            //new_state.setFirstServeUp(current_state.getFirstServeUp());
                            new_state.setFirstServeDown(current_state.getFirstServeDown());
                            //new_state.setFirstServeMissUp(current_state.getFirstServeMissUp());
                            new_state.setFirstServeMissDown(current_state.getFirstServeMissDown());
                            //new_state.setSecondServeUp(current_state.getSecondServeUp());
                            new_state.setSecondServeDown(current_state.getSecondServeDown());

                            new_state.setFirstServeWonUp(current_state.getFirstServeWonUp());
                            new_state.setFirstServeWonDown(current_state.getFirstServeWonDown());
                            new_state.setFirstServeLostUp(current_state.getFirstServeLostUp());
                            new_state.setFirstServeLostDown(current_state.getFirstServeLostDown());

                            new_state.setSecondServeWonUp(current_state.getSecondServeWonUp());
                            new_state.setSecondServeWonDown(current_state.getSecondServeWonDown());
                            new_state.setSecondServeLostUp(current_state.getSecondServeLostUp());
                            new_state.setSecondServeLostDown(current_state.getSecondServeLostDown());

                            new_state.setBreakPointDown(current_state.getBreakPointDown());
                            new_state.setBreakPointMissDown(current_state.getBreakPointMissDown());

                            new_state.setDoubleFaultUp(current_state.getDoubleFaultUp());
                            new_state.setDoubleFaultDown(current_state.getDoubleFaultDown());
                            new_state.setUnforceErrorUp(current_state.getUnforceErrorUp());
                            new_state.setUnforceErrorDown(current_state.getUnforceErrorDown());
                            new_state.setForehandWinnerUp(current_state.getForehandWinnerUp());
                            new_state.setForehandWinnerDown(current_state.getForehandWinnerDown());
                            new_state.setBackhandWinnerUp(current_state.getBackhandWinnerUp());
                            new_state.setBackhandWinnerDown(current_state.getBackhandWinnerDown());
                            new_state.setForehandVolleyUp(current_state.getForehandVolleyUp());
                            new_state.setForehandVolleyDown(current_state.getForehandVolleyDown());
                            new_state.setBackhandVolleyUp(current_state.getBackhandVolleyUp());
                            new_state.setBackhandVolleyDown(current_state.getBackhandVolleyDown());
                            new_state.setFoulToLoseUp(current_state.getFoulToLoseUp());
                            new_state.setFoulToLoseDown(current_state.getFoulToLoseDown());

                            for (byte i=1; i<=set_limit; i++) {
                                new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                            }

                            Log.d(TAG, "oppt first serve : miss/count = "+new_state.getFirstServeMissUp()+"/"+new_state.getFirstServeUp());
                            Log.d(TAG, "oppt second serve : miss/count = "+new_state.getDoubleFaultUp()+"/"+new_state.getSecondServeUp());
                        }

                        Log.d(TAG, "=== oppt serve end ===");
                        break;
                    case YOU_SCORE: //you score
                        Log.d(TAG, "=== I score start ===");

                        if (stack.isEmpty()) { //the state stack is empty
                            Log.d(TAG, "==>[Stack empty]");

                            if (serve.equals("0"))
                                new_state.setServe(true);
                            else
                                new_state.setServe(false);
                            if (is_second_serve)
                                new_state.setSecondServe(true);
                            else
                                new_state.setSecondServe(false);

                            first = first_serve_count;
                            if (new_state.isServe()) { //you serve
                                Log.d(TAG, "you serve");
                                new_state.setFirstServeDown((short) first);
                            } else {
                                Log.d(TAG, "oppt serve");
                                new_state.setFirstServeUp((short) first);
                            }



                            //set current set = 1
                            new_state.setCurrent_set((byte) 0x01);

                            new_state.setSet_point_down((byte) 0x01, (byte) 0x01);

                            new_state.setDuration(time_use);

                            //win by your self
                            new_state.setAceCountDown(ace_count);
                            new_state.setForehandWinnerDown(forehand_winner_count);
                            new_state.setBackhandWinnerDown(backhand_winner_count);
                            new_state.setForehandVolleyDown(forehand_volley_count);
                            new_state.setBackhandVolleyDown(backhand_volley_count);
                            //oppt lose
                            new_state.setDoubleFaultUp(double_faults_count);
                            new_state.setUnforceErrorUp(unforced_errors_count);
                            new_state.setFoulToLoseUp(foul_to_lose_count);

                            if (new_state.isServe()) //you serve
                                new_state.setFirstServeWonDown(first_serve_won);
                            else //oppt serve
                                new_state.setFirstServeLostUp(first_serve_lost);

                        } else {
                            Log.d(TAG, "==>[Stack not empty]");

                            if (current_state.isFinish()) {
                                Log.d(TAG, "**** Game Finish ****");
                            } else {
                                new_state.setCurrent_set(current_state.getCurrent_set());
                                new_state.setServe(current_state.isServe());
                                new_state.setInTiebreak(current_state.isInTiebreak());
                                new_state.setFinish(current_state.isFinish());
                                if (is_second_serve)
                                    new_state.setSecondServe(true);
                                else
                                    new_state.setSecondServe(false);

                                new_state.setSetsUp(current_state.getSetsUp());
                                new_state.setSetsDown(current_state.getSetsDown());

                                new_state.setDuration(time_use);

                                new_state.setFirstServeUp(current_state.getFirstServeUp());
                                new_state.setFirstServeDown(current_state.getFirstServeDown());
                                new_state.setFirstServeMissUp(current_state.getFirstServeMissUp());
                                new_state.setFirstServeMissDown(current_state.getFirstServeMissDown());
                                new_state.setSecondServeUp(current_state.getSecondServeUp());
                                new_state.setSecondServeDown(current_state.getSecondServeDown());

                                new_state.setBreakPointUp(current_state.getBreakPointUp());
                                new_state.setBreakPointDown(current_state.getBreakPointDown());
                                new_state.setBreakPointMissUp(current_state.getBreakPointMissUp());
                                new_state.setBreakPointMissDown(current_state.getBreakPointMissDown());

                                new_state.setFirstServeWonUp(current_state.getFirstServeWonUp());
                                new_state.setFirstServeWonDown(current_state.getFirstServeWonDown());
                                new_state.setFirstServeLostUp(current_state.getFirstServeLostUp());
                                new_state.setFirstServeLostDown(current_state.getFirstServeLostDown());

                                new_state.setSecondServeWonUp(current_state.getSecondServeWonUp());
                                new_state.setSecondServeWonDown(current_state.getSecondServeWonDown());
                                new_state.setSecondServeLostUp(current_state.getSecondServeLostUp());
                                new_state.setSecondServeLostDown(current_state.getSecondServeLostDown());

                                new_state.setAceCountUp(current_state.getAceCountUp());
                                new_state.setAceCountDown(current_state.getAceCountDown());
                                new_state.setDoubleFaultUp(current_state.getDoubleFaultUp());
                                new_state.setDoubleFaultDown(current_state.getDoubleFaultDown());
                                new_state.setUnforceErrorUp(current_state.getUnforceErrorUp());
                                new_state.setUnforceErrorDown(current_state.getUnforceErrorDown());
                                new_state.setForehandWinnerUp(current_state.getForehandWinnerUp());
                                new_state.setForehandWinnerDown(current_state.getForehandWinnerDown());
                                new_state.setBackhandWinnerUp(current_state.getBackhandWinnerUp());
                                new_state.setBackhandWinnerDown(current_state.getBackhandWinnerDown());
                                new_state.setForehandVolleyUp(current_state.getForehandVolleyUp());
                                new_state.setForehandVolleyDown(current_state.getForehandVolleyDown());
                                new_state.setBackhandVolleyUp(current_state.getBackhandVolleyUp());
                                new_state.setBackhandVolleyDown(current_state.getBackhandVolleyDown());
                                new_state.setFoulToLoseUp(current_state.getFoulToLoseUp());
                                new_state.setFoulToLoseDown(current_state.getFoulToLoseDown());

                                for (byte i=1; i<=set_limit; i++) {
                                    new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                    new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                    new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                    new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                    new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                    new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                                }

                                if (current_state.isServe()) { //you serve
                                    Log.d(TAG, "you serve");
                                    first = current_state.getFirstServeDown()+first_serve_count;
                                    first_miss = current_state.getFirstServeMissDown()+first_serve_miss;
                                    second = current_state.getSecondServeDown()+second_serve_count;

                                    new_state.setFirstServeDown((short) first);
                                    new_state.setFirstServeMissDown((short) first_miss);
                                    new_state.setSecondServeDown((short) second);
                                    //win on your own
                                    new_state.setAceCountDown((byte)(new_state.getAceCountDown()+ace_count));
                                    new_state.setForehandWinnerDown((short)(new_state.getForehandWinnerDown()+forehand_winner_count));
                                    new_state.setBackhandWinnerDown((short)(new_state.getBackhandWinnerDown()+backhand_winner_count));
                                    new_state.setForehandVolleyDown((short)(new_state.getForehandVolleyDown()+forehand_volley_count));
                                    new_state.setBackhandVolleyDown((short)(new_state.getBackhandVolleyDown()+backhand_volley_count));
                                    //win on oppt lose
                                    new_state.setUnforceErrorUp((byte)(new_state.getUnforceErrorUp()+unforced_errors_count));
                                    new_state.setFoulToLoseUp((byte)(new_state.getFoulToLoseUp()+foul_to_lose_count));
                                    //score on first serve or second serve
                                    if (is_second_serve)
                                        new_state.setSecondServeWonDown((short)(new_state.getSecondServeWonDown()+second_serve_won));
                                    else //first serve
                                        new_state.setFirstServeWonDown((short)(new_state.getFirstServeWonDown()+first_serve_won));

                                } else {
                                    Log.d(TAG, "oppt serve");
                                    first = current_state.getFirstServeUp()+first_serve_count;
                                    first_miss = current_state.getFirstServeMissUp()+first_serve_miss;
                                    second = current_state.getSecondServeUp()+second_serve_count;

                                    new_state.setFirstServeUp((short) first);
                                    new_state.setFirstServeMissUp((short) first_miss);
                                    new_state.setSecondServeUp((short) second);

                                    //win on your own
                                    new_state.setForehandWinnerDown((short)(new_state.getForehandWinnerDown()+forehand_winner_count));
                                    new_state.setBackhandWinnerDown((short)(new_state.getBackhandWinnerDown()+backhand_winner_count));
                                    new_state.setForehandVolleyDown((short)(new_state.getForehandVolleyDown()+forehand_volley_count));
                                    new_state.setBackhandVolleyDown((short)(new_state.getBackhandVolleyDown()+backhand_volley_count));
                                    //win on oppt lose
                                    new_state.setDoubleFaultUp((byte)(new_state.getDoubleFaultUp()+double_faults_count));
                                    new_state.setUnforceErrorUp((byte)(new_state.getUnforceErrorUp()+unforced_errors_count));
                                    new_state.setFoulToLoseUp((byte)(new_state.getFoulToLoseUp()+foul_to_lose_count));
                                    if (is_second_serve)
                                        new_state.setSecondServeLostUp((short)(new_state.getSecondServeLostUp()+second_serve_lost));
                                    else //first serve
                                        new_state.setFirstServeLostUp((short)(new_state.getFirstServeLostUp()+first_serve_lost));
                                }

                                Log.d(TAG, "your first serve : miss/count = "+new_state.getFirstServeMissDown()+"/"+new_state.getFirstServeDown());
                                Log.d(TAG, "your second serve : miss/count = "+new_state.getDoubleFaultDown()+"/"+new_state.getSecondServeDown());
                                Log.d(TAG, "oppt first serve : miss/count = "+new_state.getFirstServeMissUp()+"/"+new_state.getSecondServeUp());
                                Log.d(TAG, "oppt second serve : miss/count = "+new_state.getDoubleFaultDown()+"/"+new_state.getSecondServeDown());

                                Log.d(TAG, "your first serve : lost/won = "+new_state.getFirstServeLostDown()+"/"+new_state.getFirstServeWonDown());
                                Log.d(TAG, "your second serve : lost/won = "+new_state.getSecondServeLostDown()+"/"+new_state.getSecondServeWonDown());
                                Log.d(TAG, "oppt first serve : lost/won = "+new_state.getFirstServeLostUp()+"/"+new_state.getFirstServeWonUp());
                                Log.d(TAG, "oppt second serve : lost/won = "+new_state.getSecondServeLostUp()+"/"+new_state.getSecondServeWonUp());

                                //you score!
                                byte point = current_state.getSet_point_down(current_set);
                                Log.d(TAG, "Your point " + point + " change to " + (++point));
                                new_state.setSet_point_down(current_set, point);

                                checkPoint(new_state);

                                checkGames(new_state);
                            }
                        }

                        //scored, reset serve
                        imgServeUp.setImageResource(R.drawable.ball_icon);
                        imgServeDown.setImageResource(R.drawable.ball_icon);
                        is_second_serve = false;
                        Log.d(TAG, "=== I score end ===");
                        break;
                    case OPPT_SCORE: //oppt score
                        Log.d(TAG, "=== Oppt score start ===");

                        if (stack.isEmpty()) { //the state stack is empty
                            Log.d(TAG, "==>[Stack empty]");

                            if (serve.equals("0"))
                                new_state.setServe(true);
                            else
                                new_state.setServe(false);
                            if (is_second_serve)
                                new_state.setSecondServe(true);
                            else
                                new_state.setSecondServe(false);

                            first = first_serve_count;
                            if (new_state.isServe()) { //you serve
                                Log.d(TAG, "you serve");
                                new_state.setFirstServeDown((short) first);
                            } else {
                                Log.d(TAG, "oppt serve");
                                new_state.setFirstServeUp((short) first);
                            }

                            //set current set = 1
                            new_state.setCurrent_set((byte) 0x01);

                            new_state.setSet_point_up((byte) 0x01, (byte) 0x01);

                            new_state.setDuration(time_use);

                            //oppt win on his own
                            new_state.setAceCountUp(ace_count);
                            new_state.setForehandWinnerUp(forehand_winner_count);
                            new_state.setBackhandWinnerUp(backhand_winner_count);
                            new_state.setForehandVolleyUp(forehand_volley_count);
                            new_state.setBackhandVolleyUp(backhand_volley_count);

                            //win by your lose
                            new_state.setDoubleFaultDown(double_faults_count);
                            new_state.setUnforceErrorDown(unforced_errors_count);
                            new_state.setFoulToLoseDown(foul_to_lose_count);

                            if (new_state.isServe()) //you serve
                                new_state.setFirstServeLostDown(first_serve_lost);
                            else //oppt serve
                                new_state.setFirstServeWonUp(first_serve_won);
                        } else {
                            Log.d(TAG, "==>[Stack not empty]");
                            if (current_state.isFinish()) {
                                Log.d(TAG, "**** Game Finish ****");
                            } else {
                                new_state.setCurrent_set(current_state.getCurrent_set());
                                new_state.setServe(current_state.isServe());
                                new_state.setInTiebreak(current_state.isInTiebreak());
                                new_state.setFinish(current_state.isFinish());
                                if (is_second_serve)
                                    new_state.setSecondServe(true);
                                else
                                    new_state.setSecondServe(false);

                                new_state.setSetsUp(current_state.getSetsUp());
                                new_state.setSetsDown(current_state.getSetsDown());

                                new_state.setDuration(time_use);

                                new_state.setFirstServeUp(current_state.getFirstServeUp());
                                new_state.setFirstServeDown(current_state.getFirstServeDown());
                                new_state.setFirstServeMissUp(current_state.getFirstServeMissUp());
                                new_state.setFirstServeMissDown(current_state.getFirstServeMissDown());
                                new_state.setSecondServeUp(current_state.getSecondServeUp());
                                new_state.setSecondServeDown(current_state.getSecondServeDown());

                                new_state.setBreakPointUp(current_state.getBreakPointUp());
                                new_state.setBreakPointDown(current_state.getBreakPointDown());
                                new_state.setBreakPointMissUp(current_state.getBreakPointMissUp());
                                new_state.setBreakPointMissDown(current_state.getBreakPointMissDown());

                                new_state.setFirstServeWonUp(current_state.getFirstServeWonUp());
                                new_state.setFirstServeWonDown(current_state.getFirstServeWonDown());
                                new_state.setFirstServeLostUp(current_state.getFirstServeLostUp());
                                new_state.setFirstServeLostDown(current_state.getFirstServeLostDown());

                                new_state.setSecondServeWonUp(current_state.getSecondServeWonUp());
                                new_state.setSecondServeWonDown(current_state.getSecondServeWonDown());
                                new_state.setSecondServeLostUp(current_state.getSecondServeLostUp());
                                new_state.setSecondServeLostDown(current_state.getSecondServeLostDown());

                                new_state.setAceCountUp(current_state.getAceCountUp());
                                new_state.setAceCountDown(current_state.getAceCountDown());
                                new_state.setDoubleFaultUp(current_state.getDoubleFaultUp());
                                new_state.setDoubleFaultDown(current_state.getDoubleFaultDown());
                                new_state.setUnforceErrorUp(current_state.getUnforceErrorUp());
                                new_state.setUnforceErrorDown(current_state.getUnforceErrorDown());
                                new_state.setForehandWinnerUp(current_state.getForehandWinnerUp());
                                new_state.setForehandWinnerDown(current_state.getForehandWinnerDown());
                                new_state.setBackhandWinnerUp(current_state.getBackhandWinnerUp());
                                new_state.setBackhandWinnerDown(current_state.getBackhandWinnerDown());
                                new_state.setForehandVolleyUp(current_state.getForehandVolleyUp());
                                new_state.setForehandVolleyDown(current_state.getForehandVolleyDown());
                                new_state.setBackhandVolleyUp(current_state.getBackhandVolleyUp());
                                new_state.setBackhandVolleyDown(current_state.getBackhandVolleyDown());
                                new_state.setFoulToLoseUp(current_state.getFoulToLoseUp());
                                new_state.setFoulToLoseDown(current_state.getFoulToLoseDown());

                                for (byte i=1; i<=set_limit; i++) {
                                    new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                    new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                    new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                    new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                    new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                    new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                                }

                                if (current_state.isServe()) { //you serve
                                    Log.d(TAG, "you serve");
                                    first = current_state.getFirstServeDown()+first_serve_count;
                                    first_miss = current_state.getFirstServeMissDown()+first_serve_miss;
                                    second = current_state.getSecondServeDown()+second_serve_count;

                                    new_state.setFirstServeDown((short) first);
                                    new_state.setFirstServeMissDown((short) first_miss);
                                    new_state.setSecondServeDown((short) second);

                                    //win on oppt own
                                    new_state.setForehandWinnerUp((short)(new_state.getForehandWinnerUp()+forehand_winner_count));
                                    new_state.setBackhandWinnerUp((short)(new_state.getBackhandWinnerUp()+backhand_winner_count));
                                    new_state.setForehandVolleyUp((short)(new_state.getForehandVolleyUp()+forehand_volley_count));
                                    new_state.setBackhandVolleyUp((short)(new_state.getBackhandVolleyUp()+backhand_volley_count));
                                    //win on your lose
                                    new_state.setDoubleFaultDown((byte)(new_state.getDoubleFaultDown()+double_faults_count));
                                    new_state.setUnforceErrorDown((byte)(new_state.getUnforceErrorDown()+unforced_errors_count));
                                    new_state.setFoulToLoseDown((byte)(new_state.getFoulToLoseDown()+foul_to_lose_count));

                                    //you serve, oppt scored
                                    if (is_second_serve) {
                                        new_state.setSecondServeLostDown((short)(new_state.getSecondServeLostDown()+second_serve_lost));
                                    } else {
                                        new_state.setFirstServeLostDown((short)(new_state.getFirstServeLostDown()+first_serve_lost));
                                    }
                                } else {
                                    Log.d(TAG, "oppt serve");
                                    first = current_state.getFirstServeUp()+first_serve_count;
                                    first_miss = current_state.getFirstServeMissUp()+first_serve_miss;
                                    second = current_state.getSecondServeUp()+second_serve_count;

                                    new_state.setFirstServeUp((short) first);
                                    new_state.setFirstServeMissUp((short) first_miss);
                                    new_state.setSecondServeUp((short) second);

                                    //win on oppt own
                                    new_state.setAceCountUp((byte)(new_state.getAceCountUp()+ace_count));
                                    new_state.setForehandWinnerUp((short)(new_state.getForehandWinnerUp()+forehand_winner_count));
                                    new_state.setBackhandWinnerUp((short)(new_state.getBackhandWinnerUp()+backhand_winner_count));
                                    new_state.setForehandVolleyUp((short)(new_state.getForehandVolleyUp()+forehand_volley_count));
                                    new_state.setBackhandVolleyUp((short)(new_state.getBackhandVolleyUp()+backhand_volley_count));
                                    //win on your lose
                                    new_state.setUnforceErrorDown((byte)(new_state.getUnforceErrorDown()+unforced_errors_count));
                                    new_state.setFoulToLoseDown((byte)(new_state.getFoulToLoseDown()+foul_to_lose_count));

                                    //oppt serve, oppt scored
                                    if (is_second_serve) {
                                        new_state.setSecondServeWonUp((short)(new_state.getSecondServeWonUp()+second_serve_won));
                                    } else {
                                        new_state.setFirstServeWonUp((short)(new_state.getFirstServeWonUp()+first_serve_won));
                                    }
                                }

                                Log.d(TAG, "your first serve : miss/count = "+new_state.getFirstServeMissDown()+"/"+new_state.getFirstServeDown());
                                Log.d(TAG, "your second serve : miss/count = "+new_state.getDoubleFaultDown()+"/"+new_state.getSecondServeDown());
                                Log.d(TAG, "oppt first serve : miss/count = "+new_state.getFirstServeMissUp()+"/"+new_state.getSecondServeUp());
                                Log.d(TAG, "oppt second serve : miss/count = "+new_state.getDoubleFaultDown()+"/"+new_state.getSecondServeDown());

                                Log.d(TAG, "your first serve : lost/won = "+new_state.getFirstServeLostDown()+"/"+new_state.getFirstServeWonDown());
                                Log.d(TAG, "your second serve : lost/won = "+new_state.getSecondServeLostDown()+"/"+new_state.getSecondServeWonDown());
                                Log.d(TAG, "oppt first serve : lost/won = "+new_state.getFirstServeLostUp()+"/"+new_state.getFirstServeWonUp());
                                Log.d(TAG, "oppt second serve : lost/won = "+new_state.getSecondServeLostUp()+"/"+new_state.getSecondServeWonUp());

                                //oppt score!
                                byte point = current_state.getSet_point_up(current_set);
                                Log.d(TAG, "Opponent point " + point + " change to " + (++point));
                                new_state.setSet_point_up(current_set, point);

                                checkPoint(new_state);

                                checkGames(new_state);
                            }
                        }

                        //scored, reset serve
                        imgServeUp.setImageResource(R.drawable.ball_icon);
                        imgServeDown.setImageResource(R.drawable.ball_icon);
                        is_second_serve = false;
                        Log.d(TAG, "=== Oppt score end ===");
                        break;
                } //switch end

                if (new_state != null) {

                    Log.d(TAG, "########## new state start ##########");
                    Log.d(TAG, "current set : " + new_state.getCurrent_set());
                    Log.d(TAG, "Serve : " + new_state.isServe());
                    Log.d(TAG, "In tiebreak : " + new_state.isInTiebreak());
                    Log.d(TAG, "Finish : " + new_state.isFinish());
                    Log.d(TAG, "Second serve : "+new_state.isSecondServe());
                    Log.d(TAG, "Ace : up = "+new_state.getAceCountUp()+" down = "+new_state.getAceCountDown());
                    Log.d(TAG, "Double Faults : up  = "+new_state.getDoubleFaultUp()+ " down = "+new_state.getDoubleFaultDown());
                    Log.d(TAG, "First serve miss/count : up = "+new_state.getFirstServeMissUp()+"/"+new_state.getFirstServeUp()+
                            " down = "+new_state.getFirstServeMissDown()+"/"+new_state.getFirstServeDown());
                    Log.d(TAG, "Second serve miss/count : up = "+new_state.getDoubleFaultUp()+"/"+new_state.getSecondServeUp()+
                            " down = "+new_state.getDoubleFaultDown()+"/"+new_state.getSecondServeDown());

                    Log.d(TAG, "First serve lost/won : up = "+new_state.getFirstServeLostUp()+"/"+new_state.getFirstServeWonUp()+" down = "
                            +new_state.getFirstServeLostDown()+"/"+new_state.getFirstServeWonDown());
                    Log.d(TAG, "Second serve lost/won : up = "+new_state.getSecondServeLostUp()+"/"+new_state.getSecondServeWonUp()+" down = "
                            +new_state.getSecondServeLostDown()+"/"+new_state.getSecondServeWonDown());

                    Log.d(TAG, "======================");

                    Log.d(TAG, "Unforced Error : up = "+new_state.getUnforceErrorUp()+ " down = "+new_state.getUnforceErrorDown());
                    Log.d(TAG, "Forehand winner : up = "+new_state.getForehandWinnerUp()+ " down = "+new_state.getForehandWinnerDown());
                    Log.d(TAG, "Backhand winner : up = "+new_state.getBackhandWinnerUp()+ " down = "+new_state.getBackhandWinnerDown());
                    Log.d(TAG, "Forehand Volley : up = "+new_state.getForehandVolleyUp()+ " down = "+new_state.getForehandVolleyDown());
                    Log.d(TAG, "Backhand Volley : up = "+new_state.getBackhandVolleyUp()+ " down = "+new_state.getBackhandVolleyDown());
                    Log.d(TAG, "Foul to lose : up = "+new_state.getFoulToLoseUp()+ " down = "+new_state.getFoulToLoseDown());
                    //Log.d(TAG, "deuce : " + new_state.isDeuce());
                    //Log.d(TAG, "set Limit : " + new_state.getSetLimit());
                    Log.d(TAG, "Set up : " + new_state.getSetsUp());
                    Log.d(TAG, "Set down : " + new_state.getSetsDown());

                    Log.d(TAG, "Duration : " + new_state.getDuration());

                    for (int i = 1; i <= set_limit; i++) {
                        Log.d(TAG, "================================");
                        Log.d(TAG, "[set " + i + "]");
                        Log.d(TAG, "[Game : " + new_state.getSet_game_up((byte) i) + " / " + new_state.getSet_game_down((byte) i) + "]");
                        Log.d(TAG, "[Point : " + new_state.getSet_point_up((byte) i) + " / " + new_state.getSet_point_down((byte) i) + "]");
                        Log.d(TAG, "[tiebreak : " + new_state.getSet_tiebreak_point_up((byte) i) + " / " + new_state.getSet_tiebreak_point_down((byte) i) + "]");
                    }

                    Log.d(TAG, "########## new state end ##########");

                    //then look up top state
                    //State new_current_state = stack.peek();
                    current_set = new_state.getCurrent_set();

                    if (new_state.getSetsUp() > 0 || new_state.getSetsDown() > 0) {
                        setLayout.setVisibility(View.VISIBLE);
                        setUp.setText(String.valueOf(new_state.getSetsUp()));
                        setDown.setText(String.valueOf(new_state.getSetsDown()));
                    } else {
                        setLayout.setVisibility(View.GONE);
                        setUp.setText("0");
                        setDown.setText("0");
                    }

                    gameUp.setText(String.valueOf(new_state.getSet_game_up(current_set)));
                    gameDown.setText(String.valueOf(new_state.getSet_game_down(current_set)));

                    if (new_state.isFinish()) {
                        imgServeUp.setVisibility(View.INVISIBLE);
                        imgServeDown.setVisibility(View.INVISIBLE);

                        if (new_state.getSetsUp() > new_state.getSetsDown()) {
                            imgWinCheckUp.setVisibility(View.VISIBLE);
                            imgWinCheckDown.setVisibility(View.GONE);
                        } else {
                            imgWinCheckUp.setVisibility(View.GONE);
                            imgWinCheckDown.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (new_state.isServe()) {
                            imgServeUp.setVisibility(View.INVISIBLE);
                            imgServeDown.setVisibility(View.VISIBLE);
                        } else {
                            imgServeUp.setVisibility(View.VISIBLE);
                            imgServeDown.setVisibility(View.INVISIBLE);
                        }
                    }

                    if (!new_state.isInTiebreak()) { //not in tiebreak
                        if (new_state.getSet_point_up(current_set) == 1) {
                            pointUp.setText(String.valueOf(15));
                        } else if (new_state.getSet_point_up(current_set) == 2) {
                            pointUp.setText(String.valueOf(30));
                        } else if (new_state.getSet_point_up(current_set) == 3) {
                            pointUp.setText(String.valueOf(40));
                        } else if (new_state.getSet_point_up(current_set) == 4) {
                            String msg = String.valueOf(40)+"A";
                            pointUp.setText(msg);
                        } else {
                            pointUp.setText("0");
                        }
                    } else { //tie break;
                        pointUp.setText(String.valueOf(new_state.getSet_point_up(current_set)));
                    }

                    if (!new_state.isInTiebreak()) { //not in tiebreak
                        if (new_state.getSet_point_down(current_set) == 1) {
                            pointDown.setText(String.valueOf(15));
                        } else if (new_state.getSet_point_down(current_set) == 2) {
                            pointDown.setText(String.valueOf(30));
                        } else if (new_state.getSet_point_down(current_set) == 3) {
                            pointDown.setText(String.valueOf(40));
                        } else if (new_state.getSet_point_down(current_set) == 4) {
                            String msg = String.valueOf(40)+"A";
                            pointDown.setText(msg);
                        } else {
                            pointDown.setText("0");
                        }
                    } else {
                        pointDown.setText(String.valueOf(new_state.getSet_point_down(current_set)));
                    }

                    //push into stack
                    stack.push(new_state);

            /*Log.d(TAG, "@@@@@@ stack @@@@@@");
            for (State s : stack) {
                Log.d(TAG, "current set : " + s.getCurrent_set());
                Log.d(TAG, "Serve : " + s.isServe());
                Log.d(TAG, "In tiebreak : " + s.isInTiebreak());
                Log.d(TAG, "Finish : " + s.isFinish());

                for (int i = 1; i <= set_limit; i++) {

                    Log.d(TAG, "[set " + i + "]");
                    Log.d(TAG, "[Game : " + s.getSet_game_up((byte) i) + " / " + s.getSet_game_down((byte) i) + "]");
                    Log.d(TAG, "[Point : " + s.getSet_point_up((byte) i) + " / " + s.getSet_point_down((byte) i) + "]");
                    Log.d(TAG, "[tiebreak : " + s.getSet_tiebreak_point_up((byte) i) + " / " + s.getSet_tiebreak_point_down((byte) i) + "]");
                }
                Log.d(TAG, "================================");
            }
            Log.d(TAG, "@@@@@@ stack @@@@@@");*/
                }
            } //not finish end
        } else { //current_state null
            Log.d(TAG, "current_state not null ==>[Stack empty]");
            if (is_pause) { //
                is_pause = false;
                imgPlayOrPause.setImageResource(R.drawable.ic_pause_white_48dp);
                handler.removeCallbacks(updateTimer);
                handler.postDelayed(updateTimer, 1000);
            }
            Log.d(TAG, "*** Game is running ***");

            new_state = new State();

            Log.d(TAG, "first_serve_count = "+first_serve_count);
            Log.d(TAG, "first_serve_miss = "+first_serve_miss);
            Log.d(TAG, "second_serve_count = "+second_serve_count);

            Log.d(TAG, "first_serve_won = "+first_serve_won);
            Log.d(TAG, "first_serve_lost = "+first_serve_lost);
            Log.d(TAG, "second_serve_won = "+second_serve_won);
            Log.d(TAG, "second_serve_lost = "+second_serve_lost);

            if (serve.equals("0"))
                new_state.setServe(true);
            else
                new_state.setServe(false);


            if (is_second_serve) {
                new_state.setSecondServe(true);
                imgServeUp.setImageResource(R.drawable.ball_icon_red);
                imgServeDown.setImageResource(R.drawable.ball_icon_red);
            } else {
                new_state.setSecondServe(false);
                imgServeUp.setImageResource(R.drawable.ball_icon);
                imgServeDown.setImageResource(R.drawable.ball_icon);
            }
            //set current set = 1
            new_state.setCurrent_set((byte) 0x01);

            new_state.setDuration(time_use);

            switch (action) {
                case YOU_SERVE: //you serve
                    Log.d(TAG, "=== I serve start ===");
                    new_state.setFirstServeDown(first_serve_count);
                    new_state.setFirstServeMissDown(first_serve_miss);
                    first_serve_count = 0;
                    first_serve_miss = 0;
                    //new_state.setSecondServeDown(second_serve_count);
                    Log.d(TAG, "=== I serve end ===");
                    break;
                case OPPT_SERVE: //oppt serve
                    Log.d(TAG, "=== oppt serve start ===");
                    new_state.setFirstServeUp(first_serve_count);
                    new_state.setFirstServeMissUp(first_serve_miss);
                    first_serve_count = 0;
                    first_serve_miss = 0;
                    //new_state.setSecondServeUp(second_serve_count);
                    Log.d(TAG, "=== oppt serve end ===");
                    break;
                case YOU_SCORE: //you score
                    Log.d(TAG, "=== I score start ===");

                    if (new_state.isServe()) //you serve
                    {
                        Log.d(TAG, "you serve");
                        new_state.setFirstServeDown(first_serve_count);
                        new_state.setFirstServeMissDown(first_serve_miss);
                        new_state.setSecondServeDown(second_serve_count);

                        //win on your own
                        new_state.setAceCountDown(ace_count);
                        new_state.setForehandWinnerDown(forehand_winner_count);
                        new_state.setBackhandWinnerDown(backhand_winner_count);
                        new_state.setForehandVolleyDown(forehand_volley_count);
                        new_state.setBackhandVolleyDown(backhand_volley_count);
                        //win on oppt lose
                        new_state.setUnforceErrorUp(unforced_errors_count);
                        new_state.setFoulToLoseUp(foul_to_lose_count);

                        new_state.setFirstServeWonDown(first_serve_won);
                        new_state.setFirstServeLostDown(first_serve_lost);
                        new_state.setSecondServeWonDown(second_serve_won);
                        new_state.setSecondServeLostDown(second_serve_lost);

                    } else { //oppt serve
                        Log.d(TAG, "oppt serve");
                        new_state.setFirstServeUp(first_serve_count);
                        new_state.setFirstServeMissUp(first_serve_miss);
                        new_state.setSecondServeUp(second_serve_count);

                        //win on your own
                        new_state.setForehandWinnerDown(forehand_winner_count);
                        new_state.setBackhandWinnerDown(backhand_winner_count);
                        new_state.setForehandVolleyDown(forehand_volley_count);
                        new_state.setBackhandVolleyDown(backhand_volley_count);
                        //win on oppt lose
                        new_state.setDoubleFaultUp(double_faults_count);
                        new_state.setUnforceErrorUp(unforced_errors_count);
                        new_state.setFoulToLoseUp(foul_to_lose_count);

                        new_state.setFirstServeWonUp(first_serve_won);
                        new_state.setFirstServeLostUp(first_serve_lost);
                        new_state.setSecondServeWonUp(second_serve_won);
                        new_state.setSecondServeLostUp(second_serve_lost);
                    }

                    new_state.setSet_point_down((byte) 0x01, (byte) 0x01);

                    Log.d(TAG, "=== I score end ===");
                    break;
                case OPPT_SCORE: //oppt score
                    Log.d(TAG, "=== Oppt score start ===");

                    if (imgServeDown.getVisibility() == View.VISIBLE) //you serve
                    {
                        Log.d(TAG, "you serve");
                        new_state.setFirstServeDown(first_serve_count);
                        new_state.setFirstServeMissDown(first_serve_miss);
                        new_state.setSecondServeDown(second_serve_count);

                        //win on oppt own
                        new_state.setForehandWinnerUp(forehand_winner_count);
                        new_state.setBackhandWinnerUp(backhand_winner_count);
                        new_state.setForehandVolleyUp(forehand_volley_count);
                        new_state.setBackhandVolleyUp(backhand_volley_count);
                        //win on you lose
                        new_state.setDoubleFaultDown(double_faults_count);
                        new_state.setUnforceErrorDown(unforced_errors_count);
                        new_state.setFoulToLoseDown(foul_to_lose_count);

                        new_state.setFirstServeWonDown(first_serve_won);
                        new_state.setFirstServeLostDown(first_serve_lost);
                        new_state.setSecondServeWonDown(second_serve_won);
                        new_state.setSecondServeLostDown(second_serve_lost);

                    } else { //oppt serve
                        Log.d(TAG, "oppt serve");
                        new_state.setFirstServeUp(first_serve_count);
                        new_state.setFirstServeMissUp(first_serve_miss);
                        new_state.setSecondServeUp(second_serve_count);

                        //win on oppt own
                        new_state.setAceCountUp(ace_count);
                        new_state.setForehandWinnerUp(forehand_winner_count);
                        new_state.setBackhandWinnerUp(backhand_winner_count);
                        new_state.setForehandVolleyUp(forehand_volley_count);
                        new_state.setBackhandVolleyUp(backhand_volley_count);
                        //win on you lose
                        new_state.setUnforceErrorDown(unforced_errors_count);
                        new_state.setFoulToLoseDown(foul_to_lose_count);

                        new_state.setFirstServeWonUp(first_serve_won);
                        new_state.setFirstServeLostUp(first_serve_lost);
                        new_state.setSecondServeWonUp(second_serve_won);
                        new_state.setSecondServeLostUp(second_serve_lost);
                    }

                    new_state.setSet_point_up((byte) 0x01, (byte) 0x01);



                    Log.d(TAG, "=== Oppt score end ===");
                    break;
            }

            if (new_state != null) {

                Log.d(TAG, "########## new state start ##########");
                Log.d(TAG, "current set : " + new_state.getCurrent_set());
                Log.d(TAG, "Serve : " + new_state.isServe());
                Log.d(TAG, "In tiebreak : " + new_state.isInTiebreak());
                Log.d(TAG, "Finish : " + new_state.isFinish());
                Log.d(TAG, "Second Serve : " + new_state.isSecondServe());
                //Log.d(TAG, "deuce : " + new_state.isDeuce());
                //Log.d(TAG, "Set Limit : "+ new_state.getSetLimit());
                Log.d(TAG, "Ace : up = "+new_state.getAceCountUp()+" down = "+new_state.getAceCountDown());
                Log.d(TAG, "First serve miss/count : up = "+new_state.getFirstServeMissUp()+"/"+new_state.getFirstServeUp()+
                        " down = "+new_state.getFirstServeMissDown()+"/"+new_state.getFirstServeDown());
                Log.d(TAG, "Second serve miss/count : up = "+new_state.getDoubleFaultUp()+"/"+new_state.getSecondServeUp()+
                        " down = "+new_state.getDoubleFaultDown()+"/"+new_state.getSecondServeDown());

                Log.d(TAG, "First serve lost/won : up = "+new_state.getFirstServeLostUp()+"/"+new_state.getFirstServeWonUp()+" down = "
                        +new_state.getFirstServeLostDown()+"/"+new_state.getFirstServeWonDown());
                Log.d(TAG, "Second serve lost/won : up = "+new_state.getSecondServeLostUp()+"/"+new_state.getSecondServeWonUp()+" down = "
                        +new_state.getSecondServeLostDown()+"/"+new_state.getSecondServeWonDown());
                Log.d(TAG, "======================");
                Log.d(TAG, "Unforced Error : up = "+new_state.getUnforceErrorUp()+ " down = "+new_state.getUnforceErrorDown());
                Log.d(TAG, "Forehand winner : up = "+new_state.getForehandWinnerUp()+ " down = "+new_state.getForehandWinnerDown());
                Log.d(TAG, "Backhand winner : up = "+new_state.getBackhandWinnerUp()+ " down = "+new_state.getBackhandWinnerDown());
                Log.d(TAG, "Forehand Volley : up = "+new_state.getForehandVolleyUp()+ " down = "+new_state.getForehandVolleyDown());
                Log.d(TAG, "Backhand Volley : up = "+new_state.getBackhandVolleyUp()+ " down = "+new_state.getBackhandVolleyDown());
                Log.d(TAG, "Foul to lose : up = "+new_state.getFoulToLoseUp()+ " down = "+new_state.getFoulToLoseDown());

                Log.d(TAG, "Set up : " + new_state.getSetsUp());
                Log.d(TAG, "set down : " + new_state.getSetsDown());

                for (int i = 1; i <= set_limit; i++) {
                    Log.d(TAG, "================================");
                    Log.d(TAG, "[set " + i + "]");
                    Log.d(TAG, "[Game : " + new_state.getSet_game_up((byte) i) + " / " + new_state.getSet_game_down((byte) i) + "]");
                    Log.d(TAG, "[Point : " + new_state.getSet_point_up((byte) i) + " / " + new_state.getSet_point_down((byte) i) + "]");
                    Log.d(TAG, "[tiebreak : " + new_state.getSet_tiebreak_point_up((byte) i) + " / " + new_state.getSet_tiebreak_point_down((byte) i) + "]");
                }

                Log.d(TAG, "########## new state end ##########");

                //then look up top state
                //State new_current_state = stack.peek();
                current_set = new_state.getCurrent_set();

                gameUp.setText(String.valueOf(new_state.getSet_game_up(current_set)));
                gameDown.setText(String.valueOf(new_state.getSet_game_down(current_set)));

                if (new_state.isServe()) {
                    imgServeUp.setVisibility(View.INVISIBLE);
                    imgServeDown.setVisibility(View.VISIBLE);
                } else {
                    imgServeUp.setVisibility(View.VISIBLE);
                    imgServeDown.setVisibility(View.INVISIBLE);
                }

                if (!new_state.isInTiebreak()) { //not in tiebreak
                    if (new_state.getSet_point_up(current_set) == 1) {
                        pointUp.setText(String.valueOf(15));
                    } else if (new_state.getSet_point_up(current_set) == 2) {
                        pointUp.setText(String.valueOf(30));
                    } else if (new_state.getSet_point_up(current_set) == 3) {
                        pointUp.setText(String.valueOf(40));
                    } else if (new_state.getSet_point_up(current_set) == 4) {
                        String msg = String.valueOf(40)+"A";
                        pointUp.setText(msg);
                    } else {
                        pointUp.setText("0");
                    }
                } else { //tie break;
                    pointUp.setText(String.valueOf(new_state.getSet_point_up(current_set)));
                }

                if (!new_state.isInTiebreak()) { //not in tiebreak
                    if (new_state.getSet_point_down(current_set) == 1) {
                        pointDown.setText(String.valueOf(15));
                    } else if (new_state.getSet_point_down(current_set) == 2) {
                        pointDown.setText(String.valueOf(30));
                    } else if (new_state.getSet_point_down(current_set) == 3) {
                        pointDown.setText(String.valueOf(40));
                    } else if (new_state.getSet_point_down(current_set) == 4) {
                        String msg = String.valueOf(40)+"A";
                        pointDown.setText(msg);
                    } else {
                        pointDown.setText("0");
                    }
                } else {
                    pointDown.setText(String.valueOf(new_state.getSet_point_down(current_set)));
                }



                //push into stack
                stack.push(new_state);
            }
        }

        //reset all zero
        ace_count = 0;
        double_faults_count = 0;
        unforced_errors_count = 0;
        forehand_winner_count = 0;
        backhand_winner_count = 0;
        forehand_volley_count = 0;
        backhand_volley_count = 0;
        foul_to_lose_count = 0;
        first_serve_count = 0;
        first_serve_miss = 0;
        second_serve_count = 0;

        first_serve_won = 0;
        first_serve_lost = 0;
        second_serve_won = 0;
        second_serve_lost = 0;
    }

    private void checkPoint(State new_state) {
        Log.d(TAG, "[Check point Start]");

        byte current_set = new_state.getCurrent_set();
        if (new_state.isInTiebreak()) { //in tiebreak
            Log.d(TAG, "[In Tiebreak]");
            byte game;

            /*if ((new_state.getSet_point_up(current_set) == 1 && new_state.getSet_point_down(current_set) == 0) ||
                    (new_state.getSet_point_up(current_set) == 0 && new_state.getSet_point_down(current_set) == 1)) {
                //in tiebreak, add first point should change serve
                //change serve
                if (new_state.isServe()) {
                    new_state.setServe(false);
                } else {
                    new_state.setServe(true);
                }
            } else */
            if (new_state.getSet_point_up(current_set) == 7 && new_state.getSet_point_down(current_set) <= 5) {
                //7 : 0,1,2,3,4,5 => oppt win this game
                //set tiebreak point
                new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                //set point clean
                new_state.setSet_point_up(current_set, (byte)0);
                new_state.setSet_point_down(current_set, (byte)0);
                //add to game
                game = new_state.getSet_game_up(current_set);
                game++;
                new_state.setSet_game_up(current_set, game);
                //change serve
                if (new_state.isServe()) {
                    new_state.setServe(false);
                } else {
                    new_state.setServe(true);
                }

                //leave tiebreak;
                new_state.setInTiebreak(false);
            } else if (new_state.getSet_point_up(current_set) <= 5 && new_state.getSet_point_down(current_set) == 7) {
                //0,1,2,3,4,5 : 7 => you win this game
                //set tiebreak point
                new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                //set point clean
                new_state.setSet_point_up(current_set, (byte)0);
                new_state.setSet_point_down(current_set, (byte)0);
                //add to game
                game = new_state.getSet_game_down(current_set);
                game++;
                new_state.setSet_game_down(current_set, game);
                //change serve
                if (new_state.isServe()) {
                    new_state.setServe(false);
                } else {
                    new_state.setServe(true);
                }
                //leave tiebreak;
                new_state.setInTiebreak(false);
            } else if (new_state.getSet_point_up(current_set) >= 6 &&
                    new_state.getSet_point_down(current_set) >= 6 &&
                    (new_state.getSet_point_up(current_set) - new_state.getSet_point_down(current_set)) == 2) {
                //8:6, 9:7, 10:8.... => oppt win this game
                //set tiebreak point
                new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                //set point clean
                new_state.setSet_point_up(current_set, (byte)0);
                new_state.setSet_point_down(current_set, (byte)0);
                //add to game
                game = new_state.getSet_game_up(current_set);
                game++;
                new_state.setSet_game_up(current_set, game);
                //change serve
                if (new_state.isServe()) {
                    new_state.setServe(false);
                } else {
                    new_state.setServe(true);
                }

                //leave tiebreak;
                new_state.setInTiebreak(false);
            } else if (new_state.getSet_point_up(current_set) >= 6 &&
                    new_state.getSet_point_down(current_set) >= 6 &&
                    (new_state.getSet_point_down(current_set) - new_state.getSet_point_up(current_set)) == 2) {
                //6:8, 7:9, 8:10.... => you win this game
                //set tiebreak point
                new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                //set point clean
                new_state.setSet_point_up(current_set, (byte)0);
                new_state.setSet_point_down(current_set, (byte)0);
                //add to game
                game = new_state.getSet_game_down(current_set);
                game++;
                new_state.setSet_game_down(current_set, game);
                //change serve
                if (new_state.isServe()) {
                    new_state.setServe(false);
                } else {
                    new_state.setServe(true);
                }

                //leave tiebreak;
                new_state.setInTiebreak(false);
            } else {
                Log.d(TAG, "Other tie break");

            }

            byte plus = (byte) (new_state.getSet_point_up(current_set)+new_state.getSet_point_down(current_set));

            if (plus%2 == 1) {
                //change serve
                Log.d(TAG, "==>Points plus become odd, change serve!");
                if (new_state.isServe()) {
                    new_state.setServe(false);
                } else {
                    new_state.setServe(true);
                }
            }

        } else { //not in tiebreak;
            Log.d(TAG, "[Not in Tiebreak]");
            if (deuce.equals("0")) { //use deuce
                Log.d(TAG, "[Game Using Deuce]");
                byte game;
                if (new_state.getSet_point_up(current_set) == 4 &&
                        new_state.getSet_point_down(current_set) ==4) { //40A:40A => 40:40
                    Log.d(TAG, "40A:40A => 40:40");

                    new_state.setSet_point_up(current_set, (byte)0x03);
                    new_state.setSet_point_down(current_set, (byte)0x03);

                    if (is_break_point) {
                        Log.d(TAG, "In break point");
                        if (new_state.isServe()) { //you serve
                            new_state.setBreakPointUp((byte)(new_state.getBreakPointUp()+1));
                            new_state.setBreakPointMissUp((byte)(new_state.getBreakPointMissUp()+1));
                        } else { //oppt serve
                            new_state.setBreakPointDown((byte)(new_state.getBreakPointDown()+1));
                            new_state.setBreakPointMissDown((byte)(new_state.getBreakPointMissDown()+1));
                        }
                    } else {
                        Log.d(TAG, "Not in break point");
                    }
                    is_break_point = false;
                } else if (new_state.getSet_point_up(current_set) == 5 &&
                        new_state.getSet_point_down(current_set) == 3) { //40A+ : 40 => oppt win this game
                    //set point clean
                    Log.d(TAG, "40A+1 : 40, => oppt win this game");
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);

                    if (is_break_point) {
                        if (new_state.isServe()) { //you serve
                            Log.d(TAG, "You serve, oppt got this break point");
                            new_state.setBreakPointUp((byte)(new_state.getBreakPointUp()+1));
                        } else { //oppt serve
                            Log.d(TAG, "Oppt serve");
                        }
                    }

                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    is_break_point = false;
                } else if (new_state.getSet_point_up(current_set) == 3 &&
                        new_state.getSet_point_down(current_set) == 5) { //40 : 40A+ => you win this game
                    Log.d(TAG, "40 : 40A+ => you win this game");
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);

                    if (is_break_point) {
                        if (new_state.isServe()) {
                            Log.d(TAG, "You serve");
                        } else {
                            Log.d(TAG, "Oppt serve, you got this break point");
                            new_state.setBreakPointDown((byte)(new_state.getBreakPointDown()+1));
                        }
                    }

                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    is_break_point = false;
                } else if (new_state.getSet_point_up(current_set) == 4 &&
                        new_state.getSet_point_down(current_set) <= 2) { //40A : 0, 40A : 15, 40A : 30 => oppt win this game
                    Log.d(TAG, "40A : 0, 40A : 15, 40A : 30 => oppt win this game");
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);

                    if (is_break_point) {
                        if (new_state.isServe()) { //you serve
                            Log.d(TAG, "you serve, oppt got this break point");
                            new_state.setBreakPointUp((byte)(new_state.getBreakPointUp()+1));
                        } else {
                            Log.d(TAG, "Oppt serve");
                        }
                    }

                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    is_break_point = false;
                } else if (new_state.getSet_point_up(current_set) <=2 &&
                        new_state.getSet_point_down(current_set) == 4) { //0 : 40A, 15 : 40A, 30: 40A => you win this game
                    Log.d(TAG, "0 : 40A, 15 : 40A, 30: 40A => you win this game");
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);

                    if (is_break_point) {
                        if (new_state.isServe()) { //you serve
                            Log.d(TAG, "You serve");
                        } else {
                            Log.d(TAG, "Oppt serve, you got this break point");
                            new_state.setBreakPointDown((byte)(new_state.getBreakPointDown()+1));
                        }
                    }

                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    is_break_point = false;
                }
                else {
                    Log.d(TAG, "[points change without arrange]");
                    if (new_state.getSet_point_up(current_set) == 3 &&
                            new_state.getSet_point_down(current_set) <= 2 && !is_break_point) { // 40:0, 40:15, 40:30

                        if (new_state.isServe()) {
                            Log.d(TAG, "You serve, Not int break point => In break point");
                            is_break_point = true;
                        } else {
                            Log.d(TAG, "Oppt serve");
                        }

                    } else if (new_state.getSet_point_up(current_set) <= 2 &&
                            new_state.getSet_point_down(current_set) == 3 && !is_break_point) { // 0:40, 15:40, 30:40

                        if (new_state.isServe()) {
                            Log.d(TAG, "You serve");
                        } else {
                            Log.d(TAG, "Oppt serve, Not int break point => In break point");
                            is_break_point = true;
                        }

                    } else if (new_state.getSet_point_up(current_set) == 4 &&
                            new_state.getSet_point_down(current_set) == 3 && !is_break_point) { // 40A:40

                        if (new_state.isServe()) {
                            Log.d(TAG, "You serve, Not int break point => In break point");
                            is_break_point = true;
                        } else {
                            Log.d(TAG, "Oppt serve");
                        }

                    } else if (new_state.getSet_point_up(current_set) == 3 &&
                            new_state.getSet_point_down(current_set) == 4 && !is_break_point) { // 40:40A

                        if (new_state.isServe()) {
                            Log.d(TAG, "You serve");
                        } else {
                            Log.d(TAG, "Oppt serve, Not int break point => In break point");
                            is_break_point = true;
                        }

                    } else if (new_state.getSet_point_up(current_set) == 3 &&
                            new_state.getSet_point_down(current_set) == 3) { //40:40
                        Log.d(TAG, "become deuce ");
                        if (is_break_point) { //in break point
                            if (new_state.isServe()) { //you serve
                                new_state.setBreakPointUp((byte)(new_state.getBreakPointUp()+1));
                                new_state.setBreakPointMissUp((byte)(new_state.getBreakPointMissUp()+1));
                            } else { //oppt serve
                                new_state.setBreakPointDown((byte)(new_state.getBreakPointDown()+1));
                                new_state.setBreakPointMissDown((byte)(new_state.getBreakPointMissDown()+1));
                            }
                        } else {
                            Log.d(TAG, "not in break point");
                        }

                        is_break_point = false;
                    } else { //other point 40:0 => 40:15, 40:15 => 40:30, 0:40=>15:40, 15:40=>30:40
                        if (is_break_point) { //in break point situation
                            Log.d(TAG, "In break point");
                            Log.d(TAG, "40:0 => 40:15, 40:15 => 40:30, 0:40=>15:40, 15:40=>30:40");
                            if (new_state.isServe()) { //you serve
                                new_state.setBreakPointUp((byte)(new_state.getBreakPointUp()+1));
                                new_state.setBreakPointMissUp((byte)(new_state.getBreakPointMissUp()+1));
                                Log.d(TAG, "miss/count ("+new_state.getBreakPointMissUp()+"/"+new_state.getBreakPointMissUp()+")");
                            } else { //oppt serve
                                new_state.setBreakPointDown((byte)(new_state.getBreakPointDown()+1));
                                new_state.setBreakPointMissDown((byte)(new_state.getBreakPointMissDown()+1));
                                Log.d(TAG, "miss/count ("+new_state.getBreakPointMissDown()+"/"+new_state.getBreakPointMissDown()+")");
                            }
                        } else {
                            Log.d(TAG, "Not In break point");
                        }
                    }
                }
            } else { //use deciding point
                byte game;
                if (new_state.getSet_point_up(current_set) == 4 &&
                        new_state.getSet_point_down(current_set) <= 3) { //40A : 40,30,15,0 => oppt win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                } else if (new_state.getSet_point_up(current_set) <= 3 &&
                        new_state.getSet_point_down(current_set) == 4) { //40,30,15,0 : 40A => you win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                } else {
                    Log.d(TAG, "[points change without arrange]");
                }
            }
        }

        //point change
        new_state.setSecondServe(false);


        Log.d(TAG, "current_set = "+current_set);
        Log.d(TAG, "[Check point End]");
    }

    private void checkGames(State new_state) {
        Log.d(TAG, "[Check Games Start]");
        byte current_set = new_state.getCurrent_set();
        byte setsWinUp = new_state.getSetsUp();
        byte setsWinDown = new_state.getSetsDown();
        if (tiebreak.equals("0")) { //use tibreak
            Log.d(TAG, "[Use Tiebreak]");

            if (new_state.getSet_game_up(current_set) == 6 &&
                    new_state.getSet_game_down(current_set) == 6) {
                new_state.setInTiebreak(true); //into tiebreak;
            } else if (new_state.getSet_game_up(current_set) == 7 &&
                    new_state.getSet_game_down(current_set) == 5) { // 7:5 => oppt win this set
                //set sets win
                setsWinUp++;
                new_state.setSetsUp(setsWinUp);
                checkSets(new_state);
            } else if (new_state.getSet_game_up(current_set) == 5 &&
                    new_state.getSet_game_down(current_set) == 7) { // 5:7 => you win this set
                //set sets win
                setsWinDown++;
                new_state.setSetsDown(setsWinDown);
                checkSets(new_state);
            } else if (new_state.getSet_game_up(current_set) == 7 &&
                    new_state.getSet_game_down(current_set) == 6) { // 7:6 => oppt win this set
                //set sets win
                setsWinUp++;
                new_state.setSetsUp(setsWinUp);
                checkSets(new_state);
            } else if (new_state.getSet_game_up(current_set) == 6 &&
                    new_state.getSet_game_down(current_set) == 7) { // 5:7 => you win this set
                //set sets win
                setsWinDown++;
                new_state.setSetsDown(setsWinDown);
                checkSets(new_state);
            } else if (new_state.getSet_game_up(current_set) == 6 &&
                    new_state.getSet_game_down(current_set) <=4 ) { // 6:0,1,2,3,4 => oppt win this set
                //set sets win
                setsWinUp++;
                new_state.setSetsUp(setsWinUp);
                checkSets(new_state);
            } else if (new_state.getSet_game_up(current_set) <= 4 &&
                    new_state.getSet_game_down(current_set) == 6) { // 0,1,2,3,4:6 => you win this set
                //set sets win
                setsWinDown++;
                new_state.setSetsDown(setsWinDown);
                checkSets(new_state);
            }
        } else {
            if (new_state.getSet_game_up(current_set) == 6 &&
                    new_state.getSet_game_down(current_set) <= 5) { // 6:5 => oppt win this set
                //set sets win
                setsWinUp++;
                new_state.setSetsUp(setsWinUp);
                checkSets(new_state);
            } else if (new_state.getSet_game_up(current_set) <= 5 &&
                    new_state.getSet_game_down(current_set) == 6) { // 5:6 => you win this set
                //set sets win
                setsWinDown++;
                new_state.setSetsDown(setsWinDown);
                checkSets(new_state);
            }
        }


        Log.d(TAG, "[Check Games End]");
    }

    private void checkSets(State new_state) {
        Log.d(TAG, "[Check sets Start]");
        //check if the game is over
        byte current_set = new_state.getCurrent_set();
        byte setsWinUp = new_state.getSetsUp();
        byte setsWinDown = new_state.getSetsDown();

        switch (set) {
            case "0":
                if (setsWinUp == 1 || setsWinDown == 1) {
                    new_state.setFinish(true);
                    handler.removeCallbacks(updateTimer);
                    is_pause = false;
                    imgPlayOrPause.setVisibility(View.GONE);
                }
                break;
            case "1":
                if (setsWinUp == 2 || setsWinDown == 2) {
                    new_state.setFinish(true);
                    handler.removeCallbacks(updateTimer);
                    is_pause = false;
                    imgPlayOrPause.setVisibility(View.GONE);
                } else { // new set
                    current_set++;
                    new_state.setCurrent_set(current_set);
                }
                break;
            case "2":
                if (setsWinUp == 3 || setsWinDown == 3) {
                    new_state.setFinish(true);
                    handler.removeCallbacks(updateTimer);
                    is_pause = false;
                    imgPlayOrPause.setVisibility(View.GONE);
                } else { // new set
                    current_set++;
                    new_state.setCurrent_set(current_set);
                }
                break;
            default:
                if (setsWinUp == 1 || setsWinDown == 1) {
                    new_state.setFinish(true);
                    handler.removeCallbacks(updateTimer);
                    is_pause = false;
                    imgPlayOrPause.setVisibility(View.GONE);
                }
                break;
        }



        Log.d(TAG, "[Check sets End]");
    }

    private Runnable updateTimer = new Runnable() {
        public void run() {
            //final TextView time = (TextView) findViewById(R.id.currentTime);
            NumberFormat f = new DecimalFormat("00");
            //Long spentTime = System.currentTimeMillis() - startTime;
            //計算目前已過分鐘數

            //計算目前已過秒數
            //Long seconds = (time_use) % 60;
            //time.setText(minius+":"+seconds);

            handler.postDelayed(this, 1000);
            time_use++;

            //Log.d(TAG, "time_use = "+time_use);

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            sdf.setTimeZone(tz);//set time zone.
            Date netDate = new Date(System.currentTimeMillis());
            //Date gameDate = new Date(spentTime);
            Long hour = (time_use)/3600;
            Long min = (time_use)%3600/60;
            Long sec = (time_use)%60;
            textCurrentTime.setText(sdf.format(netDate));
            textGameTime.setText(f.format(hour)+":"+f.format(min)+":"+f.format(sec));

            //textGameTime.setText(sdf.format(gameDate));
        }
    };

    /*public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }*/

    @Override
    public void onBackPressed() {
        //clear
        clear_record(filename);

        boolean is_tiebreak;
        boolean is_deuce;
        boolean is_firstServe;

        switch (tiebreak) {
            case "0":
                is_tiebreak = true;
                break;
            default:
                is_tiebreak = false;
        }

        /*if (tiebreak.equals("0")) {
            is_tiebreak = true;
        } else {
            is_tiebreak = false;
        }*/

        switch (deuce) {
            case "0":
                is_deuce = true;
                break;
            default:
                is_deuce = false;
        }

        /*if (deuce.equals("0")) {
            is_deuce = true;
        } else {
            is_deuce = false;
        }*/

        switch (serve) {
            case "0":
                is_firstServe = true;
                break;
            default:
                is_firstServe = false;
        }

        /*if (serve.equals("0")) {
            is_firstserve = true;
        } else {
            is_firstserve = false;
        }*/

        String msg = playerUp + ";" + playerDown + ";" + is_tiebreak + ";" + is_deuce + ";" +is_firstServe+ ";" +set+ "|";
        append_record(msg, filename);

        State top = stack.peek();
        if (top != null) {
            top.setDuration(time_use);

            int i = 0;
            //load stack
            for (State s : stack) {

                if (i >= 1) {
                    append_record("&", filename);
                }


                String append_msg = s.getCurrent_set() + ";"
                        + s.isServe() + ";"
                        + s.isInTiebreak() + ";"
                        + s.isFinish() + ";"
                        + s.isSecondServe() + ";"
                        + s.isInBreakPoint() + ";"
                        + s.getSetsUp() + ";"
                        + s.getSetsDown() + ";"
                        + s.getDuration() + ";"
                        + s.getAceCountUp() + ";"
                        + s.getAceCountDown() + ";"
                        + s.getFirstServeUp() + ";"
                        + s.getFirstServeDown() + ";"
                        + s.getFirstServeMissUp() + ";"
                        + s.getFirstServeMissDown() + ";"
                        + s.getSecondServeUp() + ";"
                        + s.getSecondServeDown() + ";"
                        + s.getBreakPointUp() + ";"
                        + s.getBreakPointDown() + ";"
                        + s.getBreakPointMissUp() + ";"
                        + s.getBreakPointMissDown() + ";"
                        + s.getFirstServeWonUp() + ";"
                        + s.getFirstServeWonDown() + ";"
                        + s.getFirstServeLostUp() + ";"
                        + s.getFirstServeLostDown() + ";"
                        + s.getSecondServeWonUp() + ";"
                        + s.getSecondServeWonDown() + ";"
                        + s.getSecondServeLostUp() + ";"
                        + s.getSecondServeLostDown() + ";"
                        + s.getDoubleFaultUp() + ";"
                        + s.getDoubleFaultDown() + ";"
                        + s.getUnforceErrorUp() + ";"
                        + s.getUnforceErrorDown() + ";"
                        + s.getForehandWinnerUp() + ";"
                        + s.getForehandWinnerDown() + ";"
                        + s.getBackhandWinnerUp() + ";"
                        + s.getBackhandWinnerDown() + ";"
                        + s.getForehandVolleyUp() + ";"
                        + s.getForehandVolleyDown() + ";"
                        + s.getBackhandVolleyUp() + ";"
                        + s.getBackhandVolleyDown() + ";"
                        + s.getFoulToLoseUp() + ";"
                        + s.getFoulToLoseDown() + ";"
                        + s.getSet_game_up((byte) 0x1) + ";"
                        + s.getSet_game_down((byte) 0x1) + ";"
                        + s.getSet_point_up((byte) 0x1) + ";"
                        + s.getSet_point_down((byte) 0x1) + ";"
                        + s.getSet_tiebreak_point_up((byte) 0x1) + ";"
                        + s.getSet_tiebreak_point_down((byte) 0x1) + ";"
                        + s.getSet_game_up((byte) 0x2) + ";"
                        + s.getSet_game_down((byte) 0x2) + ";"
                        + s.getSet_point_up((byte) 0x2) + ";"
                        + s.getSet_point_down((byte) 0x2) + ";"
                        + s.getSet_tiebreak_point_up((byte) 0x2) + ";"
                        + s.getSet_tiebreak_point_down((byte) 0x2) + ";"
                        + s.getSet_game_up((byte) 0x3) + ";"
                        + s.getSet_game_down((byte) 0x3) + ";"
                        + s.getSet_point_up((byte) 0x3) + ";"
                        + s.getSet_point_down((byte) 0x3) + ";"
                        + s.getSet_tiebreak_point_up((byte) 0x3) + ";"
                        + s.getSet_tiebreak_point_down((byte) 0x3) + ";"
                        + s.getSet_game_up((byte) 0x4) + ";"
                        + s.getSet_game_down((byte) 0x4) + ";"
                        + s.getSet_point_up((byte) 0x4) + ";"
                        + s.getSet_point_down((byte) 0x4) + ";"
                        + s.getSet_tiebreak_point_up((byte) 0x4) + ";"
                        + s.getSet_tiebreak_point_down((byte) 0x4) + ";"
                        + s.getSet_game_up((byte) 0x5) + ";"
                        + s.getSet_game_down((byte) 0x5) + ";"
                        + s.getSet_point_up((byte) 0x5) + ";"
                        + s.getSet_point_down((byte) 0x5) + ";"
                        + s.getSet_tiebreak_point_up((byte) 0x5) + ";"
                        + s.getSet_tiebreak_point_down((byte) 0x5);
                append_record(append_msg, filename);
                i++;
            }
        }

        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.

        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                if (mChatService != null) {
                    // Only if the state is STATE_NONE, do we know that we haven't started already
                    if (mChatService.getState() == BluetoothService.STATE_NONE) {
                        // Start the Bluetooth chat services
                        mChatService.start();
                    }
                } else {
                    Log.d(TAG, "mChatService = null");
                    setupChat();
                }
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.game_activity_menu, menu);

        item_bluetooth = menu.findItem(R.id.action_bluetooth);

        if (mBluetoothAdapter == null)
            item_bluetooth.setVisible(false);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_show_stat:
                intent = new Intent(GameActivity.this, CurrentStatActivity.class);
                intent.putExtra("PLAYER_UP", playerUp);
                intent.putExtra("PLAYER_DOWN", playerDown);
                startActivity(intent);
                break;
            case R.id.action_bluetooth:

                if (!mBluetoothAdapter.isEnabled()) {
                    intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                    // Otherwise, setup the chat session
                } else if (mChatService == null) {
                    setupChat();
                } else {
                    intent = new Intent(GameActivity.this, DeviceListActivity.class);
                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE_SECURE);
                }


                break;

            default:
                break;
        }
        return true;
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //FragmentActivity activity = getActivity();
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE");
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Log.d(TAG, "STATE_CONNECTED");
                            //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Log.d(TAG, "STATE_CONNECTING");
                            //setStatus(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                            Log.d(TAG, "STATE_LISTEN");
                            break;
                        case BluetoothService.STATE_NONE:
                            Log.d(TAG, "STATE_NONE");
                            //setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE");
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d(TAG, "MESSAGE_READ : "+readMessage);
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(GameActivity.this, "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(GameActivity.this, msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Bluetooth was not enabled.",
                            Toast.LENGTH_SHORT).show();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        // = new ArrayAdapter<>(this, R.layout.message);

        //mConversationView.setAdapter(mConversationArrayAdapter);

        /*
        // Initialize the compose field with a listener for the return key
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                if (null != view) {
                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
                    String message = textView.getText().toString();
                    sendMessage(message);
                }
            }
        });*/

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    double Accel2mms(double accel, double freq){
        double result = 0;
        result = (gravity*accel)/(2*PI*freq);
        return result;
    }
}
