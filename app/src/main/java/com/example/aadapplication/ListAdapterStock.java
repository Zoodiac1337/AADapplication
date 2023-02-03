package com.example.aadapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ListAdapterStock extends BaseAdapter{
    Context context;
    private final String [] name;
    private final int [] quantity;
    private final Date[] date1;
    private final Date[] date2;

    public ListAdapterStock(Context context, String [] names, int [] quantities, Date[] date1, Date [] date2){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.name = names;
        this.quantity = quantities;
        this.date1 = date1;
        this.date2 = date2;
    }

    @Override
    public int getCount() {
        return name.length;
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
            convertView = inflater.inflate(R.layout.single_list_item_stock, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.ItemName);
            viewHolder.intQuantity = (TextView) convertView.findViewById(R.id.Quantity);
            viewHolder.txtDate1 = (TextView) convertView.findViewById(R.id.Date1);
            viewHolder.txtDate2 = (TextView) convertView.findViewById(R.id.Date2);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/YYYY");

        viewHolder.txtName.setText(name[position]);
        viewHolder.intQuantity.setText("#"+ quantity[position]+" ");
        viewHolder.txtDate1.setText(sdf.format(date1[position])+"-");
        viewHolder.txtDate2.setText(sdf.format(date2[position]));

        return convertView;
    }

    private static class ViewHolder {

        TextView txtName;
        TextView intQuantity;
        TextView txtDate1;
        TextView txtDate2;

    }
}
