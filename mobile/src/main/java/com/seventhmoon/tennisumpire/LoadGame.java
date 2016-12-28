package com.seventhmoon.tennisumpire;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.seventhmoon.tennisumpire.Data.FileChooseArrayAdapter;
import com.seventhmoon.tennisumpire.Data.FileChooseItem;
import com.seventhmoon.tennisumpire.Data.State;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class LoadGame extends AppCompatActivity {
    private static final String TAG = LoadGame.class.getName();

    private ListView listView;

    private FileChooseArrayAdapter fileChooseArrayAdapter;

    public static File RootDirectory = new File("/");
    private static String call_activity;
    private static String previous_filename;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        call_activity = intent.getStringExtra("CALL_ACTIVITY");
        previous_filename = intent.getStringExtra("PREVIOUS_FILENAME");

        Log.d(TAG, "call_activity = " +call_activity);

        setContentView(R.layout.load_game);

        listView = (ListView) findViewById(R.id.listViewFileChoose);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //path = Environment.getExternalStorageDirectory();
            RootDirectory = Environment.getExternalStorageDirectory();
        }
        //check folder
        File folder = new File(RootDirectory.getAbsolutePath() + "/.tennisScoredBoard");

        fill(folder);
    }

    private void fill(File f)
    {
        final File[]dirs = f.listFiles();
        //this.setTitle("Current Dir: "+f.getName());


        ArrayList<FileChooseItem> dir = new ArrayList<>();
        ArrayList<FileChooseItem> fls = new ArrayList<>();
        try{
            for(File ff: dirs)
            {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                //CheckBox checkBox = new CheckBox(getApplicationContext());
                if(!ff.isDirectory()){


                    char first = ff.getName().charAt(0);
                    if (first != '.')
                        fls.add(new FileChooseItem(ff.getName(), date_modify));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        //Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);


        fileChooseArrayAdapter = new FileChooseArrayAdapter(LoadGame.this,R.layout.file_choose_item,dir);
        listView.setAdapter(fileChooseArrayAdapter);
        //listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FileChooseItem o = fileChooseArrayAdapter.getItem(position);
                Log.d(TAG, "select file "+ o.getFileName());
                Intent intent = new Intent(LoadGame.this, GameActivity.class);
                intent.putExtra("FILE_NAME", o.getFileName());
                startActivity(intent);
                finish();
            }
        });

        /*listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.e(TAG, "position = " + position + ", size = " + listView.getCount());
                Data.FileChooseLongClick = true;
                //FileChooseItem fileChooseItem = (FileChooseItem) fileChooseArrayAdapter.getItem(position);
                //Log.i(TAG, "name = "+fileChooseItem.getName());
                //Log.e(TAG, "ck = " + fileChooseItem.getCheckBox());
                //fileChooseItem.getCheckBox().setVisibility(View.VISIBLE);

                for(int i=0;i<listView.getCount(); i++) {
                    FileChooseItem fileChooseItem = fileChooseArrayAdapter.getItem(i);

                    //Log.i(TAG, "item["+i+"]="+fileChooseItem.getName());

                    if (fileChooseItem.getCheckBox() != null) {
                        //Log.e(TAG, "set item[" + i + "] visible");
                        if (!fileChooseItem.getName().equals(".."))
                            fileChooseItem.getCheckBox().setVisibility(View.VISIBLE);
                        else
                            fileChooseItem.getCheckBox().setVisibility(View.INVISIBLE);
                    }

                }


                return false;
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        switch (call_activity) {
            case "Main":
                intent = new Intent(LoadGame.this, MainActivity.class);
                startActivity(intent);
                break;
            case "Game":
                intent = new Intent(LoadGame.this, GameActivity.class);
                intent.putExtra("FILE_NAME", previous_filename);
                startActivity(intent);
        }


        finish();
    }
}
