package com.example.watsonashton_helpertracker;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.watsonashton_helpertracker.objects.Contacts;

import java.util.ArrayList;

public class ContactsAdapter extends BaseAdapter {
    public static final int ID_CONSTANT = 0x01000000;
    final ArrayList<Contacts> mCollection;
    final Context mContext;

    public ContactsAdapter(ArrayList<Contacts> mCollection, Context mContext){
        this.mCollection = mCollection;
        this.mContext = mContext;
    }
    @Override
    public int getCount() { return mCollection.size(); }

    @Override
    public Object getItem(int position) {
        if (mCollection != null && position >= 0 && position < mCollection.size()) {
            return mCollection.get(position);
        }
        else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) { return ID_CONSTANT + position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contacts contact = (Contacts) getItem(position);
        ViewHolder vh;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_person_list_view, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }
        else{
            vh = (ViewHolder)convertView.getTag();

        }
        if(contact != null){
            vh.name.setText(contact.getFullName());
            vh.number.setText(contact.getPhoneNum());

        }
        return  convertView;
    }

    static  class ViewHolder{
        final TextView name;
        final  TextView number;


        public ViewHolder(View _layout){
            name = _layout.findViewById(R.id.textViewContactListFullName);
            number = _layout.findViewById(R.id.textViewContactListPhoneNumber);

        }

    }

}
