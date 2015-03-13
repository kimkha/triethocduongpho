package com.kimkha.triethocduongpho;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kimkha.triethocduongpho.backend.articleApi.model.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomGrid extends BaseAdapter {
    private static final int DEFAULT_IMG = R.drawable.no_image;

    private Context mContext;

    private final List<Article> articleList;


    public CustomGrid(Context c, List<Article> articleList) {
        mContext = c;
        this.articleList = articleList;
    }


    @Override
    public int getCount() {
        return articleList.size();
    }


    @Override
    public Object getItem(int position) {
        return articleList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return articleList.get(position).getId();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            //grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_single, null);
        } else {
            grid = convertView;
        }

        TextView textView = (TextView) grid.findViewById(R.id.grid_text);
        ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);
        //imageView.setImageResource(DEFAULT_IMG);
        textView.setText(articleList.get(position).getTitle());
        Picasso.with(mContext).load(articleList.get(position).getImgUrl())
                .resizeDimen(R.dimen.my_grid_image_size, R.dimen.my_grid_image_size).centerCrop()
                .placeholder(DEFAULT_IMG).error(DEFAULT_IMG).into(imageView);
        return grid;
    }
}