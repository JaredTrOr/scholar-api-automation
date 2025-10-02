package org.jared.trujillo.models.authors;

public class SearchAuthor extends Author {

    private String thumbnail;
    private String affilations;
    // interests
    // articles


    public SearchAuthor(String authorId, String name, String email, String thumbnail, String affilations) {
        super(authorId, name, email);
        this.thumbnail = thumbnail;
        this.affilations = affilations;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getAffilations() {
        return affilations;
    }
}
