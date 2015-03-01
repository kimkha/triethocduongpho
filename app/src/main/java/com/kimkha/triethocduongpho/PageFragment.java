package com.kimkha.triethocduongpho;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kimkha.triethocduongpho.data.Content;

import org.sufficientlysecure.htmltextview.HtmlTextView;

/**
 * @author kimkha
 * @since 3/1/15
 * @version 0.1
 */
public class PageFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    private Content content;

    public PageFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            content = Content.MAP_DEFAULT.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        HtmlTextView htmlTextView = (HtmlTextView) rootView.findViewById(R.id.page_content);
        htmlTextView.setHtmlFromString(content.content, false);
        return rootView;
    }
}
