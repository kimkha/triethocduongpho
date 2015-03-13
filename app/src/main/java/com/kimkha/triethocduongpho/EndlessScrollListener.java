package com.kimkha.triethocduongpho;

import android.util.Log;
import android.widget.AbsListView;
import android.widget.GridView;

/**
 * @author kimkha
 * @version 0.1
 * @since 3/11/15
 */
public class EndlessScrollListener implements AbsListView.OnScrollListener {

    private GridView gridView;
    private boolean isLoading;
    private boolean hasMorePages;
    private int pageNumber = 0;
    private RefreshList refreshList;
    private boolean isRefreshing;

    public EndlessScrollListener(GridView gridView, RefreshList refreshList) {
        this.gridView = gridView;
        this.isLoading = false;
        this.hasMorePages = true;
        this.refreshList = refreshList;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (gridView.getLastVisiblePosition() + 1 >= totalItemCount && !isLoading) {
            isLoading = true;
            if (hasMorePages && !isRefreshing) {
                isRefreshing = true;
                refreshList.onScrollNextPage();
            }
        } else {
            isLoading = false;
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    public void noMorePages() {
        this.hasMorePages = false;
    }

    public void notifyMorePages() {
        isRefreshing = false;
        pageNumber = pageNumber + 1;
    }

    public interface RefreshList {
        public void onScrollNextPage();
    }
}