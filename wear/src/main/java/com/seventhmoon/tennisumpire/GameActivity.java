package com.seventhmoon.tennisumpire;


import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.seventhmoon.tennisumpire.Bluetooth.BluetoothService;
import com.seventhmoon.tennisumpire.Data.InitData;
import com.seventhmoon.tennisumpire.Data.State;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static com.seventhmoon.tennisumpire.Bluetooth.Data.Constants.DEVICE_NAME;
import static com.seventhmoon.tennisumpire.Bluetooth.Data.Constants.MESSAGE_DEVICE_NAME;
import static com.seventhmoon.tennisumpire.Bluetooth.Data.Constants.MESSAGE_READ;
import static com.seventhmoon.tennisumpire.Bluetooth.Data.Constants.MESSAGE_STATE_CHANGE;
import static com.seventhmoon.tennisumpire.Bluetooth.Data.Constants.MESSAGE_TOAST;
import static com.seventhmoon.tennisumpire.Bluetooth.Data.Constants.MESSAGE_WRITE;
import static com.seventhmoon.tennisumpire.Bluetooth.Data.Constants.TOAST;
import static com.seventhmoon.tennisumpire.Data.InitData.accelerometerListener;
import static com.seventhmoon.tennisumpire.Data.InitData.mBluetoothAdapter;
import static com.seventhmoon.tennisumpire.Data.InitData.mChatService;
import static com.seventhmoon.tennisumpire.Data.InitData.mConnectedDeviceName;
import static com.seventhmoon.tennisumpire.Data.InitData.mLinearAcceration;
import static com.seventhmoon.tennisumpire.Data.InitData.mOutStringBuffer;
import static com.seventhmoon.tennisumpire.Data.InitData.mRotationVector;
import static com.seventhmoon.tennisumpire.Data.InitData.mSensorManager;
import static com.seventhmoon.tennisumpire.Data.InitData.mStepCounter;
import static com.seventhmoon.tennisumpire.Data.InitData.rotationVectorListener;
import static com.seventhmoon.tennisumpire.Data.InitData.stepCountListener;
import static java.lang.Math.sqrt;


public class GameActivity extends WearableActivity {
    private static final String TAG = GameActivity.class.getName();

    //private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
    //        new SimpleDateFormat("HH:mm", Locale.TAIWAN);

    //private BoxInsetLayout mContainerView;

    private TextView gameUp;
    private TextView gameDown;
    private TextView pointUp;
    private TextView pointDown;
    private ImageView imgServeUp;
    private ImageView imgServeDown;

    //private TextView mClockView;


    private TextView textCurrentTime;
    private TextView textGameTime;
    private ImageView imgPlayOrPause;

    private static String set;
    //private static String game;
    private static String tiebreak;
    private static String deuce;
    private static String serve;
    private static long startTime;
    private static Handler handler;
    private static long time_use = 0;
    private static boolean is_pause = false;

    private static Deque<State> stack = new ArrayDeque<>();

    private static long previous_time = 0;
    private static long current_time = 0;
    private static double x_previous_velocity = 0.0;
    private static double y_previous_velocity = 0.0;
    private static double x_current_velocity = 0.0;
    private static double y_current_velocity = 0.0;
    private static double previous_accel = 0.0;
    private static double current_accel = 0.0;

    private static long x_coordinate_current = 0;
    private static long y_coordinate_current = 0;
    private static long x_coordinate_previous = 0;
    private static long y_coordinate_previous = 0;
    private static double distance = 0;

    //step
    private static float step_count_start = 0;
    private static float step_count_end = 0;
    private static boolean is_first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        InitData.is_running = true;

        Button btnYouScore;
        ImageView btnImgBack;
        Button btnOpptScore;
        ImageView btnImgReset;

        setAmbientEnabled();

        handler = new Handler();

        startTime = System.currentTimeMillis();

        handler.removeCallbacks(updateTimer);
        handler.postDelayed(updateTimer, 1000);

        Intent intent = getIntent();

        set = intent.getStringExtra("SETUP_SET");
        tiebreak = intent.getStringExtra("SETUP_TIEBREAK");
        deuce = intent.getStringExtra("SETUP_DEUCE");
        serve = intent.getStringExtra("SETUP_SERVE");

        Log.e(TAG, "SET = "+set);
        Log.e(TAG, "TIEBREAK = "+tiebreak);
        Log.e(TAG, "DEUCE = "+deuce);
        Log.e(TAG, "SERVE = "+serve);

        //mContainerView = (BoxInsetLayout) findViewById(R.id.container);

        gameUp = (TextView) findViewById(R.id.textViewGameUp);
        gameDown = (TextView) findViewById(R.id.textViewGameDown);
        pointUp = (TextView) findViewById(R.id.textViewPointUp);
        pointDown = (TextView) findViewById(R.id.textViewPointDown);

        imgServeUp = (ImageView) findViewById(R.id.imageViewServeUp);
        imgServeDown = (ImageView) findViewById(R.id.imageViewServeDown);

        textCurrentTime = (TextView) findViewById(R.id.currentTime);
        textGameTime = (TextView) findViewById(R.id.gameTime);

        imgPlayOrPause = (ImageView) findViewById(R.id.imagePlayOrPause);

        //init score board
        gameUp.setText("0");
        gameDown.setText("0");
        pointUp.setText("0");
        pointDown.setText("0");

        if (serve.equals("0")) { //you server first
            imgServeUp.setVisibility(View.INVISIBLE);
            imgServeDown.setVisibility(View.VISIBLE);
        } else {
            imgServeUp.setVisibility(View.VISIBLE);
            imgServeDown.setVisibility(View.INVISIBLE);
        }



        //mClockView = (TextView) findViewById(R.id.clock);

        btnYouScore = (Button) findViewById(R.id.btnYouScore);
        btnOpptScore = (Button) findViewById(R.id.btnOpptScore);
        btnImgBack = (ImageView) findViewById(R.id.btnImgBack);
        btnImgReset = (ImageView) findViewById(R.id.btnImgReset);

        btnImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                is_pause = false;
                imgPlayOrPause.setVisibility(View.VISIBLE);
                imgPlayOrPause.setImageResource(R.drawable.ic_pause_white_48dp);

                if (stack.isEmpty()) {
                    Log.d(TAG, "stack is empty!");

                } else {
                    //send back
                    String message = "command|back";
                    byte[] send = message.getBytes();
                    if (mChatService != null) {
                        mChatService.write(send);

                        // Reset out string buffer to zero and clear the edit text field
                        mOutStringBuffer.setLength(0);
                    }

                    byte current_set;
                    //stack.pop();
                    if (stack.pop() != null) { //pop out current
                        State back_state = stack.peek();
                        if (back_state != null) {
                            current_set = back_state.getCurrent_set();

                            gameUp.setText(String.valueOf(back_state.getSet_game_up(current_set)));
                            gameDown.setText(String.valueOf(back_state.getSet_game_down(current_set)));

                            if (back_state.isServe()) {
                                imgServeUp.setVisibility(View.INVISIBLE);
                                imgServeDown.setVisibility(View.VISIBLE);
                            } else {
                                imgServeUp.setVisibility(View.VISIBLE);
                                imgServeDown.setVisibility(View.INVISIBLE);
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

                            Log.d(TAG, "########## back state start ##########");
                            Log.d(TAG, "current set : " + back_state.getCurrent_set());
                            Log.d(TAG, "Serve : " + back_state.isServe());
                            Log.d(TAG, "In tiebreak : " + back_state.isInTiebreak());
                            Log.d(TAG, "Finish : " + back_state.isFinish());

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
                }
            }
        });

        btnYouScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateScore(true);
                String message = "command|you";
                byte[] send = message.getBytes();
                if (mChatService != null) {
                    mChatService.write(send);

                    // Reset out string buffer to zero and clear the edit text field
                    mOutStringBuffer.setLength(0);
                }
            }
        });

        btnOpptScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateScore(false);
                String message = "command|oppt";
                byte[] send = message.getBytes();
                if (mChatService != null) {
                    mChatService.write(send);

                    // Reset out string buffer to zero and clear the edit text field
                    mOutStringBuffer.setLength(0);
                }
            }
        });

        btnImgReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_use = 0;
                stack.clear();
                handler.removeCallbacks(updateTimer);

                //send reset
                String message = "command|reset";
                byte[] send = message.getBytes();
                if (mChatService != null) {
                    mChatService.write(send);

                    // Reset out string buffer to zero and clear the edit text field
                    mOutStringBuffer.setLength(0);
                }

                Intent intent = new Intent(GameActivity.this, SetupMain.class);
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

        if (mBluetoothAdapter.isEnabled()) {

            if (mChatService == null) {
                setupChat();
            }

            if (mChatService != null) {
                // Get a set of currently paired devices
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                String address = "";

                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice devices : pairedDevices) {
                        Log.d(TAG, "device address : "+devices.getAddress());
                        address = devices.getAddress();
                        break;
                    }

                    if (!address.equals("")) {
                        connectDevice(true, address);
                    }
                } else {
                    Log.e(TAG, "No paired devices");
                }
            }
        }

        //sensor

        previous_time = 0;
        current_time = 0;
        x_previous_velocity = 0.0;
        y_previous_velocity = 0.0;
        x_current_velocity = 0.0;
        y_current_velocity = 0.0;
        previous_accel = 0.0;
        current_accel = 0.0;

        x_coordinate_current = 0;
        y_coordinate_current = 0;

        distance = 0.0;

        step_count_start = 0;

        //linear accelerometer
        accelerometerListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                double time;

                current_time = System.currentTimeMillis();
                current_accel = sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]);

                if (previous_time != 0) {
                    time = (((double) (current_time - previous_time)) / 1000);

                    x_coordinate_current = (long)(event.values[0] * time * time * 100);
                    y_coordinate_current = (long)(event.values[1] * time * time * 100);
                    x_current_velocity =  (x_coordinate_current - x_coordinate_previous)/time ;
                    y_current_velocity =  (y_coordinate_current - y_coordinate_previous)/time;

                    double velocity = current_accel * time;
                    if (x_coordinate_current != 0 && y_coordinate_current != 0) {
                        //distance = distance + velocity * time;
                        distance = distance + sqrt(x_current_velocity*x_current_velocity+y_current_velocity*y_current_velocity) * time;
                        //Log.d(TAG, "distance = "+distance);
                    }


                    /*
                    String logMsg = "(" +  x_coordinate_current + ", " +  y_coordinate_current +
                            ")" +
                            " vX=" +
                            String.format("%.3f", x_current_velocity) +
                            " vY=" +
                            String.format("%.3f", y_current_velocity) +
                            " aX=" +
                            String.format("%.3f", event.values[0] * 100) +
                            " aY=" +
                            String.format("%.3f", event.values[1] * 100) +
                            " sec = " +
                            time +
                            " d = " +
                            String.format("%.2f", distance / 100.0)+"M" ;

                    Log.d(TAG, logMsg);

                    byte[] send = logMsg.getBytes();
                    if (mChatService != null) {
                        mChatService.write(send);

                        // Reset out string buffer to zero and clear the edit text field
                        mOutStringBuffer.setLength(0);
                    }*/

                }

                previous_time = current_time;
                previous_accel = current_accel;
                //x_previous_velocity = x_current_velocity;
                //y_previous_velocity = y_current_velocity;
                x_coordinate_previous = x_coordinate_current;
                y_coordinate_previous = y_coordinate_current;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {


            }
        };

        mSensorManager.registerListener(accelerometerListener, mLinearAcceration, SensorManager.SENSOR_DELAY_NORMAL);

        //rotation
        rotationVectorListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                //Log.d(TAG, "x : "+event.values[0]+" y : "+event.values[1]+" z : "+event.values[2]+" scalar : "+event.values[3]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        mSensorManager.registerListener(rotationVectorListener, mRotationVector, SensorManager.SENSOR_DELAY_NORMAL);

        //step count
        stepCountListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (is_first) {
                    Log.d(TAG, "first, "+event.values[0]);
                    step_count_start = event.values[0];
                    is_first = false;
                } else {
                    step_count_end = event.values[0];
                    Log.d(TAG, "step count = "+(step_count_end - step_count_start));
                    /*String logMsg = "count = "+(step_count_end - step_count_start);
                    byte[] send = logMsg.getBytes();
                    if (mChatService != null) {
                        mChatService.write(send);

                        // Reset out string buffer to zero and clear the edit text field
                        mOutStringBuffer.setLength(0);
                    }*/
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        mSensorManager.registerListener(stepCountListener, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

        //is_pause = true;
        //imgPlayOrPause.setImageResource(R.drawable.ic_play_arrow_white_48dp);
        //handler.removeCallbacks(updateTimer);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        //is_pause = false;
        //imgPlayOrPause.setImageResource(R.drawable.ic_pause_white_48dp);
        //handler.removeCallbacks(updateTimer);
        //handler.postDelayed(updateTimer, 1000);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        /*if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            //mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.VISIBLE);

            //mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mClockView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }*/


    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        time_use = 0;
        stack.clear();
        handler.removeCallbacks(updateTimer);
        InitData.is_running = false;


        if (mChatService != null)
            mChatService.stop();
        mChatService = null;

        //mSensorManager.unregisterListener(accelerometerListener);
        //mSensorManager.unregisterListener(rotationVectorListener);

        super.onDestroy();

    }

    private void calculateScore(boolean you_score) {
        byte current_set = 0;
        State new_state=null;
        //load top state first
        State current_state = stack.peek();

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
            Log.d(TAG, "======================");

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
                handler.removeCallbacks(updateTimer);

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


                startActivity(intent);
            } else { //not finish
                if (is_pause) { //
                    is_pause = false;
                    imgPlayOrPause.setImageResource(R.drawable.ic_pause_white_48dp);
                    handler.removeCallbacks(updateTimer);
                    handler.postDelayed(updateTimer, 1000);
                }
                Log.d(TAG, "*** Game is running ***");
                if (you_score) {
                    Log.d(TAG, "=== I score start ===");

                    if (stack.isEmpty()) { //the state stack is empty
                        new_state = new State();
                        new_state.setWho_win_this_point(true);
                        Log.d(TAG, "==>[Stack empty]");
                        if (serve.equals("0"))
                            new_state.setServe(true);
                        else
                            new_state.setServe(false);

                        //set current set = 1
                        new_state.setCurrent_set((byte) 0x01);

                        new_state.setSet_point_down((byte) 0x01, (byte) 0x01);
                        //new_state.setSet_1_point_down((byte)0x01);


                        //Log.e(TAG, "get_set_1_point_down = "+new_state.getSet_1_point_down()+", isServe ? "+ (new_state.isServe() ? "YES" : "NO"));
                    } else {
                        Log.d(TAG, "==>[Stack not empty]");

                        if (current_state.isFinish()) {
                            Log.d(TAG, "**** Game Finish ****");
                        } else {
                            new_state = new State();
                            new_state.setWho_win_this_point(true);
                            //new_state = stack.peek();
                            // copy previous state;
                            new_state.setCurrent_set(current_state.getCurrent_set());
                            new_state.setServe(current_state.isServe());
                            new_state.setInTiebreak(current_state.isInTiebreak());
                            new_state.setFinish(current_state.isFinish());
                            new_state.setSetsUp(current_state.getSetsUp());
                            new_state.setSetsDown(current_state.getSetsDown());

                            for (byte i=1; i<=set_limit; i++) {
                                new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                            }


                            //you score!
                            byte point = current_state.getSet_point_down(current_set);
                            Log.d(TAG, "Your point " + point + " change to " + (++point));
                            new_state.setSet_point_down(current_set, point);

                            checkPoint(new_state);

                            checkGames(new_state);
                        }
                    }

                    Log.d(TAG, "=== I score end ===");
                } else {
                    Log.d(TAG, "=== Oppt score start ===");
                    if (stack.isEmpty()) { //the state stack is empty
                        new_state = new State();
                        new_state.setWho_win_this_point(false);
                        Log.d(TAG, "==>[Stack empty]");
                        if (serve.equals("0"))
                            new_state.setServe(true);
                        else
                            new_state.setServe(false);

                        //set current set = 1
                        new_state.setCurrent_set((byte) 0x01);

                        new_state.setSet_point_up((byte) 0x01, (byte) 0x01);

                        //Log.e(TAG, "get_set_1_point_up = "+new_state.getSet_1_point_up()+", isServe ? "+ (new_state.isServe() ? "YES" : "NO"));

                    } else {
                        Log.d(TAG, "==>[Stack not empty]");
                        if (current_state.isFinish()) {
                            Log.d(TAG, "**** Game Finish ****");
                        } else {
                            new_state = new State();
                            new_state.setWho_win_this_point(false);
                            //new_state = stack.peek();
                            // copy previous state;
                            new_state.setCurrent_set(current_state.getCurrent_set());
                            new_state.setServe(current_state.isServe());
                            new_state.setInTiebreak(current_state.isInTiebreak());
                            new_state.setFinish(current_state.isFinish());
                            new_state.setSetsUp(current_state.getSetsUp());
                            new_state.setSetsDown(current_state.getSetsDown());

                            for (byte i=1; i<=set_limit; i++) {
                                new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                            }

                            //oppt score!
                            byte point = current_state.getSet_point_up(current_set);
                            Log.d(TAG, "Opponent point " + point + " change to " + (++point));
                            new_state.setSet_point_up(current_set, point);

                            checkPoint(new_state);

                            checkGames(new_state);
                        }
                    }
                    Log.d(TAG, "=== Oppt score end ===");
                }

                if (new_state != null) {

                    Log.d(TAG, "########## new state start ##########");
                    Log.d(TAG, "current set : " + new_state.getCurrent_set());
                    Log.d(TAG, "Serve : " + new_state.isServe());
                    Log.d(TAG, "In tiebreak : " + new_state.isInTiebreak());
                    Log.d(TAG, "Finish : " + new_state.isFinish());

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






            }
        } else {
            Log.d(TAG, "Stack is empty!");

            if (is_pause) { //
                is_pause = false;
                imgPlayOrPause.setImageResource(R.drawable.ic_pause_white_48dp);
                handler.removeCallbacks(updateTimer);
                handler.postDelayed(updateTimer, 1000);
            }
            Log.d(TAG, "*** Game is running ***");
            if (you_score) {
                Log.d(TAG, "=== I score start ===");

                //if (stack.isEmpty()) { //the state stack is empty
                    new_state = new State();
                    new_state.setWho_win_this_point(true);
                    Log.d(TAG, "==>[Stack empty]");
                    if (serve.equals("0"))
                        new_state.setServe(true);
                    else
                        new_state.setServe(false);

                    //set current set = 1
                    new_state.setCurrent_set((byte) 0x01);

                    new_state.setSet_point_down((byte) 0x01, (byte) 0x01);
                    //new_state.setSet_1_point_down((byte)0x01);


                    //Log.e(TAG, "get_set_1_point_down = "+new_state.getSet_1_point_down()+", isServe ? "+ (new_state.isServe() ? "YES" : "NO"));
                //}

                Log.d(TAG, "=== I score end ===");
            } else {
                Log.d(TAG, "=== Oppt score start ===");
                //if (stack.isEmpty()) { //the state stack is empty
                    new_state = new State();
                    new_state.setWho_win_this_point(false);
                    Log.d(TAG, "==>[Stack empty]");
                    if (serve.equals("0"))
                        new_state.setServe(true);
                    else
                        new_state.setServe(false);

                    //set current set = 1
                    new_state.setCurrent_set((byte) 0x01);

                    new_state.setSet_point_up((byte) 0x01, (byte) 0x01);

                    //Log.e(TAG, "get_set_1_point_up = "+new_state.getSet_1_point_up()+", isServe ? "+ (new_state.isServe() ? "YES" : "NO"));

                //}
                Log.d(TAG, "=== Oppt score end ===");
            }

            if (new_state != null) {

                Log.d(TAG, "########## new state start ##########");
                Log.d(TAG, "current set : " + new_state.getCurrent_set());
                Log.d(TAG, "Serve : " + new_state.isServe());
                Log.d(TAG, "In tiebreak : " + new_state.isInTiebreak());
                Log.d(TAG, "Finish : " + new_state.isFinish());

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
                byte game;
                if (new_state.getSet_point_up(current_set) == 4 &&
                        new_state.getSet_point_down(current_set) ==4) { //40A:40A => 40:40
                    new_state.setSet_point_up(current_set, (byte)0x03);
                    new_state.setSet_point_down(current_set, (byte)0x03);
                } else if (new_state.getSet_point_up(current_set) == 5 &&
                        new_state.getSet_point_down(current_set) == 3) { //40A+ : 40 => oppt win this game
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

                } else if (new_state.getSet_point_up(current_set) == 3 &&
                        new_state.getSet_point_down(current_set) == 5) { //40 : 40A+ => you win this game
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
                } else if (new_state.getSet_point_up(current_set) == 4 &&
                        new_state.getSet_point_down(current_set) <= 2) { //40A : 0, 40A : 15, 40A : 30 => oppt win this game
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
                } else if (new_state.getSet_point_up(current_set) <=2 &&
                        new_state.getSet_point_down(current_set) == 4) { //0 : 40A, 15 : 40A, 30: 40A => you win this game
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
                }
                else {
                    Log.d(TAG, "[points change without arrange]");
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
                }
                handler.removeCallbacks(updateTimer);
                is_pause = false;
                imgPlayOrPause.setVisibility(View.GONE);
                break;
            case "1":
                if (setsWinUp == 2 || setsWinDown == 2) {
                    new_state.setFinish(true);
                } else { // new set
                    current_set++;
                    new_state.setCurrent_set(current_set);
                }
                handler.removeCallbacks(updateTimer);
                is_pause = false;
                imgPlayOrPause.setVisibility(View.GONE);
                break;
            case "2":
                if (setsWinUp == 3 || setsWinDown == 3) {
                    new_state.setFinish(true);
                } else { // new set
                    current_set++;
                    new_state.setCurrent_set(current_set);
                }
                handler.removeCallbacks(updateTimer);
                is_pause = false;
                imgPlayOrPause.setVisibility(View.GONE);
                break;
            default:
                if (setsWinUp == 1 || setsWinDown == 1) {
                    new_state.setFinish(true);
                }
                handler.removeCallbacks(updateTimer);
                is_pause = false;
                imgPlayOrPause.setVisibility(View.GONE);
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

            Log.d(TAG, "time_use = "+time_use);

            if (time_use % 10 == 0) {

                if (!stack.isEmpty()) {
                    /*
                        set = intent.getStringExtra("SETUP_SET");
                        tiebreak = intent.getStringExtra("SETUP_TIEBREAK");
                        deuce = intent.getStringExtra("SETUP_DEUCE");
                        serve = intent.getStringExtra("SETUP_SERVE");
                     */

                    String message = "command|calibrate&"+set+"&"+tiebreak+"&"+deuce+"&"+serve+"&";

                    String state_msg = "";
                    for (State s : stack) {
                        if (state_msg.equals(""))
                            state_msg = s.getWho_win_this_point() +"";
                        else
                            state_msg = s.getWho_win_this_point() +";"+state_msg;
                    }
                    message = message + state_msg;
                    byte[] send = message.getBytes();
                    if (mChatService != null) {
                        mChatService.write(send);

                        // Reset out string buffer to zero and clear the edit text field
                        mOutStringBuffer.setLength(0);
                    }
                }
            }

            /*
            String message = "command|oppt";
                byte[] send = message.getBytes();
                if (mChatService != null) {
                    mChatService.write(send);

                    // Reset out string buffer to zero and clear the edit text field
                    mOutStringBuffer.setLength(0);
                }
             */

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

                            String message = "command|init;"+set+";"+tiebreak+";"+deuce+";"+serve;
                            byte[] send = message.getBytes();
                            if (mChatService != null) {
                                mChatService.write(send);

                                // Reset out string buffer to zero and clear the edit text field
                                mOutStringBuffer.setLength(0);
                            }


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

    private void connectDevice(boolean secure, String address) {
        // Get the device MAC address
        //String address = data.getExtras()
        //        .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        Log.d(TAG, "connectDevice");
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
}
