package org.jared.trujillo.models;

public class SearchInformation {

    private String organicResultsState;
    private int totalResults;
    private float timeTakenDisplayed;
    private String queryDisplayed;

    public SearchInformation() { }

    public SearchInformation(String organicResultsState, int totalResults, float timeTakenDisplayed, String queryDisplayed) {
        this.organicResultsState = organicResultsState;
        this.totalResults = totalResults;
        this.timeTakenDisplayed = timeTakenDisplayed;
        this.queryDisplayed = queryDisplayed;
    }

    public String getOrganicResultsState() {
        return organicResultsState;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public float getTimeTakenDisplayed() {
        return timeTakenDisplayed;
    }

    public String getQueryDisplayed() {
        return queryDisplayed;
    }
}
