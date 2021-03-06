package com.seventhmoon.tennisumpire;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static com.seventhmoon.tennisumpire.Data.Constants.ACTION.CLOSE_RESULT_ACTIVITY;

public class ResultActivity extends AppCompatActivity{
    private static final String TAG = ResultActivity.class.getName();


    private static String playerUp;
    private static String playerDown;

    private static String wear_mode;

    private BroadcastReceiver mBroadcastReceiver;
    private boolean isRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        //for action bar
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ball_icon);
        }

        TextView textViewSet1Up;
        TextView textViewSet1Down;
        TextView textViewSet2Up;
        TextView textViewSet2Down;
        TextView textViewSet3Up;
        TextView textViewSet3Down;
        TextView textViewSet4Up;
        TextView textViewSet4Down;
        TextView textViewSet5Up;
        TextView textViewSet5Down;
        TextView textViewSet1TieBreakUp;
        TextView textViewSet1TieBreakDown;
        TextView textViewSet2TieBreakUp;
        TextView textViewSet2TieBreakDown;
        TextView textViewSet3TieBreakUp;
        TextView textViewSet3TieBreakDown;
        TextView textViewSet4TieBreakUp;
        TextView textViewSet4TieBreakDown;
        TextView textViewSet5TieBreakUp;
        TextView textViewSet5TieBreakDown;
        TextView textViewDuration;

        TextView textWinText;
        TextView textLoseText;

        Button btnOK;

        Intent intent = getIntent();

        String set1_up = intent.getStringExtra("SET1_GAME_UP");
        String set1_down = intent.getStringExtra("SET1_GAME_DOWN");
        String set2_up = intent.getStringExtra("SET2_GAME_UP");
        String set2_down = intent.getStringExtra("SET2_GAME_DOWN");
        String set3_up = intent.getStringExtra("SET3_GAME_UP");
        String set3_down = intent.getStringExtra("SET3_GAME_DOWN");
        String set4_up = intent.getStringExtra("SET4_GAME_UP");
        String set4_down = intent.getStringExtra("SET4_GAME_DOWN");
        String set5_up = intent.getStringExtra("SET5_GAME_UP");
        String set5_down = intent.getStringExtra("SET5_GAME_DOWN");

        String set1_tiebreak_up = intent.getStringExtra("SET1_TIEBREAK_UP");
        String set1_tiebreak_down = intent.getStringExtra("SET1_TIEBREAK_DOWN");
        String set2_tiebreak_up = intent.getStringExtra("SET2_TIEBREAK_UP");
        String set2_tiebreak_down = intent.getStringExtra("SET2_TIEBREAK_DOWN");
        String set3_tiebreak_up = intent.getStringExtra("SET3_TIEBREAK_UP");
        String set3_tiebreak_down = intent.getStringExtra("SET3_TIEBREAK_DOWN");
        String set4_tiebreak_up = intent.getStringExtra("SET4_TIEBREAK_UP");
        String set4_tiebreak_down = intent.getStringExtra("SET4_TIEBREAK_DOWN");
        String set5_tiebreak_up = intent.getStringExtra("SET5_TIEBREAK_UP");
        String set5_tiebreak_down = intent.getStringExtra("SET5_TIEBREAK_DOWN");

        String duration = intent.getStringExtra("GAME_DURATION");



        String win_player = intent.getStringExtra("WIN_PLAYER");
        String lose_player = intent.getStringExtra("LOSE_PLAYER");

        playerUp = intent.getStringExtra("PLAYER_UP");
        playerDown = intent.getStringExtra("PLAYER_DOWN");

        wear_mode = intent.getStringExtra("WEAR_MODE");

        textViewSet1Up = (TextView) findViewById(R.id.set1_up);
        textViewSet1Down = (TextView) findViewById(R.id.set1_down);
        textViewSet2Up = (TextView) findViewById(R.id.set2_up);
        textViewSet2Down = (TextView) findViewById(R.id.set2_down);
        textViewSet3Up = (TextView) findViewById(R.id.set3_up);
        textViewSet3Down = (TextView) findViewById(R.id.set3_down);
        textViewSet4Up = (TextView) findViewById(R.id.set4_up);
        textViewSet4Down = (TextView) findViewById(R.id.set4_down);
        textViewSet5Up = (TextView) findViewById(R.id.set5_up);
        textViewSet5Down = (TextView) findViewById(R.id.set5_down);

        textViewSet1TieBreakUp = (TextView) findViewById(R.id.set1_tibreak_up);
        textViewSet1TieBreakDown = (TextView) findViewById(R.id.set1_tibreak_down);
        textViewSet2TieBreakUp = (TextView) findViewById(R.id.set2_tibreak_up);
        textViewSet2TieBreakDown = (TextView) findViewById(R.id.set2_tibreak_down);
        textViewSet3TieBreakUp = (TextView) findViewById(R.id.set3_tibreak_up);
        textViewSet3TieBreakDown = (TextView) findViewById(R.id.set3_tibreak_down);
        textViewSet4TieBreakUp = (TextView) findViewById(R.id.set4_tibreak_up);
        textViewSet4TieBreakDown = (TextView) findViewById(R.id.set4_tibreak_down);
        textViewSet5TieBreakUp = (TextView) findViewById(R.id.set5_tibreak_up);
        textViewSet5TieBreakDown = (TextView) findViewById(R.id.set5_tibreak_down);

        textViewDuration = (TextView) findViewById(R.id.duration);

        textWinText = (TextView) findViewById(R.id.textWinPlayer);
        textLoseText = (TextView) findViewById(R.id.textLosePlayer);

        if (set1_up.equals("0") && set1_down.equals("0"))
            Log.d(TAG, "1 Don't care");
        else {
            textViewSet1Up.setText(set1_up);
            textViewSet1Down.setText(set1_down);
        }

        if (set2_up.equals("0") && set2_down.equals("0"))
            Log.d(TAG, "2 Don't care");
        else {
            textViewSet2Up.setText(set2_up);
            textViewSet2Down.setText(set2_down);
        }

        if (set3_up.equals("0") && set3_down.equals("0"))
            Log.d(TAG, "3 Don't care");
        else {
            textViewSet3Up.setText(set3_up);
            textViewSet3Down.setText(set3_down);
        }

        if (set4_up.equals("0") && set4_down.equals("0"))
            Log.d(TAG, "4 Don't care");
        else {
            textViewSet4Up.setText(set4_up);
            textViewSet4Down.setText(set4_down);
        }

        if (set5_up.equals("0") && set5_down.equals("0"))
            Log.d(TAG, "5 Don't care");
        else {
            textViewSet5Up.setText(set5_up);
            textViewSet5Down.setText(set5_down);
        }
        //tiebreak
        if (set1_tiebreak_up.equals("0") && set1_tiebreak_down.equals("0"))
            Log.d(TAG, "1 Don't care");
        else {
            textViewSet1TieBreakUp.setText(set1_tiebreak_up);
            textViewSet1TieBreakDown.setText(set1_tiebreak_down);
            textViewSet1TieBreakUp.setVisibility(View.VISIBLE);
            textViewSet1TieBreakDown.setVisibility(View.VISIBLE);
        }
        if (set2_tiebreak_up.equals("0") && set2_tiebreak_down.equals("0"))
            Log.d(TAG, "2 Don't care");
        else {
            textViewSet2TieBreakUp.setText(set2_tiebreak_up);
            textViewSet2TieBreakDown.setText(set2_tiebreak_down);
            textViewSet2TieBreakUp.setVisibility(View.VISIBLE);
            textViewSet2TieBreakDown.setVisibility(View.VISIBLE);
        }
        if (set3_tiebreak_up.equals("0") && set3_tiebreak_down.equals("0"))
            Log.d(TAG, "3 Don't care");
        else {
            textViewSet3TieBreakUp.setText(set3_tiebreak_up);
            textViewSet3TieBreakDown.setText(set3_tiebreak_down);
            textViewSet3TieBreakUp.setVisibility(View.VISIBLE);
            textViewSet3TieBreakDown.setVisibility(View.VISIBLE);
        }
        if (set4_tiebreak_up.equals("0") && set4_tiebreak_down.equals("0"))
            Log.d(TAG, "4 Don't care");
        else {
            textViewSet4TieBreakUp.setText(set4_tiebreak_up);
            textViewSet4TieBreakDown.setText(set4_tiebreak_down);
            textViewSet4TieBreakUp.setVisibility(View.VISIBLE);
            textViewSet4TieBreakDown.setVisibility(View.VISIBLE);
        }
        if (set5_tiebreak_up.equals("0") && set5_tiebreak_down.equals("0"))
            Log.d(TAG, "5 Don't care");
        else {
            textViewSet5TieBreakUp.setText(set5_tiebreak_up);
            textViewSet5TieBreakDown.setText(set5_tiebreak_down);
            textViewSet5TieBreakUp.setVisibility(View.VISIBLE);
            textViewSet5TieBreakDown.setVisibility(View.VISIBLE);
        }

        NumberFormat f = new DecimalFormat("00");
        Long hour = (Long.valueOf(duration))/3600;
        Long min = (Long.valueOf(duration))%3600/60;
        Long sec = (Long.valueOf(duration))%60;

        textViewDuration.setText(f.format(hour)+":"+f.format(min)+":"+f.format(sec));

        textWinText.setText(win_player);
        textLoseText.setText(lose_player);

        btnOK = (Button) findViewById(R.id.btnResultOk);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(ResultActivity.this, GameActivity.class);
                //startActivity(intent);
                finish();
            }
        });

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(CLOSE_RESULT_ACTIVITY)) {
                    finish();
                }
            }
        };

        if (!isRegister) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(CLOSE_RESULT_ACTIVITY);
            registerReceiver(mBroadcastReceiver, filter);
            isRegister = true;
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();

    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");

        if (isRegister && mBroadcastReceiver != null) {

            try {
                unregisterReceiver(mBroadcastReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            isRegister = false;
            mBroadcastReceiver = null;
            Log.d(TAG, "unregisterReceiver mReceiver");

        }

        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.game_activity_menu, menu);

        MenuItem item_stat = menu.findItem(R.id.action_show_stat);
        if (wear_mode != null && wear_mode.equals("true")) {
            item_stat.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_show_stat:
                intent = new Intent(ResultActivity.this, CurrentStatActivity.class);
                intent.putExtra("PLAYER_UP", playerUp);
                intent.putExtra("PLAYER_DOWN", playerDown);
                startActivity(intent);
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}
