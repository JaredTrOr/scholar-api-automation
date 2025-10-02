package org.jared.trujillo.models;

import org.jared.trujillo.models.authors.Author;

import java.util.List;
import java.util.Map;

public class OrganicResults {

    private int position;
    private String title;
    private String resultId;
    private String link;
    private String snippet;
    private String summary;
    private List<Author> authorsList;
    private List<Map<String, String>> resources;

    public OrganicResults(int position, String title, String resultId, String link, String snippet, String summary, List<Author> authorsList, List<Map<String, String>> resources) {
        this.position = position;
        this.title = title;
        this.resultId = resultId;
        this.link = link;
        this.snippet = snippet;
        this.summary = summary;
        this.authorsList = authorsList;
        this.resources = resources;
    }

    public int getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public String getResultId() {
        return resultId;
    }

    public String getLink() {
        return link;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getSummary() {
        return summary;
    }

    public List<Author> getAuthorsList() {
        return authorsList;
    }

    public List<Map<String, String>> getResources() {
        return resources;
    }

    @Override
    public String toString() {
        return "OrganicResults{" +
                "position=" + position +
                ", title='" + title + '\'' +
                ", resultId='" + resultId + '\'' +
                ", link='" + link + '\'' +
                ", snippet='" + snippet + '\'' +
                ", summary='" + summary + '\'' +
                ", authorsList=" + authorsList.toString() +
                ", resources=" + resources.toString() +
                '}';
    }
}
