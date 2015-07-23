package com.kimkha.triethocduongpho.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "articleApi",
        version = "v1",
        resource = "article",
        namespace = @ApiNamespace(
                ownerDomain = "backend.triethocduongpho.kimkha.com",
                ownerName = "backend.triethocduongpho.kimkha.com",
                packagePath = ""
        )
)
public class ArticleEndpoint {

    private static final Logger logger = Logger.getLogger(ArticleEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Article.class);
    }

    private static Article defaultArticle() {
        Article article = new Article();
        article.setAuthor("NGUYỄN KIM KHA");
        article.setCreated(new Date());
        article.setId(1L);
        article.setStyle(1);
        article.setTitle("PLEASE UPDATE! VUI LÒNG CẬP NHẬT PHIÊN BẢN MỚI!");
        Text text = new Text("PHIÊN BẢN NÀY HIỆN ĐÃ LỖI THỜI. ĐỂ TIẾP TỤC SỬ DỤNG, VUI LÒNG CẬP NHẬT PHIÊN BẢN MỚI.<br/><br/>" +
                "DOWNLOAD TẠI: <a href='https://play.google.com/store/apps/details?id=com.kimkha.triethocduongpho'>https://play.google.com/store/apps/details?id=com.kimkha.triethocduongpho</a>" +
                ". <br/><br/>HOẶC DÙNG LINK RÚT GỌN: <a href='https://goo.gl/b44yb1'>https://goo.gl/b44yb1</a>.");
        article.setFullContent(text);
        article.setVersion(4);
        article.setUrl("https://play.google.com/store/apps/details?id=com.kimkha.triethocduongpho");
        return article;
    }

    /**
     * Returns the {@link Article} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Article} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "article/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Article get(@Named("id") Long id) throws NotFoundException {
//        Article article = ObjectifyService.ofy().load().type(Article.class).id(id).now();
//        if (article == null) {
//            throw new NotFoundException("Could not find Article with ID: " + id);
//        }
        return defaultArticle();
    }

    @ApiMethod(
            name = "getByUrl",
            path = "articleUrl",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Article getByUrl(@Named("url") String url) throws NotFoundException {
        // Remove ? and #
//        url = url.split("\\?")[0];
//        url = url.split("#")[0];
//
//        String[] arr = new String[2];
//        arr[0] = url;
//        if (url.endsWith("/")) {
//            arr[1] = url.substring(0, url.length()-1);
//        } else {
//            arr[1] = url + "/";
//        }
//
//        Article article = ObjectifyService.ofy().load().type(Article.class).filter("url in", arr).first().now();
//        if (article == null) {
//            throw new NotFoundException("Could not find Article with URL: " + url);
//        }
        return defaultArticle();
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "article",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Article> list(@Named("cat") String category, @Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
//        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
//        //test();
//        Query<Article> query = ObjectifyService.ofy().load().type(Article.class).order("-created").limit(limit);
//        if (category != null && !"".equals(category)) {
//            query = query.filter("category", category);
//        }
//        if (cursor != null) {
//            query = query.startAt(Cursor.fromWebSafeString(cursor));
//        }
//        QueryResultIterator<Article> queryIterator = query.iterator();
        List<Article> articleList = new ArrayList<Article>();
        articleList.add(defaultArticle());
        return CollectionResponse.<Article>builder().setItems(articleList).build();

//        // TODO Use random to choose big style
//        Random random = new Random();
//        boolean isFirst = true;
//
//        while (queryIterator.hasNext()) {
//            Article article = queryIterator.next();
//            // To save data transfer
//            article.setFullContent(null);
//            if (isFirst || random.nextInt(8) == 0) {
//                article.setStyle(1);
//                isFirst = false;
//            }
//            articleList.add(article);
//        }
//        return CollectionResponse.<Article>builder().setItems(articleList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

}