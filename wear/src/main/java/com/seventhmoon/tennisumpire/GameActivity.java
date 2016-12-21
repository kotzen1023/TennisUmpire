package com.seventhmoon.tennisumpire;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.seventhmoon.tennisumpire.Data.State;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.Locale;
import java.util.Stack;
import java.util.TimeZone;


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

    private TextView mClockView;
    private Button btnYouScore;
    private Button btnBack;
    private Button btnOpptScore;
    private Button btnReset;

    private TextView textCurrentTime;
    private TextView textGameTime;

    private static String set;
    //private static String game;
    private static String tiebreak;
    private static String deuce;
    private static String serve;
    private static long startTime;
    private static Handler handler;
    private static long time_use = 0;

    private static Deque<State> stack = new ArrayDeque<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        handler = new Handler();

        startTime = System.currentTimeMillis();

        handler.removeCallbacks(updateTimer);
        handler.postDelayed(updateTimer, 1000);

        Intent intent = getIntent();

        set = intent.getStringExtra("SETUP_SET");
        //game = intent.getStringExtra("SETUP_GAME");
        tiebreak = intent.getStringExtra("SETUP_TIEBREAK");
        deuce = intent.getStringExtra("SETUP_DEUCE");
        serve = intent.getStringExtra("SETUP_SERVE");

        Log.e(TAG, "SET = "+set);
        //Log.e(TAG, "GAME = "+game);
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


        setAmbientEnabled();






        //mClockView = (TextView) findViewById(R.id.clock);

        btnYouScore = (Button) findViewById(R.id.btnYouScore);
        btnOpptScore = (Button) findViewById(R.id.btnOpptScore);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnReset = (Button) findViewById(R.id.btnReset);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stack.isEmpty()) {
                    Log.d(TAG, "stack is empty!");
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
                                    pointUp.setText("15");
                                } else if (back_state.getSet_point_up(current_set) == 2) {
                                    pointUp.setText("30");
                                } else if (back_state.getSet_point_up(current_set) == 3) {
                                    pointUp.setText("40");
                                } else if (back_state.getSet_point_up(current_set) == 4) {
                                    pointUp.setText("40A");
                                } else {
                                    pointUp.setText("0");
                                }
                            } else { //tie break;
                                pointUp.setText(String.valueOf(back_state.getSet_point_up(current_set)));
                            }

                            if (!back_state.isInTiebreak()) { //not in tiebreak
                                if (back_state.getSet_point_down(current_set) == 1) {
                                    pointDown.setText("15");
                                } else if (back_state.getSet_point_down(current_set) == 2) {
                                    pointDown.setText("30");
                                } else if (back_state.getSet_point_down(current_set) == 3) {
                                    pointDown.setText("40");
                                } else if (back_state.getSet_point_down(current_set) == 4) {
                                    pointDown.setText("40A");
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

                            int set_limit = 0;
                            if (set.equals("0")) {
                                set_limit = 1;
                            } else if (set.equals("1")) {
                                set_limit = 3;
                            } else if (set.equals("2")) {
                                set_limit = 5;
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
                finish();
            }
        });
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

    private void calculateScore(boolean you_score) {
        byte current_set = 0;
        State new_state=null;
        //load top state first
        State current_state = stack.peek();

        int set_limit = 0;
        if (set.equals("0")) {
            set_limit = 1;
        } else if (set.equals("1")) {
            set_limit = 3;
        } else if (set.equals("2")) {
            set_limit = 5;
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
        } else {
            Log.d(TAG, "Stack is empty!");
        }

        if (current_state != null && current_state.isFinish()) {
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
        } else {
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
                    pointUp.setText("15");
                } else if (new_state.getSet_point_up(current_set) == 2) {
                    pointUp.setText("30");
                } else if (new_state.getSet_point_up(current_set) == 3) {
                    pointUp.setText("40");
                } else if (new_state.getSet_point_up(current_set) == 4) {
                    pointUp.setText("40A");
                } else {
                    pointUp.setText("0");
                }
            } else { //tie break;
                pointUp.setText(String.valueOf(new_state.getSet_point_up(current_set)));
            }

            if (!new_state.isInTiebreak()) { //not in tiebreak
                if (new_state.getSet_point_down(current_set) == 1) {
                    pointDown.setText("15");
                } else if (new_state.getSet_point_down(current_set) == 2) {
                    pointDown.setText("30");
                } else if (new_state.getSet_point_down(current_set) == 3) {
                    pointDown.setText("40");
                } else if (new_state.getSet_point_down(current_set) == 4) {
                    pointDown.setText("40A");
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
        if (set.equals("0")) { // 1 set
            if (setsWinUp == 1 || setsWinDown == 1) {
                new_state.setFinish(true);
            }
        } else if (set.equals("1")) { // three set
            if (setsWinUp == 2 || setsWinDown == 2) {
                new_state.setFinish(true);
            } else { // new set
                current_set++;
                new_state.setCurrent_set(current_set);
            }
        } else if (set.equals("2")) { // five set
            if (setsWinUp == 3 || setsWinDown == 3) {
                new_state.setFinish(true);
            } else { // new set
                current_set++;
                new_state.setCurrent_set(current_set);
            }
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

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            sdf.setTimeZone(tz);//set time zone.
            Date netDate = new Date(System.currentTimeMillis());
            //Date gameDate = new Date(spentTime);
            Long hour = (time_use)/3600;
            Long minius = (time_use)%3600/60;
            textCurrentTime.setText(sdf.format(netDate));
            textGameTime.setText(f.format(hour)+":"+f.format(minius));
            //textGameTime.setText(sdf.format(gameDate));
        }
    };
}
