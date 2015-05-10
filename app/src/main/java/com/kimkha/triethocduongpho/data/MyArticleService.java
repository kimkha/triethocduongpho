package com.kimkha.triethocduongpho.data;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Pair;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.kimkha.triethocduongpho.backend.articleApi.ArticleApi;
import com.kimkha.triethocduongpho.backend.articleApi.model.Article;
import com.kimkha.triethocduongpho.backend.articleApi.model.CollectionResponseArticle;

import java.io.IOException;
import java.util.List;

/**
 * @author kimkha
 * @version 0.1
 * @since 3/3/15
 */
public class MyArticleService {
    private final static String URL_BASE = "http://10.0.2.2:8080/_ah/api/";
    private final static String IMG_BASE = "http://storage.googleapis.com";
    private final static ArticleApi articleApi;

    /**
     * Class instance of the JSON factory.
     */
    public static JsonFactory getJsonFactory() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // only for honeycomb and newer versions
            return new AndroidJsonFactory();
        } else {
            return new GsonFactory();
        }
    }

    static {
        articleApi = new ArticleApi.Builder(AndroidHttp.newCompatibleTransport(),
                getJsonFactory(), null)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver
//                        .setRootUrl(URL_BASE)
//                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                            @Override
//                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
//                                abstractGoogleClientRequest.setDisableGZipContent(true);
//                            }
//                        })
                // end option for devappserver
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

    public static void getArticleList(String category, String nextPageToken, ApiCallback callback) {
        new EndpointsAsyncTask(callback).execute(new Pair<>(category, nextPageToken));
    }

    public static void getArticle(String url, Long id, ApiCallback callback) {
        new ArticleEndpointsAsyncTask(callback).execute(new Pair<>(url, id));
    }

    static class ArticleEndpointsAsyncTask extends AsyncTask<Pair<String, Long>, Void, Article> {
        private final ApiCallback apiCallback;

        public ArticleEndpointsAsyncTask(ApiCallback apiCallback) {
            this.apiCallback = apiCallback;
        }

        @Override
        protected Article doInBackground(Pair<String, Long>... params) {
            String url = params[0].first;
            Long id = params[0].second;

            try {
                if (id > 0) {
                    return articleApi.get(id).execute();
                }
                return articleApi.getByUrl(url).execute();
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

    static class EndpointsAsyncTask extends AsyncTask<Pair<String, String>, Void, CollectionResponseArticle> {
        private final ApiCallback apiCallback;

        public EndpointsAsyncTask(ApiCallback apiCallback) {
            this.apiCallback = apiCallback;
        }

        @Override
        protected CollectionResponseArticle doInBackground(Pair<String, String>... params) {
            String category = params[0].first;
            String nextPageToken = params[0].second;

            try {
                return articleApi.list(category).setCursor(nextPageToken).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CollectionResponseArticle result) {
            apiCallback.onArticleListReady(result.getItems(), result.getNextPageToken());
        }
    }

    public interface ApiCallback {
        void onArticleReady(Article article);
        void onArticleListReady(List<Article> articleList, String nextPageToken);
    }
}
