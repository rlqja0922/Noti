package com.example.notipj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Filter_adapter extends ArrayAdapter {
    public Filter_adapter(@NonNull Context context, ArrayList resource) {
        super(context,0 ,resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listadapter, parent, false);
        }
        ArrayList list = SharedStore.getStringArrayPref(getContext(),"filterkey");
        String filter = (String) getItem(position);
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.filter_text);
        ImageView tvHome = (ImageView) convertView.findViewById(R.id.filter_delete);
        tvName.setText(filter);
        tvHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(position);
                SharedStore.setStringArrayPref(getContext(),"filterkey",list);
            }
        });
        // Populate the data into the template view using the data object
        // Return the completed view to render on screen
        return convertView;
    }
}
