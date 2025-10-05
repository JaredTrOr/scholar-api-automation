package org.jared.trujillo.models.author;

public class ProfileAuthor extends Author {

    private int citedBy;

    public ProfileAuthor(String authorId, String name, String email, String link, int citedBy) {
        super(authorId, name, email);
        super.setLink(link);
        this.citedBy = citedBy;
    }

    public int getCitedBy() {
        return citedBy;
    }

    @Override
    public String toString() {
        return "ProfilesAuthor{" +
                "authorId=" + super.getAuthorId() +
                ", name=" + super.getName() +
                ", email=" + super.getName() +
                ", link=" + super.getLink() +
                ", citedBy=" + citedBy +
                '}';
    }
}
