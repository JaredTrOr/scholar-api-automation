package org.jared.trujillo.models.author;

import org.jared.trujillo.models.Article;

import java.util.List;

public class DetailedAuthor extends Author {

    private String thumbnail;
    private String affilations;
    private List<Article> articles;

    public DetailedAuthor(
            String authorId,
            String name,
            String email,
            String thumbnail,
            String affilations,
            List<Article> articles
    ) {
        super(authorId, name, email);
        this.thumbnail = thumbnail;
        this.affilations = affilations;
        this.articles = articles;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getAffilations() {
        return affilations;
    }

    public List<Article> getArticles() { return articles; }
}
