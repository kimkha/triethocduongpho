package com.kimkha.triethocduongpho.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kimkha.triethocduongpho.R;
import com.kimkha.triethocduongpho.backend.articleApi.model.Article;
import com.kimkha.triethocduongpho.ui.MainFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public abstract class ArticleAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    protected static final int DEFAULT_IMG = R.drawable.no_image;
    protected final DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnFail(DEFAULT_IMG).showImageForEmptyUri(DEFAULT_IMG).showImageOnLoading(DEFAULT_IMG)
            .cacheInMemory(true).cacheOnDisk(true)
            .build();

    protected final List<Article> mArticleList = new ArrayList<>();
    private MainFragment.Callbacks mCallback;
    private String mNextPageToken;
    private String mCategory;
    private boolean loading = false;

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
    public long getItemId(int position) {
        return mArticleList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

    public void onChooseItem(int position) {
        if (position < mArticleList.size() && position >= 0) {
            Article article = mArticleList.get(position);
            mCallback.onItemSelected(article.getId(), article.getTitle(), article.getUrl(), article.getImgUrl());
        }
    }
}