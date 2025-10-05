package org.jared.trujillo.classes.types.scholar_data;

import org.jared.trujillo.classes.types.Pagination;
import org.jared.trujillo.models.author.DetailedAuthor;

public class ScholarAuthor extends  ScholarData {
    private DetailedAuthor author;

    public ScholarAuthor(Pagination pagination, DetailedAuthor author) {
        super(pagination);
        this.author = author;
    }

    public DetailedAuthor getAuthor() {
        return author;
    }

}
