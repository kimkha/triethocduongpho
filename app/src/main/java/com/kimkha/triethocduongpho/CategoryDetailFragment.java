package com.kimkha.triethocduongpho;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


import com.kimkha.triethocduongpho.backend.articleApi.model.Article;
import com.kimkha.triethocduongpho.data.MyArticleService;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Page detail screen.
 * This fragment is either contained in a {@link CategoryListActivity}
 * in two-pane mode (on tablets) or a {@link CategoryDetailActivity}
 * on handsets.
 */
public class CategoryDetailFragment extends Fragment implements MyArticleService.ApiCallback, EndlessScrollListener.RefreshList {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_CATEGORY = "category";

    private String category = "";
    private String nextPageToken = null;
    private GridView gridView = null;
    private List<Article> articleList = new ArrayList<>();
    private boolean readyForGrid = false;
    private CustomGrid adapter = null;
    private EndlessScrollListener scrollListener = null;

    private Callbacks mCallbacks = sDummyCallbacks;

    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(Long id, String title, String imgUrl);

        public void onItemLoaded();

        public void onItemLoading();
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Long id, String title, String imgUrl) {
        }
        @Override
        public void onItemLoaded(){}

        @Override
        public void onItemLoading() {}
    };

    public CategoryDetailFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_CATEGORY)) {
            category = getArguments().getString(ARG_CATEGORY);
        }

        loadListFromStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category_detail, container, false);
        gridView = (GridView) rootView.findViewById(R.id.grid);

        updateView();
        return rootView;
    }

    private void updateView() {
        if (gridView != null && readyForGrid) {
            if (adapter == null) {
                adapter = new CustomGrid(getActivity(), articleList);
                gridView.setAdapter(adapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Article article = articleList.get(position);
                        mCallbacks.onItemSelected(article.getId(), article.getTitle(), article.getImgUrl());
                    }
                });

                scrollListener = new EndlessScrollListener(gridView, this);
                gridView.setOnScrollListener(scrollListener);

            } else {
                adapter.notifyDataSetChanged();
                if (this.nextPageToken == null || "".equals(this.nextPageToken.trim())) {
                    scrollListener.noMorePages();
                } else {
                    scrollListener.notifyMorePages();
                }
            }

            mCallbacks.onItemLoaded();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;

        category = getString(R.string.category_def);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    public void loadListFromStart() {
        articleList.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        readyForGrid = false;
        MyArticleService.getArticleList(category, nextPageToken, this);
    }

    @Override
    public void onScrollNextPage() {
        readyForGrid = false;
        mCallbacks.onItemLoading();
        MyArticleService.getArticleList(category, nextPageToken, this);
    }

    @Override
    public void onArticleReady(Article article) {
    }

    @Override
    public void onArticleListReady(List<Article> articleList, String nextPageToken) {
        if (articleList != null && articleList.size() > 0) {
            this.articleList.addAll(articleList);
        }

        this.nextPageToken = nextPageToken;

        readyForGrid = true;// Make articleList ready
        updateView();
    }

}
