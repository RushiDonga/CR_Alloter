package com.scupe.cralloter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class MainAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> cr_names;
    private ArrayList<Integer> cr_votes;

    public MainAdapter(Context c, ArrayList<String> cr_names, ArrayList<Integer> cr_votes){
        context = c;
        this.cr_names = cr_names;
        this.cr_votes = cr_votes;
    }

    @Override
    public int getCount() {
        return cr_names.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context
            .LAYOUT_INFLATER_SERVICE);
        }

        if(convertView == null){
            convertView = inflater.inflate(R.layout.custom_winner, null);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name_cr);
        TextView votes = (TextView) convertView.findViewById(R.id.votes_cr);

        name.setText(cr_names.get(position));
        votes.setText(Integer.toString(cr_votes.get(position)));

        return convertView;
    }
}
