package com.example.steven.rewrite_itechzero;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by steven on 2016/2/29.
 */
public class Google_adapter extends ArrayAdapter<Link>{

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Link link=getItem(position);
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.link_item,null);
        }
        TextView tv= (TextView) convertView.findViewById(R.id.link);
        tv.setText(link.getName());
        return convertView;
    }

    public Google_adapter(Context context, int resource,  ArrayList<Link> objects) {
        super(context, resource, objects);
    }
}
