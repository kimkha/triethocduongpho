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
import com.kimkha.triethocduongpho.backend.article2Api.model.Article;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author kimkha
 * @version 1.2
 * @since 5/10/15
 */
public class ArticleListAdapter extends ArticleAdapter<ArticleListAdapter.ViewHolder> {
    private final int mExpectHeightForBig;
    private final Context mContext;

    public ArticleListAdapter(Context context, int expectHeightForBig) {
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

        String img = MyArticle2Service.parseImageUrl(article.getImgUrl());
        ImageLoader.getInstance().displayImage(img, holder.mImageView, options);
    }

    private void displayBigStyle(ViewHolder holder, Article article) {
        holder.mBigTitleView.setText(article.getTitle());

        CharSequence timeSpanned = DateUtils.getRelativeTimeSpanString(
                article.getCreated().getValue(), System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS);
        holder.mBigSubView.setText(timeSpanned);

        String img = MyArticle2Service.parseImageUrl(article.getImgUrl());
        ImageLoader.getInstance().displayImage(img, holder.mBigImageView, options);
    }

}
