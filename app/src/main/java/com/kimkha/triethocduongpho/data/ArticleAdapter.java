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

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private static final int DEFAULT_IMG = R.drawable.no_image;
    private final DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnFail(DEFAULT_IMG).showImageForEmptyUri(DEFAULT_IMG).showImageOnLoading(DEFAULT_IMG)
            .build();

    private final List<Article> mArticleList = new ArrayList<>();
    private final Context mContext;
    private final int mExpectHeightForBig;
    private MainFragment.Callbacks mCallback;
    private String mNextPageToken;
    private String mCategory;
    private boolean loading = false;

    public ArticleAdapter(Context context, int expectHeightForBig) {
        mContext = context;
        mExpectHeightForBig = expectHeightForBig;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // This for normal style
        public View normalWrapView;
        public TextView mTextView;
        public ImageView mImageView;
        public TextView mSubTextView;

        public View bigWrapView;
        public TextView mBigTitleView;
        public ImageView mBigImageView;
        public TextView mBigSubView;

        public ViewHolder(View view, int expectHeightForBig) {
            super(view);
            normalWrapView = view.findViewById(R.id.normal_style);
            mTextView = (TextView) view.findViewById(R.id.grid_text);
            mImageView = (ImageView) view.findViewById(R.id.grid_image);
            mSubTextView = (TextView) view.findViewById(R.id.sub_text);

            bigWrapView = view.findViewById(R.id.big_style);
            mBigTitleView = (TextView) view.findViewById(R.id.big_title);
            mBigImageView = (ImageView) view.findViewById(R.id.big_image);
            mBigSubView = (TextView) view.findViewById(R.id.big_sub);

            View contentView = view.findViewById(R.id.big_content_view);
            contentView.getLayoutParams().height = expectHeightForBig;
            mBigImageView.getLayoutParams().height = expectHeightForBig;
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
        View grid = LayoutInflater.from(mContext).inflate(R.layout.list_single, null);

        return new ViewHolder(grid, mExpectHeightForBig);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = mArticleList.get(position);
        if (article.getStyle() == 0) {
            holder.bigWrapView.setVisibility(View.GONE);
            holder.normalWrapView.setVisibility(View.VISIBLE);
            displayNormalStyle(holder, article);
        } else {
            holder.bigWrapView.setVisibility(View.VISIBLE);
            holder.normalWrapView.setVisibility(View.GONE);
            displayBigStyle(holder, article);
        }
    }

    private void displayNormalStyle(ViewHolder holder, Article article) {
        holder.mTextView.setText(article.getTitle());

        CharSequence timeSpanned = DateUtils.getRelativeTimeSpanString(
                article.getCreated().getValue(), System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS);
        holder.mSubTextView.setText(timeSpanned);

        String img = MyArticleService.parseImageUrl(article.getImgUrl());
        ImageLoader.getInstance().displayImage(img, holder.mImageView, options);
    }

    private void displayBigStyle(ViewHolder holder, Article article) {
        holder.mBigTitleView.setText(article.getTitle());

        CharSequence timeSpanned = DateUtils.getRelativeTimeSpanString(
                article.getCreated().getValue(), System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS);
        holder.mBigSubView.setText(timeSpanned);

        String img = MyArticleService.parseImageUrl(article.getImgUrl());
        ImageLoader.getInstance().displayImage(img, holder.mBigImageView, options);
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
            mCallback.onItemSelected(article.getId(), article.getTitle(), article.getImgUrl());
        }
    }
}