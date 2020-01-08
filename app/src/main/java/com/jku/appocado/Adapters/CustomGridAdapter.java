package com.jku.appocado.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jku.appocado.Models.GridItem;
import com.jku.appocado.R;

import java.util.ArrayList;

public class CustomGridAdapter extends ArrayAdapter <GridItem> {

    private TextView habitName;

    public CustomGridAdapter(Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);

    }
    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.grid_view_item, null);
        TextView textView = (TextView) view.findViewById(R.id.gridText);
        ImageView imageView = (ImageView) view.findViewById(R.id.gridImage);
        textView.setText(getItem(position).getHabitName());

        return view;

    }



}
