package com.scupe.cralloter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Custom_Spinner_Adapter extends ArrayAdapter<Custom_Spinner_Items> {



    public Custom_Spinner_Adapter(@NonNull Context context, ArrayList<Custom_Spinner_Items> customList) {
        super(context, 0,  customList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return CustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return CustomView(position, convertView, parent);
    }

    public View CustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

        if(convertView == null){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_layout, parent, false);
        }

        Custom_Spinner_Items items = getItem(position);
        ImageView spinnerImage = convertView.findViewById(R.id.customSpinnerIcon);
        TextView spinnerName = convertView.findViewById(R.id.customSpinnerText);

        if(items != null){

            spinnerImage.setImageResource(items.getSpinnerImage());
            spinnerName.setText(items.getSpinnerText());
        }

        return  convertView;
    }
}
