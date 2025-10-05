package org.jared.trujillo.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ApiUrlBuilder {

    private static final String API_KEY = System.getenv("api_key");
    private static final String BASE_API_URL = "https://serpapi.com/search";

    public static String buildUrl(String engine, String restrictions, Map<String, String> parameters) {

        StringBuilder urlBuilder = new StringBuilder(BASE_API_URL);

        // BASE URL
        urlBuilder.append("?engine=").append(engine);
        urlBuilder.append("&api_key=").append(API_KEY);
        urlBuilder.append("&json_restrictor=").append(URLEncoder.encode(restrictions, StandardCharsets.UTF_8));

        try {

            for (Map.Entry<String, String> param : parameters.entrySet()) {
                urlBuilder.append("&");
                urlBuilder.append(param.getKey());
                urlBuilder.append("=");
                urlBuilder.append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8));
            }

        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        return urlBuilder.toString();
    }

}
