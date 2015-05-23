package com.kimkha.triethocduongpho.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.kimkha.triethocduongpho.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author kimkha
 * @version 2.0
 * @since 5/23/15
 */
public class MonthYearAdapter extends BaseAdapter {
    private final List<Calendar> listMonth = new ArrayList<>();
    private final Context context;

    public MonthYearAdapter(Context context) {
        this.context = context;

        Calendar now = Calendar.getInstance();
        now.set(Calendar.MILLISECOND, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.DAY_OF_MONTH, 1);

        Calendar target = Calendar.getInstance();
        target.set(Calendar.MILLISECOND, 0);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.HOUR_OF_DAY, 0);
        target.set(Calendar.DAY_OF_MONTH, 1);
        target.set(Calendar.MONTH, Calendar.JULY);
        target.set(Calendar.YEAR, 2013);

        while (now.after(target)) {
            listMonth.add((Calendar) now.clone());
            now.add(Calendar.MONTH, -1);
        }
    }

    @Override
    public int getCount() {
        return listMonth.size();
    }

    @Override
    public Object getItem(int position) {
        return listMonth.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.select_dialog_singlechoice, null);
            vh = new VH();
            vh.textView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(vh);
        } else {
            vh = (VH) convertView.getTag();
        }

        Calendar item = listMonth.get(position);
        String value = context.getResources().getString(R.string.month_format, item.get(Calendar.MONTH)+1, item.get(Calendar.YEAR));
        vh.textView.setText(value);
        return convertView;
    }

    public int findMatchedPosition(Calendar date) {
        if (date == null) {
            return -1;
        }

        int pos=0;
        for (int i=0; i<listMonth.size(); i++) {
            if (date.before(listMonth.get(i))) {
                pos = i+1;
                break;
            }
        }

        if (pos >= listMonth.size() || pos < 0) {
            return -1;//Not found
        }
        return pos;
    }

    class VH {
        public CheckedTextView textView;
    }
}
