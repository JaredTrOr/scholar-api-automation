package org.jared.trujillo.classes;

import org.jared.trujillo.models.OrganicResults;
import org.jared.trujillo.models.Pagination;
import org.jared.trujillo.models.authors.Author;

import java.util.List;

public class ParsedGoogleScholarData {

    private final List<Author> profileAuthors;
    private final List<OrganicResults> organicResults;
    private final Pagination pagination;

    public ParsedGoogleScholarData(List<Author> profileAuthors, List<OrganicResults> organicResults, Pagination pagination) {
        this.profileAuthors = profileAuthors;
        this.organicResults = organicResults;
        this.pagination = pagination;
    }

    // Getters to access the lists
    public List<Author> getProfileAuthors() {
        return this.profileAuthors;
    }

    public List<OrganicResults> getOrganicResults() {
        return this.organicResults;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
