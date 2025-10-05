package org.jared.trujillo.classes.types;

import java.util.Map;

public class HttpSimpleResponse {

    private int statusCode;
    private String body;
    private Map<String, String> headers;
    private String reasonPhrase;

    public HttpSimpleResponse(int statusCode, String body, Map<String, String> headers, String reasonPhrase) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
        this.reasonPhrase = reasonPhrase;
    }

    public HttpSimpleResponse(int statusCode, String body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getBody() {
        return this.body;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    @Override
    public String toString() {
        return "HttpSimpleResponse{" +
                "statusCode=" + statusCode +
                ", body='" + body + '\'' +
                ", headers=" + headers.toString() +
                ", reasonPhrase='" + reasonPhrase + '\'' +
                '}';
    }
}
