package com.seventhmoon.tennisumpire;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.seventhmoon.tennisumpire.Data.FileOperation.append_record;
import static com.seventhmoon.tennisumpire.Data.FileOperation.check_file_exist;
import static com.seventhmoon.tennisumpire.Data.FileOperation.clear_record;
import static com.seventhmoon.tennisumpire.Data.FileOperation.init_folder_and_files;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    //private SensorManager mSensorManager;
    //private final int REQUEST_ENABLE_BT = 200;
    //private Context context;
    //BluetoothAdapter mBluetoothAdapter;
    //BroadcastReceiver mReceiver;
    //private boolean isRegister;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_folder_and_files();

        Button btnNewGame = (Button) findViewById(R.id.btnNewGame);
        Button btnContinue = (Button) findViewById(R.id.btnContinue);

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(MainActivity.this, SetupMain.class);
                startActivity(intent);
                finish();*/
                showInputDialog();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoadGame.class);
                intent.putExtra("CALL_ACTIVITY", "Main");
                startActivity(intent);
                finish();
            }
        });

        /*context = getBaseContext();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.d(TAG, "Device does not support Bluetooth");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                }
            }
        };

        if (!isRegister) {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);
            isRegister = true;
        }*/
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

    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    protected void showInputDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        View promptView = View.inflate(MainActivity.this, R.layout.input_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        final EditText editPlayerUp = (EditText) promptView.findViewById(R.id.editPlayerUp);
        final EditText editPlayerDown = (EditText) promptView.findViewById(R.id.editPlayerDown);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //resultText.setText("Hello, " + editText.getText());
                //Log.e(TAG, "input password = " + editText.getText());

                if (editFileName.getText().toString().equals("")) {
                    toast("file name empty");

                } else {
                    //check same file name
                    if (check_file_exist(editFileName.getText().toString()))
                    {
                        AlertDialog.Builder confirmdialog = new AlertDialog.Builder(MainActivity.this);
                        confirmdialog.setTitle("File "+"\""+editFileName.getText().toString()+"\" is exist, want to overwrite it?");
                        confirmdialog.setIcon(R.drawable.ball_icon);

                        confirmdialog.setCancelable(false);
                        confirmdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //overwrite
                                //clear
                                clear_record(editFileName.getText().toString());

                                //String msg = editPlayerUp.getText().toString() + ";" + editPlayerDown.getText().toString() + "|";
                                //append_record(msg, editFileName.getText().toString());

                                Intent intent = new Intent(MainActivity.this, SetupMain.class);
                                intent.putExtra("FILE_NAME", editFileName.getText().toString());
                                if (!editPlayerUp.getText().equals(""))
                                    intent.putExtra("PLAYER_UP", editPlayerUp.getText().toString());
                                else
                                    intent.putExtra("PLAYER_UP", "");
                                if (!editPlayerDown.getText().equals(""))
                                    intent.putExtra("PLAYER_DOWN", editPlayerDown.getText().toString());
                                else
                                    intent.putExtra("PLAYER_DOWN", "");
                                startActivity(intent);
                                finish();
                            }
                        });
                        confirmdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                        confirmdialog.show();
                    } else {

                        //add new file
                        //String msg = editPlayerUp.getText().toString() + ";" + editPlayerDown.getText().toString() + "|";
                        //append_record(msg, editFileName.getText().toString());


                        Intent intent = new Intent(MainActivity.this, SetupMain.class);
                        intent.putExtra("FILE_NAME", editFileName.getText().toString());
                        if (!editPlayerUp.getText().equals(""))
                            intent.putExtra("PLAYER_UP", editPlayerUp.getText().toString());
                        else
                            intent.putExtra("PLAYER_UP", "");
                        if (!editPlayerDown.getText().equals(""))
                            intent.putExtra("PLAYER_DOWN", editPlayerDown.getText().toString());
                        else
                            intent.putExtra("PLAYER_DOWN", "");
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.show();
    }
}
