package com.seventhmoon.tennisumpire;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seventhmoon.tennisumpire.Data.State;


import java.io.File;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.TimeZone;

import static com.seventhmoon.tennisumpire.Data.FileOperation.append_record;
import static com.seventhmoon.tennisumpire.Data.FileOperation.check_file_exist;
import static com.seventhmoon.tennisumpire.Data.FileOperation.clear_record;
import static com.seventhmoon.tennisumpire.Data.FileOperation.read_record;


public class GameActivity extends AppCompatActivity{
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
    private LinearLayout setLayout;
    private LinearLayout nameLayout;
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

    private static long startTime;
    private static Handler handler;
    private static long time_use = 0;

    private static Deque<State> stack = new ArrayDeque<>();

    public static File RootDirectory = new File("/");

    private static boolean is_pause = false;

    ProgressDialog loadDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        Button btnYouScore;
        Button btnBack;
        Button btnOpptScore;
        Button btnReset;
        Button btnSave;
        Button btnLoad;

        handler = new Handler();

        startTime = System.currentTimeMillis();

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


                for (int i = 0; i < stat.length; i++) {
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

                }

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

                    /*if (top.isServe()) {
                        imgServeUp.setVisibility(View.INVISIBLE);
                        imgServeDown.setVisibility(View.VISIBLE);
                    } else {
                        imgServeUp.setVisibility(View.VISIBLE);
                        imgServeDown.setVisibility(View.INVISIBLE);
                    }*/

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
        TextView textViewPlayerDown = (TextView) findViewById(R.id.textViewPlayerDown);

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
                    byte current_set;
                    //stack.pop();
                    State popState = stack.pop();
                    time_use = popState.getDuration();

                    handler.removeCallbacks(updateTimer);
                    handler.postDelayed(updateTimer, 1000);


                    if (popState != null) { //pop out current
                        State back_state = stack.peek();
                        if (back_state != null) {
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

                            if (serve.equals("0")) { //you serve first
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
            }
        });

        btnOpptScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateScore(false);
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
                //clear
                clear_record(filename);

                boolean is_tiebreak;
                boolean is_deuce;
                boolean is_firstserve;

                if (tiebreak.equals("0")) {
                    is_tiebreak = true;
                } else {
                    is_tiebreak = false;
                }

                if (deuce.equals("0")) {
                    is_deuce = true;
                } else {
                    is_deuce = false;
                }

                if (serve.equals("0")) {
                    is_firstserve = true;
                } else {
                    is_firstserve = false;
                }

                String msg = playerUp + ";" + playerDown + ";" + is_tiebreak + ";" + is_deuce + ";" +is_firstserve+ ";" +set+ "|";
                append_record(msg, filename);

                State top = stack.peek();
                top.setDuration(time_use);

                int i = 0;
                //load stack
                for (State s : stack) {

                    if (i >= 1) {
                        append_record("&", filename);
                    }


                    String append_msg = s.getCurrent_set()+";"
                            +s.isServe()+";"
                            +s.isInTiebreak()+";"
                            +s.isFinish()+";"
                            //+s.isDeuce()+";"
                            //+s.getSetLimit()+";"
                            +s.getSetsUp()+";"
                            +s.getSetsDown()+";"
                            +s.getDuration()+";"
                            +s.getSet_game_up((byte)0x1)+";"
                            +s.getSet_game_down((byte)0x1)+";"
                            +s.getSet_point_up((byte)0x1)+";"
                            +s.getSet_point_down((byte)0x1)+";"
                            +s.getSet_tiebreak_point_up((byte)0x1)+";"
                            +s.getSet_tiebreak_point_down((byte)0x1)+";"
                            +s.getSet_game_up((byte)0x2)+";"
                            +s.getSet_game_down((byte)0x2)+";"
                            +s.getSet_point_up((byte)0x2)+";"
                            +s.getSet_point_down((byte)0x2)+";"
                            +s.getSet_tiebreak_point_up((byte)0x2)+";"
                            +s.getSet_tiebreak_point_down((byte)0x2)+";"
                            +s.getSet_game_up((byte)0x3)+";"
                            +s.getSet_game_down((byte)0x3)+";"
                            +s.getSet_point_up((byte)0x3)+";"
                            +s.getSet_point_down((byte)0x3)+";"
                            +s.getSet_tiebreak_point_up((byte)0x3)+";"
                            +s.getSet_tiebreak_point_down((byte)0x3)+";"
                            +s.getSet_game_up((byte)0x4)+";"
                            +s.getSet_game_down((byte)0x4)+";"
                            +s.getSet_point_up((byte)0x4)+";"
                            +s.getSet_point_down((byte)0x4)+";"
                            +s.getSet_tiebreak_point_up((byte)0x4)+";"
                            +s.getSet_tiebreak_point_down((byte)0x4)+";"
                            +s.getSet_game_up((byte)0x5)+";"
                            +s.getSet_game_down((byte)0x5)+";"
                            +s.getSet_point_up((byte)0x5)+";"
                            +s.getSet_point_down((byte)0x5)+";"
                            +s.getSet_tiebreak_point_up((byte)0x5)+";"
                            +s.getSet_tiebreak_point_down((byte)0x5);
                    append_record(append_msg, filename);
                    i++;
                }
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

        super.onDestroy();

    }

    private void calculateScore(boolean you_score) {
        byte current_set = 0;
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

                        new_state.setDuration(time_use);
                        //new_state.setSet_1_point_down((byte)0x01);
                        /*new_state.setSetLimit(Byte.valueOf(set));

                        if (deuce.equals("0")) {
                            new_state.setDeuce(true);
                        } else {
                            new_state.setDeuce(false);
                        }*/

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

                            /*if (deuce.equals("0")) {
                                new_state.setDeuce(true);
                            } else {
                                new_state.setDeuce(false);
                            }

                            new_state.setSetLimit(current_state.getSetLimit());*/
                            new_state.setSetsUp(current_state.getSetsUp());
                            new_state.setSetsDown(current_state.getSetsDown());

                            new_state.setDuration(time_use);

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

                        new_state.setDuration(time_use);
                        //Log.e(TAG, "get_set_1_point_up = "+new_state.getSet_1_point_up()+", isServe ? "+ (new_state.isServe() ? "YES" : "NO"));
                        /*new_state.setSetLimit(Byte.valueOf(set));

                        if (deuce.equals("0")) {
                            new_state.setDeuce(true);
                        } else {
                            new_state.setDeuce(false);
                        }*/
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

                            /*if (deuce.equals("0")) {
                                new_state.setDeuce(true);
                            } else {
                                new_state.setDeuce(false);
                            }

                            new_state.setSetLimit(current_state.getSetLimit());*/
                            new_state.setSetsUp(current_state.getSetsUp());
                            new_state.setSetsDown(current_state.getSetsDown());

                            new_state.setDuration(time_use);

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
                Log.d(TAG, "==>[Stack empty]");
                if (serve.equals("0"))
                    new_state.setServe(true);
                else
                    new_state.setServe(false);

                //set current set = 1
                new_state.setCurrent_set((byte) 0x01);

                new_state.setSet_point_down((byte) 0x01, (byte) 0x01);
                //new_state.setSet_1_point_down((byte)0x01);
                /*new_state.setSetLimit(Byte.valueOf(set));

                if (deuce.equals("0")) {
                    new_state.setDeuce(true);
                } else {
                    new_state.setDeuce(false);
                }*/

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

                /*new_state.setSetLimit(Byte.valueOf(set));
                //Log.e(TAG, "get_set_1_point_up = "+new_state.getSet_1_point_up()+", isServe ? "+ (new_state.isServe() ? "YES" : "NO"));

                if (deuce.equals("0")) {
                    new_state.setDeuce(true);
                } else {
                    new_state.setDeuce(false);
                }*/
                //}
                Log.d(TAG, "=== Oppt score end ===");
            }

            if (new_state != null) {

                Log.d(TAG, "########## new state start ##########");
                Log.d(TAG, "current set : " + new_state.getCurrent_set());
                Log.d(TAG, "Serve : " + new_state.isServe());
                Log.d(TAG, "In tiebreak : " + new_state.isInTiebreak());
                Log.d(TAG, "Finish : " + new_state.isFinish());
                //Log.d(TAG, "deuce : " + new_state.isDeuce());
                //Log.d(TAG, "Set Limit : "+ new_state.getSetLimit());

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
            Long spentTime = System.currentTimeMillis() - startTime;
            //計算目前已過分鐘數

            //計算目前已過秒數
            //Long seconds = (time_use) % 60;
            //time.setText(minius+":"+seconds);

            handler.postDelayed(this, 1000);
            time_use++;

            Log.d(TAG, "time_use = "+time_use);

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

    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        //clear
        clear_record(filename);

        boolean is_tiebreak;
        boolean is_deuce;
        boolean is_firstserve;

        if (tiebreak.equals("0")) {
            is_tiebreak = true;
        } else {
            is_tiebreak = false;
        }

        if (deuce.equals("0")) {
            is_deuce = true;
        } else {
            is_deuce = false;
        }

        if (serve.equals("0")) {
            is_firstserve = true;
        } else {
            is_firstserve = false;
        }

        String msg = playerUp + ";" + playerDown + ";" + is_tiebreak + ";" + is_deuce + ";" +is_firstserve+ ";" +set+ "|";
        append_record(msg, filename);

        State top = stack.peek();
        top.setDuration(time_use);

        int i = 0;
        //load stack
        for (State s : stack) {

            if (i >= 1) {
                append_record("&", filename);
            }


            String append_msg = s.getCurrent_set()+";"
                    +s.isServe()+";"
                    +s.isInTiebreak()+";"
                    +s.isFinish()+";"
                    //+s.isDeuce()+";"
                    //+s.getSetLimit()+";"
                    +s.getSetsUp()+";"
                    +s.getSetsDown()+";"
                    +s.getDuration()+";"
                    +s.getSet_game_up((byte)0x1)+";"
                    +s.getSet_game_down((byte)0x1)+";"
                    +s.getSet_point_up((byte)0x1)+";"
                    +s.getSet_point_down((byte)0x1)+";"
                    +s.getSet_tiebreak_point_up((byte)0x1)+";"
                    +s.getSet_tiebreak_point_down((byte)0x1)+";"
                    +s.getSet_game_up((byte)0x2)+";"
                    +s.getSet_game_down((byte)0x2)+";"
                    +s.getSet_point_up((byte)0x2)+";"
                    +s.getSet_point_down((byte)0x2)+";"
                    +s.getSet_tiebreak_point_up((byte)0x2)+";"
                    +s.getSet_tiebreak_point_down((byte)0x2)+";"
                    +s.getSet_game_up((byte)0x3)+";"
                    +s.getSet_game_down((byte)0x3)+";"
                    +s.getSet_point_up((byte)0x3)+";"
                    +s.getSet_point_down((byte)0x3)+";"
                    +s.getSet_tiebreak_point_up((byte)0x3)+";"
                    +s.getSet_tiebreak_point_down((byte)0x3)+";"
                    +s.getSet_game_up((byte)0x4)+";"
                    +s.getSet_game_down((byte)0x4)+";"
                    +s.getSet_point_up((byte)0x4)+";"
                    +s.getSet_point_down((byte)0x4)+";"
                    +s.getSet_tiebreak_point_up((byte)0x4)+";"
                    +s.getSet_tiebreak_point_down((byte)0x4)+";"
                    +s.getSet_game_up((byte)0x5)+";"
                    +s.getSet_game_down((byte)0x5)+";"
                    +s.getSet_point_up((byte)0x5)+";"
                    +s.getSet_point_down((byte)0x5)+";"
                    +s.getSet_tiebreak_point_up((byte)0x5)+";"
                    +s.getSet_tiebreak_point_down((byte)0x5);
            append_record(append_msg, filename);
            i++;
        }

        finish();
    }
}
