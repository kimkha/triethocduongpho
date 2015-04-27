package com.kimkha.triethocduongpho;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kimkha.triethocduongpho.backend.articleApi.model.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private static final int DEFAULT_IMG = R.drawable.no_image;

    private final List<Article> articleList = new ArrayList<>();
    private final Context mContext;

    public ArticleAdapter(Context context) {
        mContext = context;
    }

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

    public void appendArticleList(List<Article> articleList) {
        this.articleList.addAll(articleList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View grid = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_single, null);

        return new ViewHolder(grid);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.mTextView.setText(article.getTitle());
        String img = article.getImgUrl();
        if (img != null && img.startsWith("/triethocduongpho-android")) {
            img = "http://storage.googleapis.com" + img;
        }
        Picasso.with(mContext).load(img)
                .placeholder(DEFAULT_IMG).error(DEFAULT_IMG).into(holder.mImageView);
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