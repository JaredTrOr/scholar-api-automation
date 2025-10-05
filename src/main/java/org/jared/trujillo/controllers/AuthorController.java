package org.jared.trujillo.controllers;

import org.jared.trujillo.classes.http.HttpClientFactory;
import org.jared.trujillo.classes.types.HttpSimpleResponse;
import org.jared.trujillo.classes.types.ScholarData;
import org.jared.trujillo.exceptions.JsonHandlerException;
import org.jared.trujillo.utils.JacksonHandler;
import org.jared.trujillo.interfaces.HttpClient;


public class AuthorController {

    HttpClient http = HttpClientFactory.createHttpClient();
    JacksonHandler jsonHandler = new JacksonHandler();

    public AuthorController() { }

    public ScholarData searchByAuthorAndTopic(String apiUrl) {
        try {
            HttpSimpleResponse response = this.http.get(apiUrl);

            return jsonHandler.parseGoogleScholarResponse(response.getBody());
        } catch(Exception e) {
            throw new JsonHandlerException(e.getMessage());
        }
    }



}
