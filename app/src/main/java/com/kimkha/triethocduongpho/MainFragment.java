package com.kimkha.triethocduongpho;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kimkha.triethocduongpho.backend.articleApi.model.Article;
import com.kimkha.triethocduongpho.data.MyArticleService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kimkha
 * @version 0.1
 * @since 3/14/15
 */
public class MainFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_CATEGORY = "category";

    private String category = "";
    private RecyclerView mRecyclerView = null;
    private StaggeredGridLayoutManager mLayoutManager;
    private ArticleAdapter adapter = null;

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

            if (category == null || category.equalsIgnoreCase(getString(R.string.category_all))) {
                category = "";
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new StaggeredGridLayoutManager(getResources().getInteger(R.integer.num_of_column), StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        triggerEvents();

        adapter = new ArticleAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);

        adapter.setCallback(mCallbacks);
        adapter.startLoader(category);

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

    public void cleanAndReload() {
        adapter.startLoader(category);
    }

    private void triggerEvents() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int[] pastVisiblesItems = mLayoutManager.findFirstVisibleItemPositions(null);

                if (pastVisiblesItems.length > 0) {
                    if ( (visibleItemCount+pastVisiblesItems[0]) >= totalItemCount) {
                        // This is last item
                        adapter.goNext();
                    }
                }
            }
        });

    }

}
