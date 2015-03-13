package com.kimkha.triethocduongpho.data;

import android.os.AsyncTask;
import android.util.Pair;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
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
    private final static ArticleApi articleApi;

    static {
        articleApi = new ArticleApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
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

    public static void getArticleList(String category, String nextPageToken, ApiCallback callback) {
        new EndpointsAsyncTask(callback).execute(new Pair<String, String>(category, nextPageToken));
    }

   public static void getArticle(Long id, ApiCallback callback) {
       new ArticleEndpointsAsyncTask().execute(new Pair<ApiCallback, Long>(callback, id));
   }

    static class ArticleEndpointsAsyncTask extends AsyncTask<Pair<ApiCallback, Long>, Void, Article> {
        private ApiCallback apiCallback;

        @Override
        protected Article doInBackground(Pair<ApiCallback, Long>... params) {
            apiCallback = params[0].first;
            Long id = params[0].second;

            try {
                Article response = articleApi.get(id).execute();
                return response;
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
        private ApiCallback apiCallback;

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

    public static interface ApiCallback {
        void onArticleReady(Article article);
        void onArticleListReady(List<Article> articleList, String nextPageToken);
    }
}