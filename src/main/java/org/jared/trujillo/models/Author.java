package org.jared.trujillo.models;

public class Author {

    private String name;
    private String email;
    private String authorId;
    private String link;

    Author() { }

    public Author(String authorId, String name, String email) {
        this.name = name;
        this.authorId = authorId;
        this.email = email;
    }

    public Author(String authorId,String name) {
        this.authorId = authorId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public String getEmail() {
        return email;
    }
}
