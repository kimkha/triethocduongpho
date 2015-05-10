package com.kimkha.triethocduongpho.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kimkha.triethocduongpho.R;
import com.kimkha.triethocduongpho.backend.articleApi.model.Article;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author kimkha
 * @version 1.2
 * @since 5/10/15
 */
public class ArticleGridAdapter extends ArticleAdapter<ArticleGridAdapter.ViewHolder> {
    private final Context mContext;

    public ArticleGridAdapter(Context context) {
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // This for normal style
        public TextView mTextView;
        public ImageView mImageView;
//        public TextView mSubTextView;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.grid_text);
            mImageView = (ImageView) view.findViewById(R.id.grid_image);
//            mSubTextView = (TextView) view.findViewById(R.id.sub_text);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View grid = LayoutInflater.from(mContext).inflate(R.layout.grid_single, null);

        return new ViewHolder(grid);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = mArticleList.get(position);

        holder.mTextView.setText(article.getTitle());
        String img = MyArticleService.parseImageUrl(article.getImgUrl());
        ImageLoader.getInstance().displayImage(img, holder.mImageView, options);
    }

}
