package com.journaldev.errkodutoo;


public class RSSItem {

    public String title;
    public String link;
    public String description;
    public String pubdate;
    public String guid;
    public String contentid;
    public String thumbnail;


    public RSSItem(String title, String link, String description, String pubdate, String guid, String thumbnail) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubdate = pubdate;
        this.guid = guid;
        this.thumbnail=thumbnail;
        this.contentid=link.split("/",5)[3];

    }
}
