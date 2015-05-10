package com.kimkha.triethocduongpho.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kimkha.triethocduongpho.data.ArticleAdapter;
import com.kimkha.triethocduongpho.R;
import com.kimkha.triethocduongpho.data.ArticleGridAdapter;
import com.kimkha.triethocduongpho.data.ArticleListAdapter;

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

    private int expectHeightForBig = 200;
    private String category = "";
    private RecyclerView mRecyclerView = null;
    private RecyclerView.LayoutManager mLayoutManager;
    private StaggeredGridLayoutManager gridLayoutManager;
    private LinearLayoutManager listLayoutManager;
    private ArticleAdapter adapter = null;
    private boolean isGridMode = false;

    private Callbacks mCallbacks = sDummyCallbacks;

    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(Long id, String title, String url, String imgUrl);

        public void onItemLoaded();

        public void onItemLoading();
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

        int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        screenWidth = screenWidth - 2*getResources().getDimensionPixelSize(R.dimen.my_text_margin);
        expectHeightForBig = screenWidth*9/16;

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

        mLayoutManager = getLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        adapter = getAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);
        adapter.setCallback(mCallbacks);

        triggerEvents();

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

        category = "";
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

    private ArticleAdapter getAdapter(Activity activity) {
        if (isGridMode) {
            return new ArticleGridAdapter(activity);
        } else {
            return new ArticleListAdapter(activity, expectHeightForBig);
        }
    }

    private RecyclerView.LayoutManager getLayoutManager(Activity activity) {
        if (isGridMode) {
            listLayoutManager = null;
            gridLayoutManager = new StaggeredGridLayoutManager(activity.getResources().getInteger(R.integer.num_of_column), StaggeredGridLayoutManager.VERTICAL);
            return gridLayoutManager;
        } else {
            listLayoutManager = new LinearLayoutManager(activity);
            gridLayoutManager = null;
            return listLayoutManager;
        }
    }

    private void triggerEvents() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();

                if (isGridMode) {
                    int[] pastVisibleItems = gridLayoutManager.findFirstVisibleItemPositions(null);

                    if (pastVisibleItems.length > 0) {
                        if ( (visibleItemCount+pastVisibleItems[0]) >= totalItemCount) {
                            // This is last item
                            adapter.goNext();
                        }
                    }
                } else {
                    int lastVisibleItems = listLayoutManager.findFirstVisibleItemPosition();
                    if (lastVisibleItems+visibleItemCount >= totalItemCount) {
                        // This is last item
                        adapter.goNext();
                    }
                }
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                adapter.onChooseItem(position);
            }
        }));
    }

}
