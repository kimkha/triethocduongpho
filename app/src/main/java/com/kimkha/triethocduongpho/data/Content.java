package com.kimkha.triethocduongpho.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kimkha
 * @since 3/1/15
 * @version 0.1
 */
public class Content {
    public static List<Content> CATEGORY_DEFAULT = new ArrayList<Content>();
    public static Map<String, Content> MAP_DEFAULT = new HashMap<String, Content>();

    static {
        // Add 3 sample items.
        addItem(new Content("1", "Item 1", "<h1>Item 1</h1>", "http://www.gravatar.com/avatar/91e3c7fabb05539008870121e35dcb79.png?s=160"));
        addItem(new Content("2", "Item 2", "<h1>Item 2</h1>", "http://www.gravatar.com/avatar/91e3c7fabb05539008870121e35dcb79.png?s=160"));
        addItem(new Content("3", "Item 3", "<h1>Item 3</h1><p><img src='http://www.gravatar.com/avatar/91e3c7fabb05539008870121e35dcb79.png' /></p>", "http://www.gravatar.com/avatar/91e3c7fabb05539008870121e35dcb79.png?s=160"));
        addItem(new Content("4", "Item 3", "<h1>Item 1</h1>", "http://www.gravatar.com/avatar/91e3c7fabb05539008870121e35dcb79.png?s=160"));
        addItem(new Content("5", "Item 3", "<h1>Item 1</h1>", "http://www.gravatar.com/avatar/91e3c7fabb05539008870121e35dcb79.png?s=160"));
        addItem(new Content("6", "Item 3", "<h1>Item 1</h1>", "http://www.gravatar.com/avatar/91e3c7fabb05539008870121e35dcb79.png?s=160"));
        addItem(new Content("7", "Item 3", "<h1>Item 1</h1>", "http://www.gravatar.com/avatar/91e3c7fabb05539008870121e35dcb79.png?s=160"));
        addItem(new Content("8", "Item 3", "<h1>Item 1</h1>", "http://www.gravatar.com/avatar/91e3c7fabb05539008870121e35dcb79.png?s=160"));
    }

    private static void addItem(Content item) {
        CATEGORY_DEFAULT.add(item);
        MAP_DEFAULT.put(item.id, item);
    }

    public String id;
    public String title;
    public String content;
    public String imageUrl;

    public Content(String id, String title, String content, String imageUrl) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return title;
    }
}
