package com.seventhmoon.tennisumpire;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seventhmoon.tennisumpire.Bluetooth.BluetoothService;
import com.seventhmoon.tennisumpire.Data.State;
import com.seventhmoon.tennisumpire.Data.StateAction;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;

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
import static com.seventhmoon.tennisumpire.Data.FileOperation.clear_record;
import static com.seventhmoon.tennisumpire.Data.InitData.mBluetoothAdapter;
import static com.seventhmoon.tennisumpire.Data.InitData.mChatService;
import static com.seventhmoon.tennisumpire.Data.InitData.mConnectedDeviceName;
import static com.seventhmoon.tennisumpire.Data.InitData.mOutStringBuffer;

public class WearModeGameActivity extends AppCompatActivity {
    private static final String TAG = WearModeGameActivity.class.getName();

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    LinearLayout nameLayout;
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
    private TextView stepAndDistance;

    //private TextView mClockView;

    private static String set;
    //private static String game;
    private static String tiebreak;
    private static String deuce;
    private static String serve;
    //private static String duration;

    private static String filename;
    private static String playerUp;
    private static String playerDown;

    private static String mode;

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
    private MenuItem item_stat;





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
    private static String steps_count;
    private static String distance_run;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wear_mode_game_activity);
        Log.d(TAG, "onCreate");

        //mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter.isEnabled()) {
            if (mChatService == null) {
                Log.d(TAG, "mChatService = null");
                setupChat();
            }
        }

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

        //LinearLayout nameLayout;

        handler = new Handler();

        //startTime = System.currentTimeMillis();

        //handler.removeCallbacks(updateTimer);
        //handler.postDelayed(updateTimer, 1000);

        Intent intent = getIntent();

        set = intent.getStringExtra("SETUP_SET");
        tiebreak = intent.getStringExtra("SETUP_TIEBREAK");
        deuce = intent.getStringExtra("SETUP_DEUCE");
        serve = intent.getStringExtra("SETUP_SERVE");

        filename = intent.getStringExtra("FILE_NAME");
        playerUp = intent.getStringExtra("PLAYER_UP");
        playerDown = intent.getStringExtra("PLAYER_DOWN");
        //duration = intent.getStringExtra("GAME_DURATION");
        mode = intent.getStringExtra("WEAR_MODE");

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

        setLayout = (LinearLayout) findViewById(R.id.setLayout);
        nameLayout = (LinearLayout) findViewById(R.id.nameLayout);
        setUp = (TextView) findViewById(R.id.textViewSetUp);
        setDown = (TextView) findViewById(R.id.textViewSetDown);

        imgWinCheckUp = (ImageView) findViewById(R.id.imageWincheckUp);
        imgWinCheckDown = (ImageView) findViewById(R.id.imageWincheckDown);

        imgPlayOrPause = (ImageView) findViewById(R.id.imageViewPlayOrPause);

        stepAndDistance = (TextView) findViewById(R.id.currentTime);
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

        //mClockView = (TextView) findViewById(R.id.clock);

        /*btnYouScore = (Button) findViewById(R.id.btnYouScore);
        btnOpptScore = (Button) findViewById(R.id.btnOpptScore);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnReset = (Button) findViewById(R.id.btnReset);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnLoad = (Button) findViewById(R.id.btnLoad);*/

        TextView textViewPlayerUp = (TextView) findViewById(R.id.textViewPlayerUp);
        final TextView textViewPlayerDown = (TextView) findViewById(R.id.textViewPlayerDown);

        textViewPlayerUp.setText(playerUp);
        textViewPlayerDown.setText(playerDown);

    }



    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");


        mChatService.stop();
        mChatService = null;

        super.onDestroy();

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WearModeGameActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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
                //handler.removeCallbacks(updateTimer);

                Intent intent = new Intent(WearModeGameActivity.this, ResultActivity.class);
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
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
            } else { //not finish

                Log.d(TAG, "*** Game is running ***");
                if (you_score) {
                    Log.d(TAG, "=== I score start ===");

                    if (stack.isEmpty()) { //the state stack is empty
                        new_state = new State();
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


            Log.d(TAG, "*** Game is running ***");
            if (you_score) {
                Log.d(TAG, "=== I score start ===");

                //if (stack.isEmpty()) { //the state stack is empty
                new_state = new State();
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
                break;
            case "1":
                if (setsWinUp == 2 || setsWinDown == 2) {
                    new_state.setFinish(true);
                } else { // new set
                    current_set++;
                    new_state.setCurrent_set(current_set);
                }
                break;
            case "2":
                if (setsWinUp == 3 || setsWinDown == 3) {
                    new_state.setFinish(true);
                } else { // new set
                    current_set++;
                    new_state.setCurrent_set(current_set);
                }
                break;
            default:
                if (setsWinUp == 1 || setsWinDown == 1) {
                    new_state.setFinish(true);
                }
                break;
        }



        Log.d(TAG, "[Check sets End]");
    }



    /*public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }*/





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
                    if (readMessage.contains("command")) {
                        String msgArray[] = readMessage.split("\\|");
                        actionForMessage(msgArray[1]);
                    }

                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(WearModeGameActivity.this, "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(WearModeGameActivity.this, msg.getData().getString(TOAST),
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

    private void actionForMessage(String msg) {
        Log.d(TAG, "command action "+msg);
        if (msg.contains("init")) {
            Log.d(TAG, "=> init");
            stack.clear();
            String msgArray[] = msg.split(";");

            set = msgArray[1];
            tiebreak = msgArray[2];
            deuce = msgArray[3];
            serve = msgArray[4];

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
        } if (msg.contains("calibrate")) {
            Log.d(TAG, "=> calibrate");
            stack.clear();
            String msgArray[] = msg.split("&");
            set = msgArray[1];
            tiebreak = msgArray[2];
            deuce = msgArray[3];
            serve = msgArray[4];
            String stateArray[] = msgArray[5].split(";");
            for (int i=0; i <stateArray.length;i++) {
                Log.d(TAG, "stateArray["+i+"]="+stateArray[i]);
                calculateScore(Boolean.valueOf(stateArray[i]));
            }
        } else {

            switch (msg) {
                case "you":
                    Log.d(TAG, "=> you");
                    calculateScore(true);
                    break;
                case "oppt":
                    Log.d(TAG, "=> oppt");
                    calculateScore(false);
                    break;
                case "reset":

                    break;
                case "back":
                    Log.d(TAG, "=> back");
                    if (stack.isEmpty()) {
                        Log.d(TAG, "stack is empty");
                    } else {
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
                                        String pointMsg = String.valueOf(40) + "A";
                                        pointUp.setText(pointMsg);
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
                                        String pointMsg = String.valueOf(40) + "A";
                                        pointDown.setText(pointMsg);
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
                    break;
            }
        }
    }
}
