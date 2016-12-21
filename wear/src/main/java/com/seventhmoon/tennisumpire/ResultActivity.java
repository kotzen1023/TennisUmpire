package com.seventhmoon.tennisumpire;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends WearableActivity {
    private static final String TAG = ResultActivity.class.getName();

    private TextView textViewSet1Up;
    private TextView textViewSet1Down;
    private TextView textViewSet2Up;
    private TextView textViewSet2Down;
    private TextView textViewSet3Up;
    private TextView textViewSet3Down;
    private TextView textViewSet4Up;
    private TextView textViewSet4Down;
    private TextView textViewSet5Up;
    private TextView textViewSet5Down;
    private TextView textViewSet1TieBreakUp;
    private TextView textViewSet1TieBreakDown;
    private TextView textViewSet2TieBreakUp;
    private TextView textViewSet2TieBreakDown;
    private TextView textViewSet3TieBreakUp;
    private TextView textViewSet3TieBreakDown;
    private TextView textViewSet4TieBreakUp;
    private TextView textViewSet4TieBreakDown;
    private TextView textViewSet5TieBreakUp;
    private TextView textViewSet5TieBreakDown;

    private Button btnOK;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

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
            textViewSet3TieBreakUp.setText(set5_tiebreak_up);
            textViewSet3TieBreakDown.setText(set5_tiebreak_down);
            textViewSet3TieBreakUp.setVisibility(View.VISIBLE);
            textViewSet3TieBreakDown.setVisibility(View.VISIBLE);
        }

        btnOK = (Button) findViewById(R.id.btnResultOk);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(ResultActivity.this, GameActivity.class);
                //startActivity(intent);
                finish();
            }
        });
    }
}

