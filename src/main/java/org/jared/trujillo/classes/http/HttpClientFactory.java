package org.jared.trujillo.classes.http;

import org.jared.trujillo.interfaces.HttpClient;

public class HttpClientFactory {

    public static HttpClient createHttpClient() {
        return new ApacheHttpClient();
    }

}
