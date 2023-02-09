package com.example.aadapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ListAdapterStock2 extends BaseAdapter{
    Context context;
    private final String [] insertedBy;
    private final Date[] insertedOn;
    private final Date[] expiryDate;

    public ListAdapterStock2(Context context, String [] names, Date[] date1, Date [] date2){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.insertedBy = names;
        this.insertedOn = date1;
        this.expiryDate = date2;
    }

    @Override
    public int getCount() {
        return insertedBy.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {

        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.single_list_item_stock2, parent, false);
            viewHolder.txtInsertedOn = (TextView) convertView.findViewById(R.id.InsertedOn);
            viewHolder.txtInsertedBy = (TextView) convertView.findViewById(R.id.InsertedBy);
            viewHolder.txtExpiryDate = (TextView) convertView.findViewById(R.id.ExpiryDate);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("E, dd/MM");
        SimpleDateFormat sd2 = new SimpleDateFormat("dd/MM HH:mm");

        viewHolder.txtInsertedBy.setText(insertedBy[position]);
        viewHolder.txtInsertedOn.setText(sd2.format(insertedOn[position]));
        viewHolder.txtExpiryDate.setText(sdf.format(expiryDate[position]));

        return convertView;
    }

    private static class ViewHolder {

        TextView txtInsertedOn;
        TextView txtInsertedBy;
        TextView txtExpiryDate;

    }
}
