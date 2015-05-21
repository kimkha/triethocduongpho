package com.kimkha.triethocduongpho.data;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Pair;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.kimkha.triethocduongpho.backend.article2Api.Article2Api;
import com.kimkha.triethocduongpho.backend.article2Api.model.Article;
import com.kimkha.triethocduongpho.backend.article2Api.model.CollectionResponseArticle;
import com.kimkha.triethocduongpho.util.MyValidator;

import java.io.IOException;
import java.util.List;

/**
 * @author kimkha
 * @version 0.1
 * @since 3/3/15
 */
public class MyArticle2Service {
    private final static String URL_BASE = "http://10.0.2.2:8080/_ah/api/";
    private final static String IMG_BASE = "http://storage.googleapis.com";
    private final static Article2Api articleApi;

    /**
     * Class instance of the JSON factory.
     */
    public static JsonFactory getJsonFactory() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // only for honeycomb and newer versions
            return new AndroidJsonFactory();
        } else {
            return new GsonFactory();
        }
    }

    static {
        articleApi = new Article2Api.Builder(AndroidHttp.newCompatibleTransport(),
                getJsonFactory(), null)
                .build();
    }

    public static String parseImageUrl(String imgUrl) {
        if (imgUrl != null && imgUrl.startsWith("/triethocduongpho-android")) {
            imgUrl = IMG_BASE + imgUrl;
        }
        return imgUrl;
    }

    public static String getImgBase() {
        return IMG_BASE;
    }

    public static void getArticleList(String category, String nextPageToken, int limit, ApiCallback callback) {
        new EndpointsAsyncTask(callback, category, nextPageToken, limit).execute();
    }

    public static void getArticle(String url, Long id, ApiCallback callback) {
        new ArticleEndpointsAsyncTask(callback, id, url).execute();
    }

    static class ArticleEndpointsAsyncTask extends AsyncTask<Void, Void, Article> {
        private final ApiCallback apiCallback;
        private final String url;
        private final Long id;

        public ArticleEndpointsAsyncTask(ApiCallback apiCallback, Long id, String url) {
            this.apiCallback = apiCallback;
            this.id = id;
            this.url = url;
        }

        @Override
        protected Article doInBackground(Void... p) {
            Long timehash = System.currentTimeMillis();
            String cert = MyValidator.getCertificate(timehash);

            try {
                if (id > 0) {
                    return articleApi.get(id).setTimehash(timehash).setCert(cert).execute();
                }
                return articleApi.getByUrl(url).setTimehash(timehash).setCert(cert).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Article result) {
            apiCallback.onArticleReady(result);
        }
    }

    static class EndpointsAsyncTask extends AsyncTask<Void, Void, CollectionResponseArticle> {
        private final ApiCallback apiCallback;
        private final String category;
        private final String nextPageToken;
        private final int limit;

        public EndpointsAsyncTask(ApiCallback apiCallback, String category, String nextPageToken, int limit) {
            this.apiCallback = apiCallback;
            this.category = category;
            this.nextPageToken = nextPageToken;
            this.limit = limit;
        }

        @Override
        protected CollectionResponseArticle doInBackground(Void... params) {
            Long timehash = System.currentTimeMillis();
            String cert = MyValidator.getCertificate(timehash);

            try {
                return articleApi.list(category).setCursor(nextPageToken).setLimit(limit)
                        .setTimehash(timehash).setCert(cert).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CollectionResponseArticle result) {
            if (result == null) {
                apiCallback.onArticleListReady(null, null);
                return;
            }
            apiCallback.onArticleListReady(result.getItems(), result.getNextPageToken());
        }
    }

    public interface ApiCallback {
        void onArticleReady(Article article);
        void onArticleListReady(List<Article> articleList, String nextPageToken);
    }
}
