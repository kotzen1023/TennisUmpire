package com.seventhmoon.tennisumpire;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);








    }

    @Override
    protected void onPause() {

        super.onPause();

    }

    @Override
    protected void onResume() {


        super.onResume();
    }

    @Override
    protected void onDestroy() {



        /*if (isRegister && mReceiver != null) {

            try {
                unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            isRegister = false;
            mReceiver = null;
            Log.d(TAG, "unregisterReceiver mReceiver");

        }*/

        super.onDestroy();
    }




}
