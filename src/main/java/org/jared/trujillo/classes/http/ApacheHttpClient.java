package org.jared.trujillo.classes.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jared.trujillo.classes.types.HttpSimpleResponse;
import org.jared.trujillo.exceptions.HttpException;
import org.jared.trujillo.interfaces.HttpClient;

import java.util.HashMap;
import java.util.Map;

class ApacheHttpClient implements HttpClient {

    private final CloseableHttpClient http;

    protected ApacheHttpClient() {
        this.http = HttpClients.createDefault();
    }

    @Override
    public HttpSimpleResponse get(String url) throws HttpException {
        try {
            HttpGet request = new HttpGet(url);
            return this.executeRequest(request);
        } catch(Exception e) {
            throw new HttpException(e.getMessage(), e);
        }
    }

    @Override
    public HttpSimpleResponse get(String url, Map<String, String> headers) throws HttpException {
        try {
            HttpGet request = new HttpGet(url);
            this.setHeaders(request, headers);

            return this.executeRequest(request);
        } catch(Exception e) {
            throw new HttpException(e.getMessage(), e);
        }
    }

    private void setHeaders(HttpRequestBase request, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                request.addHeader(header.getKey(), header.getKey());
            }
        }
    }

    private HttpSimpleResponse executeRequest(HttpUriRequest request) throws HttpException {
        try {
            CloseableHttpResponse response = this.http.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = null;

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseBody = EntityUtils.toString(entity);
            }

            Map<String, String> responseHeaders = new HashMap<>();

            for (Header header : response.getAllHeaders()) {
                responseHeaders.put(header.getName(), header.getValue());
            }

            return new HttpSimpleResponse(statusCode, responseBody, responseHeaders, response.getStatusLine().getReasonPhrase());

        } catch(Exception e) {
            throw new HttpException("HTTP request failed", e);
        }
    }


}
