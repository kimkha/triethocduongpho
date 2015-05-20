package com.kimkha.triethocduongpho.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.security.MessageDigest;
import java.util.ArrayList;
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
        name = "article2Api",
        version = "v1",
        resource = "article",
        namespace = @ApiNamespace(
                ownerDomain = "backend.triethocduongpho.kimkha.com",
                ownerName = "backend.triethocduongpho.kimkha.com",
                packagePath = ""
        )
)
public class Article2Endpoint {

    private static final Logger logger = Logger.getLogger(Article2Endpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Article.class);
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
    public Article get(@Named("id") Long id,
                       @Nullable @Named("timehash") Long timehash, @Nullable @Named("cert") String cert)
            throws NotFoundException, UnauthorizedException, ForbiddenException {
        validateRequest(timehash, cert);

        Article article = ofy().load().type(Article.class).id(id).now();
        if (article == null) {
            throw new NotFoundException("Could not find Article with ID: " + id);
        }
        return article;
    }

    @ApiMethod(
            name = "getByUrl",
            path = "articleUrl",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Article getByUrl(@Named("url") String url,
                            @Nullable @Named("timehash") Long timehash, @Nullable @Named("cert") String cert)
            throws NotFoundException, ForbiddenException {
        validateRequest(timehash, cert);

        // Remove ? and #
        url = url.split("\\?")[0];
        url = url.split("#")[0];

        String[] arr = new String[2];
        arr[0] = url;
        if (url.endsWith("/")) {
            arr[1] = url.substring(0, url.length()-1);
        } else {
            arr[1] = url + "/";
        }

        Article article = ofy().load().type(Article.class).filter("url in", arr).first().now();
        if (article == null) {
            throw new NotFoundException("Could not find Article with URL: " + url);
        }
        return article;
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
    public CollectionResponse<Article> list(@Named("cat") String category,
                                            @Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit,
                                            @Nullable @Named("timehash") Long timehash, @Nullable @Named("cert") String cert)
            throws ForbiddenException {
        validateRequest(timehash, cert);

        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        //test();
        Query<Article> query = ofy().load().type(Article.class).order("-created").limit(limit);
        if (category != null && !"".equals(category)) {
            query = query.filter("category", category);
        }
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterable<Article> queryIter = query.iterable();
        List<Article> articleList = new ArrayList<Article>(limit);

        // TODO Use random to choose big style
        Random random = new Random();
        boolean isFirst = true;

        for (Article article : queryIter) {
            // To save data transfer
            article.setFullContent(null);
            if (isFirst || random.nextInt(8) == 0) {
                article.setStyle(1);
                isFirst = false;
            }
            articleList.add(article);
        }
        return CollectionResponse.<Article>builder().setItems(articleList).setNextPageToken(queryIter.iterator().getCursor().toWebSafeString()).build();
    }

    private void validateRequest(Long timehash, String cert) throws ForbiddenException {
        if (timehash != null && cert != null) {
            try {
                QueryResultIterable<PrivateInfo> allPrivate = ofy().load().type(PrivateInfo.class).iterable();
                for (PrivateInfo info : allPrivate) {
                    String key = doSHA1(String.format("%s;%s;%d", info.getFingerprint(), info.getFingerprint(), timehash));
                    if (cert.equalsIgnoreCase(key)) {
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        throw new ForbiddenException("Cannot access by: " + cert + " at: " + timehash);
    }

    private String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    private String doSHA1(String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }
}