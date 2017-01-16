package com.seventhmoon.tennisumpire;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.seventhmoon.tennisumpire.Data.CurrentStatArrayAdapter;
import com.seventhmoon.tennisumpire.Data.CurrentStatItem;
import com.seventhmoon.tennisumpire.Data.State;

import java.util.ArrayList;
import java.util.Locale;

import static com.seventhmoon.tennisumpire.GameActivity.stack;

public class CurrentStatActivity extends AppCompatActivity{
    private static final String TAG = CurrentStatActivity.class.getName();

    private static ArrayList<CurrentStatItem> currrentArray = new ArrayList<>();

    private ListView listView;
    private CurrentStatArrayAdapter currentStatArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_current);

        //for action bar
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ball_icon);
        }

        listView = (ListView) findViewById(R.id.listViewStat);

        currrentArray.clear();



        Intent intent = getIntent();
        String playerUp = intent.getStringExtra("PLAYER_UP");
        String playerDown = intent.getStringExtra("PLAYER_DOWN");

        Locale current_local;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            current_local = getResources().getConfiguration().locale;
        } else {
            current_local = getResources().getConfiguration().getLocales().get(0);
        }


        State current_state = stack.peek();
        if (current_state != null) {

            CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown);
            currrentArray.add(item1);

            CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                    String.valueOf(current_state.getAceCountUp()), String.valueOf(current_state.getAceCountDown()));
            currrentArray.add(item2);

            CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                    String.valueOf(current_state.getDoubleFaultUp()), String.valueOf(current_state.getDoubleFaultDown()));
            currrentArray.add(item3);

            CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error),
                    String.valueOf(current_state.getUnforceErrorUp()),
                    String.valueOf(current_state.getUnforceErrorDown()));
            currrentArray.add(item4);

            CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve), current_state.getFirstServeUp() == 0 ? "0%" :
                    String.format(current_local, "%.1f", ((float) (current_state.getFirstServeUp() - current_state.getFirstServeMissUp()) / (float) current_state.getFirstServeUp()) * 100) + "%",
                    current_state.getFirstServeDown() == 0 ? "0%" :
                            String.format(current_local, "%.1f", ((float) (current_state.getFirstServeDown() - current_state.getFirstServeMissDown()) / (float) current_state.getFirstServeDown()) * 100) + "%");
            currrentArray.add(item5);

            CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won),
                    current_state.getFirstServeWonUp() == 0 ? "0%" :
                            String.format(current_local, "%.1f", ((float) current_state.getFirstServeWonUp() / (float) (current_state.getFirstServeWonUp() + current_state.getFirstServeLostUp())) * 100) + "%",
                    current_state.getFirstServeWonDown() == 0 ? "0%" :
                            String.format(current_local, "%.1f", ((float) current_state.getFirstServeWonDown() / (float) (current_state.getFirstServeWonDown() + current_state.getFirstServeLostDown())) * 100) + "%");
            currrentArray.add(item6);

            CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve), current_state.getSecondServeUp() == 0 ? "0%" :
                    String.format(current_local, "%.1f", ((float) (current_state.getSecondServeUp() - current_state.getDoubleFaultUp()) / (float) current_state.getSecondServeUp()) * 100) + "%",
                    current_state.getSecondServeDown() == 0 ? "0%" :
                            String.format(current_local, "%.1f", ((float) (current_state.getSecondServeDown() - current_state.getDoubleFaultDown()) / (float) current_state.getSecondServeDown()) * 100) + "%");
            currrentArray.add(item7);

            CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won),
                    current_state.getSecondServeWonUp() == 0 ? "0%" :
                            String.format(current_local, "%.1f", ((float) current_state.getSecondServeWonUp() / (float) (current_state.getSecondServeWonUp() + current_state.getSecondServeLostUp())) * 100) + "%",
                    current_state.getSecondServeDown() == 0 ? "0%" :
                            String.format(current_local, "%.1f", ((float) current_state.getSecondServeWonDown() / (float) (current_state.getSecondServeWonDown() + current_state.getSecondServeLostDown())) * 100) + "%");
            currrentArray.add(item8);

            CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner),
                    String.valueOf(current_state.getForehandWinnerUp() +
                            current_state.getBackhandWinnerUp() +
                            current_state.getForehandVolleyUp() +
                            current_state.getBackhandVolleyUp()),
                    String.valueOf(current_state.getForehandWinnerDown() +
                            current_state.getBackhandWinnerDown() +
                            current_state.getForehandVolleyDown() +
                            current_state.getBackhandVolleyDown()));
            currrentArray.add(item9);

            CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won),
                    String.valueOf(current_state.getForehandVolleyUp() +
                            current_state.getBackhandVolleyUp()),
                    String.valueOf(current_state.getForehandVolleyDown() +
                            current_state.getBackhandVolleyDown()));
            currrentArray.add(item10);

            CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won),
                    current_state.getBreakPointUp() == 0 ? "0%" : "("+
                            String.valueOf(current_state.getBreakPointUp() - current_state.getBreakPointMissUp()) +"/"+String.valueOf(current_state.getBreakPointUp())+") "+
                            String.format(current_local, "%.1f", ((float) (current_state.getBreakPointUp() - current_state.getBreakPointMissUp()) / (float) current_state.getBreakPointUp()) * 100) + "%",
                    current_state.getBreakPointDown() == 0 ? "0%" : "("+
                            String.valueOf(current_state.getBreakPointDown() - current_state.getBreakPointMissDown()) +"/"+String.valueOf(current_state.getBreakPointDown())+") "+
                            String.format(current_local, "%.1f", ((float) (current_state.getBreakPointDown() - current_state.getBreakPointMissDown()) / (float) current_state.getBreakPointDown()) * 100) + "%");
            currrentArray.add(item11);
        } else {
            Log.d(TAG, "current_state = null");

            CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown);
            currrentArray.add(item1);

            CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                    "0", "0");
            currrentArray.add(item2);

            CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                    "0", "0");
            currrentArray.add(item3);

            CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error), "0", "0");
            currrentArray.add(item4);

            CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve), "0%", "0%");
            currrentArray.add(item5);

            CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won), "0%", "0%");
            currrentArray.add(item6);

            CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve), "0%", "0%");
            currrentArray.add(item7);

            CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won), "0%", "0%");
            currrentArray.add(item8);

            CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner), "0", "0");
            currrentArray.add(item9);

            CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won), "0", "0");
            currrentArray.add(item10);

            CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won), "0%", "0%");
            currrentArray.add(item11);
        }

        currentStatArrayAdapter = new CurrentStatArrayAdapter(CurrentStatActivity.this, R.layout.stat_current_item, currrentArray);
        listView.setAdapter(currentStatArrayAdapter);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onResume();

        Log.d(TAG, "onPause");

    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
