package org.jared.trujillo.models;

import java.util.List;
import java.util.Map;

public class Pagination {

    private int current;
    private String next;
    private Map<String, String> otherPages;

    public Pagination(int current, String next, Map<String, String> otherPages) {
        this.current = current;
        this.next = next;
        this.otherPages = otherPages;
    }

    public int getCurrent() {
        return current;
    }

    public String getNext() {
        return next;
    }

    public Map<String, String> getOtherPages() {
        return otherPages;
    }
}
