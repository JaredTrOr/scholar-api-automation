package org.jared.trujillo.controllers;

import org.jared.trujillo.classes.http.HttpClientFactory;
import org.jared.trujillo.classes.types.HttpSimpleResponse;
import org.jared.trujillo.classes.types.scholar_data.ScholarAuthor;
import org.jared.trujillo.classes.types.scholar_data.ScholarGeneral;
import org.jared.trujillo.exceptions.JsonHandlerException;
import org.jared.trujillo.utils.JacksonHandler;
import org.jared.trujillo.interfaces.HttpClient;


public class AuthorController {

    HttpClient http = HttpClientFactory.createHttpClient();
    JacksonHandler jsonHandler = new JacksonHandler();

    public AuthorController() { }

    public ScholarGeneral searchByAuthorAndTopic(String apiUrl) {
        try {
            // TODO: Check if response was successfully otherwise notify
            HttpSimpleResponse response = this.http.get(apiUrl);
            return jsonHandler.parseScholarGeneralResponse(response.getBody());
        } catch(Exception e) {
            throw new JsonHandlerException(e.getMessage());
        }
    }

    public ScholarAuthor searchByAuthorId(String apiUrl, String authorId) {
        try {
            // TODO: Check if response was successfully otherwise notigy
            HttpSimpleResponse response = this.http.get(apiUrl);
            return jsonHandler.parseScholarAuthorResponse(response.getBody(), authorId);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
