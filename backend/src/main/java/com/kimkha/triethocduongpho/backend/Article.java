package com.kimkha.triethocduongpho.backend;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * @author kimkha
 * @version 0.1
 * @since 3/2/15
 */
@Entity
public class Article {
    @Id
    private Long id;

    private String title;
    private String imgUrl;
    private Text fullContent;

    @Index
    private String url;

    public Article() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Text getFullContent() {
        return fullContent;
    }

    public void setFullContent(Text fullContent) {
        this.fullContent = fullContent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
