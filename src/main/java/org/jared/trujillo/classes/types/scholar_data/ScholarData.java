package org.jared.trujillo.classes.types.scholar_data;

import org.jared.trujillo.classes.types.Pagination;

public class ScholarData {

    private Pagination pagination;

    public ScholarData(Pagination pagination) {
        this.pagination = pagination;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
