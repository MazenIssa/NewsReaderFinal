package com.example.newsreader;

public class Favourite {

    private String title;
    private String desc;
    private String url;
    private String date;
    private long id;

    public Favourite(long id, String title, String desc, String date, String url) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.url = url;
    }

    public Favourite()
    {
        this.title="";
        this.desc="";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public String getDate() {
        return date;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
