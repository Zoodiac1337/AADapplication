package com.example.aadapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapterAccess extends BaseAdapter{
    Context context;
    private final String [] userName;
    private final String[] userEmail;
    private final String[] userType;

    public ListAdapterAccess(Context context, String [] userNames, String [] userEmails, String [] userTypes){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.userName = userNames;
        this.userEmail = userEmails;
        this.userType = userTypes;
    }

    @Override
    public int getCount() {
        return userName.length;
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
            convertView = inflater.inflate(R.layout.single_list_item_access, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.userName);
            viewHolder.txtEmail = (TextView) convertView.findViewById(R.id.userEmail);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.userType);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(userName[position]);
        viewHolder.txtEmail.setText(userEmail[position]);
        viewHolder.txtType.setText(userType[position]);

        return convertView;
    }

    private static class ViewHolder {

        TextView txtName;
        TextView txtEmail;
        TextView txtType;

    }
}
