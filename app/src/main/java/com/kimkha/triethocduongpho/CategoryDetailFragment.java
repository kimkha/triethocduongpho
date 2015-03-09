package com.kimkha.triethocduongpho;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


import com.kimkha.triethocduongpho.backend.articleApi.ArticleApi;
import com.kimkha.triethocduongpho.backend.articleApi.model.Article;
import com.kimkha.triethocduongpho.data.Content;
import com.kimkha.triethocduongpho.data.MyArticleService;

import java.util.List;

/**
 * A fragment representing a single Page detail screen.
 * This fragment is either contained in a {@link CategoryListActivity}
 * in two-pane mode (on tablets) or a {@link CategoryDetailActivity}
 * on handsets.
 */
public class CategoryDetailFragment extends Fragment implements MyArticleService.ApiCallback {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private GridView gridView = null;
    private List<Article> articleList = null;
    private boolean readyForGrid = false;

    private Callbacks mCallbacks = sDummyCallbacks;

    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    public CategoryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO Choose type of category (default: Home)
        articleList = null;
        MyArticleService.getArticleList("", this);
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
        if (gridView != null && articleList != null) {
            CustomGrid adapter = new CustomGrid(getActivity(), articleList);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    mCallbacks.onItemSelected(articleList.get(position).getUrl());
                }
            });
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
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onArticleReady(Article article) {
    }

    @Override
    public void onArticleListReady(List<Article> articleList) {
        this.articleList = articleList;
        updateView();
    }

}
