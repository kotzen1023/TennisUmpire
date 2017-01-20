package com.seventhmoon.tennisumpire.Data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seventhmoon.tennisumpire.R;

import java.util.ArrayList;


public class ServeAdapter extends ArrayAdapter<ServeItem> {
    public static final String TAG = ServeAdapter.class.getName();
    //private Context context;
    private LayoutInflater inflater = null;

    private int layoutResourceId;
    private ArrayList<ServeItem> items = new ArrayList<>();

    public ServeAdapter(Context context, int textViewResourceId,
                        ArrayList<ServeItem> objects) {
        super(context, textViewResourceId, objects);
        //this.context = context;
        this.layoutResourceId = textViewResourceId;
        this.items = objects;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return items.size();

    }

    public ServeItem getItem(int position)
    {
        return items.get(position);
    }

    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.e(TAG, "getView = " + position);
        View view;
        ViewHolder holder;


        if (convertView == null || convertView.getTag() == null) {
            /*LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();

            holder.jid = (TextView) convertView.findViewById(R.id.contact_jid);
            holder.avatar = (ImageView) convertView.findViewById(R.id.contact_icon);
            convertView.setTag(holder);*/
            view = inflater.inflate(layoutResourceId, null);
            holder = new ViewHolder(view);
            view.setTag(holder);


        } else {
            view = convertView;
            //Log.e(TAG, "here!");
            holder = (ViewHolder) view.getTag();
        }


        ServeItem item = items.get(position);


        if (item != null) {
            holder.color.setBackgroundColor(item.getColor());
            holder.text.setText(item.getText());

        }


        return view;
    }

    class ViewHolder {

        ImageView color;
        TextView text;

        public ViewHolder(View view) {

            this.color = (ImageView) view.findViewById(R.id.color_serve);
            this.text = (TextView) view.findViewById(R.id.text_serve);
        }


    }
}
