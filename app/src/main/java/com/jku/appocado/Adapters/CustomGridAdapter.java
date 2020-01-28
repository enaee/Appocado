package com.jku.appocado.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jku.appocado.Models.Habit;
import com.jku.appocado.R;

import java.util.ArrayList;

public class CustomGridAdapter extends ArrayAdapter<Habit> {


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
        TextView textView = view.findViewById(R.id.gridText);
        ImageView imageView = view.findViewById(R.id.gridImage);
        textView.setText(getItem(position).getName());
        Glide.with(imageView.getContext()).load(getItem(position).getImage()).into(imageView);

        return view;

    }


}
