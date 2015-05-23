package com.kimkha.triethocduongpho.data;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.kimkha.triethocduongpho.R;
import com.kimkha.triethocduongpho.backend.article2Api.model.Article;
import com.kimkha.triethocduongpho.ui.MainFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;
import java.util.List;

public abstract class ArticleAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    protected static final int DEFAULT_IMG = R.drawable.no_image;
    protected final DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnFail(DEFAULT_IMG).showImageForEmptyUri(DEFAULT_IMG).showImageOnLoading(DEFAULT_IMG)
            .cacheInMemory(true).cacheOnDisk(true)
            .build();
    private static final int DEFAULT_LIST_LIMIT = 20;

    protected final List<Article> mArticleList = new ArrayList<>();
    private Callbacks mCallback = sDummyCallbacks;
    private String mNextPageToken;
    private String mCategory;
    private long mFromTime = -1;
    private long mToTime = -1;
    private boolean loading = false;
    private boolean isEndOfList = false;

    public void setCallback(Callbacks callback) {
        mCallback = callback;
    }

    public void startLoader(String category, long fromTime, long toTime) {
        // Clear the list first
        mNextPageToken = null;
        isEndOfList = false;
        mCategory = category;
        mFromTime = fromTime;
        mToTime = toTime;
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

            MyArticle2Service.getArticleList(mCategory, mNextPageToken, DEFAULT_LIST_LIMIT, mFromTime, mToTime, new MyArticle2Service.ApiCallback() {
                @Override
                public void onArticleReady(Article article) {
                    // Do nothing
                }

                @Override
                public void onArticleListReady(List<Article> articleList, String nextPageToken) {
                    if (articleList == null || articleList.size() == 0) {
                        isEndOfList = true;
                    }

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

    public boolean isNoPost() {
        return isEndOfList && (mArticleList == null || mArticleList.size() == 0);
    }

    public boolean isEndOfList() {
        return isEndOfList && mArticleList != null && mArticleList.size() > 0;
    }

    public void onChooseItem(int position) {
        if (position < mArticleList.size() && position >= 0) {
            Article article = mArticleList.get(position);
            mCallback.onItemSelected(article.getId(), article.getTitle(), article.getUrl(), article.getImgUrl());
        }
    }

    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(Long id, String title, String url, String imgUrl);

        void onItemLoaded();

        void onItemLoading();
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Long id, String title, String url, String imgUrl) {
        }
        @Override
        public void onItemLoaded(){}

        @Override
        public void onItemLoading() {}
    };

}