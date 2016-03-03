package com.example.steven.rewrite_itechzero;

/**
 * Created by steven on 2016/2/29.
 */
public class Link {
    private String url;
    private String name;

    public Link(String name,String url) {
        this.url = url;
        this.name=name;
    }

    public String getUrl(){
        return this.url;
    }

    public String getName(){
        return this.name;
    }
}
