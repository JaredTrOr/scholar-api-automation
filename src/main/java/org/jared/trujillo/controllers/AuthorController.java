package org.jared.trujillo.controllers;

import org.jared.trujillo.classes.HttpClientFactory;
import org.jared.trujillo.classes.HttpSimpleResponse;
import org.jared.trujillo.models.ParsedGoogleScholarData;
import org.jared.trujillo.exceptions.JsonHandlerException;
import org.jared.trujillo.handlers.JacksonHandler;
import org.jared.trujillo.interfaces.HttpClient;


public class AuthorController {

    HttpClient http = HttpClientFactory.createHttpClient();
    JacksonHandler jsonHandler = new JacksonHandler();

    public AuthorController() { }

    public ParsedGoogleScholarData searchByAuthorAndTopic(String apiUrl) {
        try {
            HttpSimpleResponse response = this.http.get(apiUrl);

            return jsonHandler.parseGoogleScholarResponse(response.getBody());
        } catch(Exception e) {
            throw new JsonHandlerException(e.getMessage());
        }
    }



}
