package org.jared.trujillo.classes.types.scholar_data;

import org.jared.trujillo.classes.types.OrganicResults;
import org.jared.trujillo.classes.types.Pagination;
import org.jared.trujillo.models.author.Author;

import java.util.List;

public class ScholarGeneral extends ScholarData {

    private final List<Author> profileAuthors;
    private final List<OrganicResults> organicResults;

    public ScholarGeneral(Pagination pagination, List<Author> profileAuthors, List<OrganicResults> organicResults) {
        super(pagination);
        this.profileAuthors = profileAuthors;
        this.organicResults = organicResults;
    }

    // Getters to access the lists
    public List<Author> getProfileAuthors() {
        return this.profileAuthors;
    }

    public List<OrganicResults> getOrganicResults() {
        return this.organicResults;
    }

}
