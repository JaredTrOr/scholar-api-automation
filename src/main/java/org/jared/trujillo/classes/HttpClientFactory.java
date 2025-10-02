package org.jared.trujillo.classes;

import org.jared.trujillo.interfaces.HttpClient;

public class HttpClientFactory {

    public static HttpClient createHttpClient() {
        return new ApacheHttpClient();
    }

}
