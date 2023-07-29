package com.example.notipj;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

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
        //삭제 버튼을 누를시 실행되는 로직
        tvHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //얼럿 창을 띄워준다
                show(position);
            }
        });
        // Populate the data into the template view using the data object
        // Return the completed view to render on screen
        return convertView;
    }
    public void show(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("List Remove");
        builder.setMessage("Are you sure you want to remove it?");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //리스트 목록을 삭제 하며 Fragment의 로직을 실행
                ArrayList list = SharedStore.getStringArrayPref(getContext(),"filterkey");
                list.remove(position);
                SharedStore.setStringArrayPref(getContext(),"filterkey",list);
                ListFragment.notifyChange();
            }
        });
        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }
}
