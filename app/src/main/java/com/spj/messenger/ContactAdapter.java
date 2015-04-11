package com.spj.messenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Stenal P Jolly on 11-Mar-15.
 */
public class ContactAdapter extends ArrayAdapter<UserDetails> {

    private ArrayList<UserDetails> detailsArrayList;

    public ContactAdapter(Context context, ArrayList<UserDetails> detailsArrayList) {
        super(context, 0);
        this.detailsArrayList = detailsArrayList;
    }

    @Override
    public int getCount() {
        return detailsArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_row, null);
        TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
        TextView txtNumber = (TextView) convertView.findViewById(R.id.txtNumber);
        UserDetails userDetails = detailsArrayList.get(position);
        txtName.setText(userDetails.getvUsername());
        txtNumber.setText(userDetails.getvPhone());

        return convertView;
    }
}
