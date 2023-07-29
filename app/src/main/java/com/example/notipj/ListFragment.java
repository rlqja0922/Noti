package com.example.notipj;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;


public class ListFragment extends Fragment  implements MainActivity.onBackPressedListener {
    public View v;
    public static Filter_adapter itemsAdapter;
    public static ArrayList<String> filterlist;
    public static ListView listView;
    public TextView filter_modbt;
    public MainActivity mainActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_list, container, false);
        // 먼저 리소스 파일인 Arraylist를 만든다.
        filterlist = SharedStore.getStringArrayPref(getContext(), "filterkey");
        itemsAdapter = (Filter_adapter) new Filter_adapter(getContext(), filterlist);


        //아까 만든 xml파일에 있는 listView를 불러온다.
        listView = (ListView) v.findViewById(R.id.filter_listview);
        filter_modbt = v.findViewById(R.id.filter_modbt);
        filter_modbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterlist = SharedStore.getStringArrayPref(getContext(), "filterkey");
                itemsAdapter.notifyDataSetChanged();
                onBackPressed();
            }
        });
        //SetAdapter를 이용해 ListView와 ArrayAdapter를 연결한다.

        listView.setAdapter(itemsAdapter);

        return v;
    }
    public static void noti(){
        filterlist.clear();
        ArrayList list = SharedStore.getStringArrayPref(itemsAdapter.getContext(), "filterkey");
        filterlist.addAll(list);
        itemsAdapter = (Filter_adapter) new Filter_adapter(itemsAdapter.getContext(), filterlist);
        itemsAdapter.notifyDataSetChanged();
        listView.setAdapter(itemsAdapter);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(ListFragment.this).commit();
        fragmentManager.beginTransaction();
    }
}