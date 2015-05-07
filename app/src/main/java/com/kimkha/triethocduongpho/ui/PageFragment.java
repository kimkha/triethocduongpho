package com.kimkha.triethocduongpho.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kimkha.triethocduongpho.R;
import com.kimkha.triethocduongpho.app.PageActivity;
import com.kimkha.triethocduongpho.backend.articleApi.model.Article;
import com.kimkha.triethocduongpho.data.MyArticleService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.List;

/**
 * @author kimkha
 * @since 3/1/15
 * @version 0.1
 */
public class PageFragment extends Fragment implements MyArticleService.ApiCallback {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_TITLE = "item_title";
    public static final String ARG_ITEM_IMG = "item_img";

    private final DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true)
            .build();

    private int startAlpha = 0;
    private int endAlpha = 1;
    private int endTitle = 1;

    private String imgUrl;
    private String title;
    private Article article;
    private PageActivity mActivity;
    private ScrollView scrollView;
    private HtmlTextView htmlTextView;
    private ImageView imageView;
    private TextView headerView;
    private TextView subHeaderView;
    private View headGroup;

    public PageFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            Long id = getArguments().getLong(ARG_ITEM_ID);
            title = getArguments().getString(ARG_ITEM_TITLE);
            imgUrl = getArguments().getString(ARG_ITEM_IMG);
            imgUrl = MyArticleService.parseImageUrl(imgUrl);

            MyArticleService.getArticle(id, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        scrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);
        htmlTextView = (HtmlTextView) rootView.findViewById(R.id.page_content);
        imageView = (ImageView) rootView.findViewById(R.id.page_image);
        headerView = (TextView) rootView.findViewById(R.id.page_header);
        subHeaderView = (TextView) rootView.findViewById(R.id.page_subheader);
        headGroup = rootView.findViewById(R.id.page_head_group);

        listenScroll();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        headerView.setText(title);
        subHeaderView.setText("");
        ImageLoader.getInstance().displayImage(imgUrl, imageView, options);
        updateView();

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActivity = (PageActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must be PageActivity.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    private void listenScroll() {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                Log.e("AAA", "scrollY " + scrollY);
                changeToolbarUI(scrollY);
            }
        });
    }

    private void changeToolbarUI(int scrollY) {
        if (endAlpha <= 1) {
            // Not detect height yet.
            int toolbarHeight = (int) getResources().getDimension(R.dimen.my_min_height);
            endAlpha = Math.max(1, imageView.getHeight() - toolbarHeight);
            startAlpha = endAlpha/2;
            endTitle = Math.max(1, imageView.getHeight() + headGroup.getHeight() - toolbarHeight);
//            endTitle = endAlpha;
        }

        if (mActivity != null) {
//            if (scrollY >= endAlpha || endAlpha <= startAlpha) {
//                mActivity.makeTransparentToolbar(255);
//            } else if (scrollY <= startAlpha) {
//                mActivity.makeTransparentToolbar(0);
//            } else {
//                int deltaY = scrollY - startAlpha;
//                int factor = endAlpha - startAlpha;
//                mActivity.makeTransparentToolbar(deltaY*255/factor);
//            }
            mActivity.makeTransparentToolbar(scrollY >= endTitle?255:0);
            mActivity.setTitleIsShow(scrollY >= endTitle);
        }
    }

    private void updateView() {
        if (article != null && htmlTextView != null) {
            htmlTextView.setHtmlFromString(article.getFullContent().getValue(), false, MyArticleService.getImgBase());
            CharSequence timeSpanned = DateUtils.getRelativeTimeSpanString(
                    article.getCreated().getValue(), System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS);
            subHeaderView.setText(timeSpanned);

            changeToolbarUI(0);
        }
    }

    @Override
    public void onArticleReady(Article article) {
        this.article = article;
        updateView();
    }

    @Override
    public void onArticleListReady(List<Article> articleList, String nextPageToken) {

    }
}
