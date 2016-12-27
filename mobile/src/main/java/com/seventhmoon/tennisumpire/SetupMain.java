package com.seventhmoon.tennisumpire;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SetupMain extends AppCompatActivity{
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

    private String fileName;
    private String playerUp;
    private String playerDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_menu);

        Intent intent = getIntent();

        fileName = intent.getStringExtra("FILE_NAME");
        playerUp = intent.getStringExtra("PLAYER_UP");
        playerDown = intent.getStringExtra("PLAYER_DOWN");

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

        String[] serveList = {getResources().getString(R.string.setup_serve_first), getResources().getString(R.string.setup_receive)};


        setAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, setList);
        setSpinner.setAdapter(setAdapter);

        //gameAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, gameList);
        //gameSpinner.setAdapter(gameAdapter);

        tiebreakAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, tiebreakList);
        tiebreakSpinner.setAdapter(tiebreakAdapter);

        deuceAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, deuceList);
        deuceSpinner.setAdapter(deuceAdapter);

        serveAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, serveList);
        serveSpinner.setAdapter(serveAdapter);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetupMain.this, GameActivity.class);
                intent.putExtra("SETUP_SET", String.valueOf(setSpinner.getSelectedItemPosition()));
                //intent.putExtra("SETUP_GAME", String.valueOf(gameSpinner.getSelectedItemPosition()));
                intent.putExtra("SETUP_TIEBREAK", String.valueOf(tiebreakSpinner.getSelectedItemPosition()));
                intent.putExtra("SETUP_DEUCE", String.valueOf(deuceSpinner.getSelectedItemPosition()));
                intent.putExtra("SETUP_SERVE", String.valueOf(serveSpinner.getSelectedItemPosition()));
                intent.putExtra("FILE_NAME", fileName);
                intent.putExtra("PLAYER_UP", playerUp);
                intent.putExtra("PLAYER_DOWN", playerDown);
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
