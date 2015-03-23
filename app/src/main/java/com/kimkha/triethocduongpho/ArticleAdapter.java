package com.kimkha.triethocduongpho;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kimkha.triethocduongpho.backend.articleApi.model.Article;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private static final int DEFAULT_IMG = R.drawable.no_image;

    private final List<Article> articleList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.grid_text);
            mImageView = (ImageView) view.findViewById(R.id.grid_image);
        }
    }

    public ArticleAdapter(List<Article> articleList) {
        this.articleList = articleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View grid = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_single, null);

        return new ViewHolder(grid);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(articleList.get(position).getTitle());
//        Picasso.with(mContext).load(articleList.get(position).getImgUrl())
//                .placeholder(DEFAULT_IMG).error(DEFAULT_IMG).into(holder.mImageView);
    }

    @Override
    public long getItemId(int position) {
        return articleList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

}