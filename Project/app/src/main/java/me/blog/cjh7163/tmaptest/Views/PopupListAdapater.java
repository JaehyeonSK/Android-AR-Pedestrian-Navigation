package me.blog.cjh7163.tmaptest.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import me.blog.cjh7163.tmaptest.R;

/**
 * Created by david2 on 2017-10-24.
 */

public class PopupListAdapater extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<PopupListItem> itemList;
    int layout;

    public PopupListAdapater(Context context, int layout, ArrayList<PopupListItem> list) {
        this.context = context;
        this.layout = layout;
        this.itemList = list;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int i) {
        return itemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(layout, viewGroup, false);
        }

        TextView mainText = (TextView)view.findViewById(R.id.item_main_text);
        TextView subText = (TextView)view.findViewById(R.id.item_sub_text);
        ImageView imageStatus = (ImageView)view.findViewById(R.id.image_status);

        PopupListItem item = itemList.get(position);
        mainText.setText(item.mainText);
        subText.setText(item.subText);

        if (item.walk) {
            imageStatus.setImageDrawable(context.getDrawable(R.drawable.walk));
            
        }

        return view;
    }
}
