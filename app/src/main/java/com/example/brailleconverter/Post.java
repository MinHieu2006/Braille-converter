package com.example.brailleconverter;

public class Post {
    public String url ;
    public String title;

    public Post(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }
}
