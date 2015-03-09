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
import java.util.List;
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

    /**
     * Generate test article
     */
    @ApiMethod(
            name = "test",
            path = "article/test",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public void test() {
        Article article = new Article();
        article.setUrl("test1");
        article.setTitle("Test title");
        article.setImgUrl("http://www.nimbleams.com/media/165625/personaccount.jpg");
        Text txt = new Text("<h2>Hello</h2><p>I'm <b>Nguyen Kim Kha</b>. Nice to meet you!</p>" +
                "<p><img src='http://www.nimbleams.com/media/165625/personaccount.jpg' /></p>");
        article.setFullContent(txt);
        ofy().save().entity(article).now();
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
        logger.info("Getting Article with ID: " + id);
        Article article = ofy().load().type(Article.class).id(id).now();
        if (article == null) {
            throw new NotFoundException("Could not find Article with ID: " + id);
        }
        return article;
    }

    /**
     * Inserts a new {@code Article}.
     */
    @ApiMethod(
            name = "insert",
            path = "article",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Article insert(Article article) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that article.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(article).now();
        logger.info("Created Article.");

        return ofy().load().entity(article).now();
    }

    /**
     * Updates an existing {@code Article}.
     *
     * @param id      the ID of the entity to be updated
     * @param article the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Article}
     */
    @ApiMethod(
            name = "update",
            path = "article/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Article update(@Named("id") Long id, Article article) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(article).now();
        logger.info("Updated Article: " + article);
        return ofy().load().entity(article).now();
    }

    /**
     * Deletes the specified {@code Article}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Article}
     */
    @ApiMethod(
            name = "remove",
            path = "article/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Article.class).id(id).now();
        logger.info("Deleted Article with ID: " + id);
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
    public CollectionResponse<Article> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        //test();
        Query<Article> query = ofy().load().type(Article.class).order("-created").limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Article> queryIterator = query.iterator();
        List<Article> articleList = new ArrayList<Article>(limit);
        while (queryIterator.hasNext()) {
            Article article = queryIterator.next();
            // To save data transfer
            article.setFullContent(null);
            articleList.add(article);
        }
        return CollectionResponse.<Article>builder().setItems(articleList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Article.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Article with ID: " + id);
        }
    }
}