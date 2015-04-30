package com.kimkha.triethocduongpho.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kimkha.triethocduongpho.R;

import java.util.Arrays;
import java.util.List;

/**
 * @author kimkha
 * @version 0.2
 * @since 4/29/15
 */
public class NavigationAdapter extends BaseAdapter {

    private final List<String> mListItems;
    private final Context mContext;
    private final LayoutInflater mInflater;

    public NavigationAdapter(Context context, String[] objects) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListItems = Arrays.asList(objects);
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder vh;
        String item = mListItems.get(position);
        if (convertView == null) {
            vh = new ViewHolder();
            view = mInflater.inflate(R.layout.navi_item, parent, false);
            vh.textView = (TextView) view.findViewById(R.id.text);
            vh.divider = view.findViewById(R.id.divider);

            view.setTag(vh);
        } else {
            view = convertView;
            vh = (ViewHolder) view.getTag();
        }

        vh.textView.setText(item);

        if (item != null && item.startsWith("---")) {
            vh.textView.setVisibility(View.GONE);
            vh.divider.setVisibility(View.VISIBLE);
        } else {
            vh.textView.setVisibility(View.VISIBLE);
            vh.divider.setVisibility(View.GONE);
        }
        return view;
    }

    private class ViewHolder {
        public TextView textView;
        public View divider;
    }
}
