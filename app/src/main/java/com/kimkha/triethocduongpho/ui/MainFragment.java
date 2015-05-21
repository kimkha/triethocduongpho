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
import android.widget.TextView;
import android.widget.Toast;

import com.kimkha.triethocduongpho.app.MainActivity;
import com.kimkha.triethocduongpho.data.ArticleAdapter;
import com.kimkha.triethocduongpho.R;
import com.kimkha.triethocduongpho.data.ArticleGridAdapter;
import com.kimkha.triethocduongpho.data.ArticleListAdapter;

/**
 * @author kimkha
 * @version 0.1
 * @since 3/14/15
 */
public class MainFragment extends Fragment implements ArticleAdapter.Callbacks {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_CATEGORY = "category";

    private int expectHeightForBig = 200;
    private String category = "";
    private TextView mNoPostView;
    private RecyclerView mRecyclerView = null;
    private RecyclerView.LayoutManager mLayoutManager;
    private StaggeredGridLayoutManager gridLayoutManager;
    private LinearLayoutManager listLayoutManager;
    private ArticleAdapter adapter = null;
    private boolean isGridMode = false;

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
        mNoPostView = (TextView) rootView.findViewById(R.id.no_post);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        switchLayout(false);

        triggerEvents();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        category = "";
    }

    public void switchLayout(boolean isGridMode) {
        this.isGridMode = isGridMode;

        mLayoutManager = getLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        adapter = getAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);
        adapter.setCallback(this);

        adapter.startLoader(category);
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

                if (adapter.isEndOfList()) {
                    // End of list, don't need to get more
                    return;
                }

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

    @Override
    public void onItemSelected(Long id, String title, String url, String imgUrl) {
        ((MainActivity) getActivity()).onItemSelected(id, title, url, imgUrl);
    }

    @Override
    public void onItemLoaded() {
        ((MainActivity) getActivity()).setRefreshActionButtonState(false);
        if (adapter.isNoPost()) {
            mRecyclerView.setVisibility(View.GONE);
            mNoPostView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoPostView.setVisibility(View.GONE);
        }

        if (adapter.isEndOfList()) {
            Toast.makeText(getActivity(), R.string.end_of_list, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemLoading() {
        ((MainActivity) getActivity()).setRefreshActionButtonState(true);
    }
}
