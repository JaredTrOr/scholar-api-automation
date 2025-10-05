package org.jared.trujillo.models;

public class Article {

    private String citationId;
    private String title;
    private String link;
    private String authors;
    private String publication;

    // Uncertain
    private int citedBy;
    private String keywords;
    private String abstractSummary;

    // FULL ARTICLE INFORMATION
    public Article(String citationId, String title, String link, String authors, String publication, int citedBy, String abstractSummary, String keywords) {
        this.citationId = citationId;
        this.title = title;
        this.link = link;
        this.authors = authors;
        this.publication = publication;
        this.citedBy = citedBy;
        this.abstractSummary = abstractSummary;
        this.keywords = keywords;
    }

    // ARTICLE INFORMATION IN AUTHOR'S PROFILE
    public Article(String citationId, String title, String link, String authors, String publication, int citedBy) {
        this.citationId = citationId;
        this.title = title;
        this.link = link;
        this.authors = authors;
        this.publication = publication;
        this.citedBy = citedBy;
    }

    public String getCitationId() {
        return citationId;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getAuthors() {
        return authors;
    }

    public String getPublication() {
        return publication;
    }

    public int getCitedBy() {
        return citedBy;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getAbstractSummary() {
        return abstractSummary;
    }
}
