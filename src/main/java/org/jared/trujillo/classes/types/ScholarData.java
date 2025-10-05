package org.jared.trujillo.classes.types;

import org.jared.trujillo.models.Author;

import java.util.List;

public class ScholarData {

    private final List<Author> profileAuthors;
    private final List<OrganicResults> organicResults;
    private final Pagination pagination;

    public ScholarData(List<Author> profileAuthors, List<OrganicResults> organicResults, Pagination pagination) {
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
