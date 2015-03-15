package com.kimkha.triethocduongpho;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
 * @author kimkha
 * @version 0.1
 * @since 3/14/15
 */
public class MainFragment extends Fragment implements MyArticleService.ApiCallback, EndlessScrollListener.RefreshList {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_CATEGORY = "category";

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

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainFragment newInstance(String category) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.grid_main);

        updateView();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallbacks = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement fragment's callbacks.");
        }

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
        nextPageToken = null;
        readyForGrid = false;
        MyArticleService.getArticleList(category, nextPageToken, this);
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
