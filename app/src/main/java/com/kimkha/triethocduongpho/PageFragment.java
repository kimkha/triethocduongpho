package com.kimkha.triethocduongpho;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kimkha.triethocduongpho.backend.articleApi.model.Article;
import com.kimkha.triethocduongpho.data.Content;
import com.kimkha.triethocduongpho.data.MyArticleService;

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

    private Article article;
    private HtmlTextView htmlTextView;

    public PageFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            Long id = getArguments().getLong(ARG_ITEM_ID);

            MyArticleService.getArticle(id, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        htmlTextView = (HtmlTextView) rootView.findViewById(R.id.page_content);

        updateView();
        return rootView;
    }

    private void updateView() {
        if (article != null && htmlTextView != null) {
            htmlTextView.setHtmlFromString(article.getFullContent().getValue(), false);
        }
    }

    @Override
    public void onArticleReady(Article article) {
        this.article = article;
        updateView();
    }

    @Override
    public void onArticleListReady(List<Article> articleList) {

    }
}
