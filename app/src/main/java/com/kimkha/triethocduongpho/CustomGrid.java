package com.kimkha.triethocduongpho;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kimkha.triethocduongpho.backend.articleApi.model.Article;
import com.kimkha.triethocduongpho.data.Content;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomGrid extends BaseAdapter {

    private Context mContext;

    private final List<Article> content;


    public CustomGrid(Context c, List<Article> content) {
        mContext = c;
        this.content = content;
    }


    @Override
    public int getCount() {
        return content.size();
    }


    @Override
    public Object getItem(int position) {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            //grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_single, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);
            textView.setText(content.get(position).getTitle());
            Picasso.with(mContext).load(content.get(position).getImgUrl()).into(imageView);
        } else {
            grid = convertView;
        }
        return grid;
    }
}