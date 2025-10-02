package org.jared.trujillo.models;

public class SearchMetadata {

    private String id;
    private String status;
    private String jsonEndpoint;
    private float totalTimeTaken;

    public SearchMetadata() { }

    public SearchMetadata(String id, String status, String jsonEndpoint, float totalTimeTaken) {
        this.id = id;
        this.status = status;
        this.jsonEndpoint = jsonEndpoint;
        this.totalTimeTaken = totalTimeTaken;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getJsonEndpoint() {
        return jsonEndpoint;
    }

    public float getTotalTimeTaken() {
        return totalTimeTaken;
    }

    @Override
    public String toString() {
        return "SearchMetadata{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", jsonEndpoint='" + jsonEndpoint + '\'' +
                ", totalTimeTaken=" + totalTimeTaken +
                '}';
    }
}
