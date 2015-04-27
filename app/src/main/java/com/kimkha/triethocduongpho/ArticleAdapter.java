package com.kimkha.triethocduongpho;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kimkha.triethocduongpho.backend.articleApi.model.Article;
import com.kimkha.triethocduongpho.data.MyArticleService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private static final int DEFAULT_IMG = R.drawable.no_image;

    private final List<Article> mArticleList = new ArrayList<>();
    private final Context mContext;
    private MainFragment.Callbacks mCallback;
    private String mNextPageToken;
    private String mCategory;
    private boolean loading = false;

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

    public void setCallback(MainFragment.Callbacks callback) {
        mCallback = callback;
    }

    public void startLoader(String category) {
        // Clear the list first
        mNextPageToken = null;
        mCategory = category;
        mArticleList.clear();
        notifyDataSetChanged();

        loadCurrentPage();
    }

    public void goNext() {
        // Everything was prepared, just go
        loadCurrentPage();
    }

    private void loadCurrentPage() {
        if (!loading) {
            // Mark it as loading... Please wait
            loading = true;
            notifyLoading();

            MyArticleService.getArticleList(mCategory, mNextPageToken, new MyArticleService.ApiCallback() {
                @Override
                public void onArticleReady(Article article) {
                    // Do nothing
                }

                @Override
                public void onArticleListReady(List<Article> articleList, String nextPageToken) {
                    if (articleList != null && articleList.size() > 0) {
                        // Exist the list to append
                        mArticleList.addAll(articleList);
                        mNextPageToken = nextPageToken;
                        notifyDataSetChanged();
                    }

                    // Mark it as load finished... Can go next
                    loading = false;
                    notifyLoaded();
                }
            });
        }
    }

    private void notifyLoading() {
        mCallback.onItemLoading();
    }

    private void notifyLoaded() {
        mCallback.onItemLoaded();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View grid = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_single, null);

        return new ViewHolder(grid);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = mArticleList.get(position);
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
        return mArticleList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

}