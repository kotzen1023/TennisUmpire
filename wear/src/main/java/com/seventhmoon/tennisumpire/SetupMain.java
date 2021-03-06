package com.seventhmoon.tennisumpire;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.seventhmoon.tennisumpire.Data.ServeAdapter;
import com.seventhmoon.tennisumpire.Data.ServeItem;

import java.util.ArrayList;


public class SetupMain extends WearableActivity {
    private static final String TAG = SetupMain.class.getName();

    private Spinner setSpinner;
    //private Spinner gameSpinner;
    private Spinner tiebreakSpinner;
    private Spinner deuceSpinner;
    private Spinner serveSpinner;

    public ArrayAdapter<String> setAdapter;
    //public ArrayAdapter<String> gameAdapter;
    public ArrayAdapter<String> tiebreakAdapter;
    public ArrayAdapter<String> deuceAdapter;
    public ArrayAdapter<String> serveAdapter;

    private ArrayList<ServeItem> newServerList = new ArrayList<>();
    public ServeAdapter newserveAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_menu);

        Button confirm;

        setSpinner = (Spinner) findViewById(R.id.spinnerSets);
        //gameSpinner = (Spinner) findViewById(R.id.spinnerGames);
        tiebreakSpinner = (Spinner) findViewById(R.id.spinnerTieBreak);
        deuceSpinner = (Spinner) findViewById(R.id.spinnerDeuce);
        serveSpinner = (Spinner) findViewById(R.id.spinnerServe);
        confirm = (Button) findViewById(R.id.btnConfirm);

        String[] setList = {getResources().getString(R.string.setup_one_set),
                getResources().getString(R.string.setup_three_sets),
                getResources().getString(R.string.setup_five_set)};

        //String[] gameList = {getResources().getString(R.string.setup_four_game),
        //        getResources().getString(R.string.setup_six_games)};

        String[] tiebreakList = {getResources().getString(R.string.setup_tiebreak), getResources().getString(R.string.setup_deciding_game)};

        String[] deuceList = {getResources().getString(R.string.setup_deuce), getResources().getString(R.string.setup_deciding_point)};

        //String[] serveList = {getResources().getString(R.string.setup_serve_first), getResources().getString(R.string.setup_receive)};

        setAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, setList);
        setSpinner.setAdapter(setAdapter);




        //gameAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, gameList);
        //gameSpinner.setAdapter(gameAdapter);

        tiebreakAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, tiebreakList);
        tiebreakSpinner.setAdapter(tiebreakAdapter);

        deuceAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, deuceList);
        deuceSpinner.setAdapter(deuceAdapter);

        //serve
        ServeItem item1 = new ServeItem();
        item1.setColor(0x16843663); //colorFocusedHighlight
        item1.setText(getResources().getString(R.string.setup_serve_first));
        newServerList.add(item1);

        ServeItem item2 = new ServeItem();
        item1.setColor(0x17170451); //holo_blue_dark
        item1.setText(getResources().getString(R.string.setup_serve_first));
        newServerList.add(item2);

        //serveAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, serveList);
        //serveSpinner.setAdapter(serveAdapter);

        newserveAdapter = new ServeAdapter(SetupMain.this, R.layout.serve_spinner, newServerList);
        serveSpinner.setAdapter(newserveAdapter);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetupMain.this, GameActivity.class);
                intent.putExtra("SETUP_SET", String.valueOf(setSpinner.getSelectedItemPosition()));
                //intent.putExtra("SETUP_GAME", String.valueOf(gameSpinner.getSelectedItemPosition()));
                intent.putExtra("SETUP_TIEBREAK", String.valueOf(tiebreakSpinner.getSelectedItemPosition()));
                intent.putExtra("SETUP_DEUCE", String.valueOf(deuceSpinner.getSelectedItemPosition()));
                intent.putExtra("SETUP_SERVE", String.valueOf(serveSpinner.getSelectedItemPosition()));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");


        super.onDestroy();

    }
}
