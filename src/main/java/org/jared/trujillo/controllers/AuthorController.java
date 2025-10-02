package org.jared.trujillo.controllers;

import org.jared.trujillo.classes.HttpClientFactory;
import org.jared.trujillo.classes.HttpSimpleResponse;
import org.jared.trujillo.classes.ParsedGoogleScholarData;
import org.jared.trujillo.handlers.JacksonHandler;
import org.jared.trujillo.interfaces.HttpClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AuthorController {

    final String API_KEY = System.getenv("api_key");
    final String[] ENGINES = { "google_scholar", "google_scholar_author" };
    String query = "john";
    String restriction = "{search_metadata, search_information, profiles.authors, organic_results, serpapi_pagination}";
    final String API_URL = "https://serpapi.com/search?engine="
            +this.ENGINES[0]
            +"&api_key="+this.API_KEY
            +"&q=author:"+ URLEncoder.encode(query, StandardCharsets.UTF_8)
            +"&json_restrictor="+URLEncoder.encode(restriction, StandardCharsets.UTF_8);
            /*+"&start="+0
            +"&num="+3;*/

    HttpClient http = HttpClientFactory.createHttpClient();
    JacksonHandler jsonHandler = new JacksonHandler();

    public AuthorController() { }

    public void getData() {
        try {
            HttpSimpleResponse response = this.http.get(this.API_URL);
            ParsedGoogleScholarData data = jsonHandler.parseGoogleScholarResponse(response.getBody());

            // Send data to views
        } catch(Exception e) {
            System.out.println(e);
        }

    }

}
