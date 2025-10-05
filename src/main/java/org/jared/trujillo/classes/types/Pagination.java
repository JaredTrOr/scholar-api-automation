package org.jared.trujillo.classes.types;

import java.util.Map;

public class Pagination {

    private int current;
    private String next;
    private String prev;
    private Map<String, String> otherPages;

    public Pagination(int current, String next, Map<String, String> otherPages) {
        this.current = current;
        this.next = next;
        this.otherPages = otherPages;
    }

    public Pagination(String next) {
        this.next = next;
    }

    public int getCurrent() {
        return this.current;
    }

    public String getNext() {
        return this.next;
    }

    public Map<String, String> getOtherPages() {
        return this.otherPages;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public void setPrev(String prev) {
        this.prev = prev;
    }

    public String getPrev() { return this.prev; }
}
