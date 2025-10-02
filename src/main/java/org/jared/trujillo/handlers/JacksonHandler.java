package org.jared.trujillo.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jared.trujillo.classes.ParsedGoogleScholarData;
import org.jared.trujillo.exceptions.JsonHandlerException;
import org.jared.trujillo.models.OrganicResults;
import org.jared.trujillo.models.Pagination;
import org.jared.trujillo.models.authors.Author;
import org.jared.trujillo.models.authors.ProfilesAuthor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JacksonHandler {

    ObjectMapper mapper = new ObjectMapper();

    public JacksonHandler() { }

    public ParsedGoogleScholarData parseGoogleScholarResponse(String jsonString) throws JsonHandlerException {

        try {
            JsonNode jsonNode = mapper.readTree(jsonString);

            // PROFILE GOOGLE SUGGESTIONS
            JsonNode authorsNode = jsonNode.get("profiles").get("authors");
            List<Author> profilesAuthorsList = new ArrayList<>();
            for (JsonNode authorNode : authorsNode) {
                profilesAuthorsList.add(
                        new ProfilesAuthor(
                                authorNode.get("author_id").asText(),
                                authorNode.get("name").asText(),
                                authorNode.get("email").asText(),
                                authorNode.get("link").asText(),
                                authorNode.get("cited_by").asInt()
                        )
                );
            }

            // PAGINATION
            JsonNode serpApiNode = jsonNode.path("serpapi_pagination");
            JsonNode otherPagesNode = serpApiNode.path("other_pages");
            Map<String, String> otherPagesMap = new HashMap<>();
            if (!otherPagesNode.isMissingNode() && otherPagesNode.isObject()) {
                otherPagesMap = mapper.convertValue(otherPagesNode, new TypeReference<Map<String, String>>() {});
            }
            Pagination pagination = new Pagination(
                    serpApiNode.path("current").asInt(),
                    serpApiNode.path("next").asText(),
                    otherPagesMap
            );

            // ORGANIC RESULTS
            JsonNode organicResultsNode = jsonNode.path("organic_results"); // Use path() instead of get()
            List<OrganicResults> organicResultsList = new ArrayList<>();
            if (organicResultsNode != null && organicResultsNode.isArray()) {
                for(JsonNode node : organicResultsNode) {

                    // GET AUTHORS FROM ORGANIC DATA
                    JsonNode organicAuthorsNode = node.path("publication_info").path("authors"); // Use path()
                    List<Author> organicAuthors = new ArrayList<>();

                    if (organicAuthorsNode != null && organicAuthorsNode.isArray()) {
                        for (JsonNode authorNode : organicAuthorsNode) {
                            Author newAuthor = new Author(
                                    authorNode.path("author_id").asText(), // Default empty string
                                    authorNode.path("name").asText()       // Default empty string
                            );
                            newAuthor.setLink(authorNode.path("link").asText()); // Default empty string
                            organicAuthors.add(newAuthor);
                        }
                    }

                    // GET RESOURCES - ADD NULL CHECK
                    JsonNode resourcesNode = node.path("resources"); // Use path()
                    List<Map<String, String>> resourcesList = new ArrayList<>();

                    if (resourcesNode != null && resourcesNode.isArray()) {
                        for (JsonNode resource : resourcesNode) {
                            Map<String, String> newResource = new HashMap<>();
                            newResource.put("title", resource.path("title").asText());
                            newResource.put("link", resource.path("link").asText());
                            newResource.put("fileFormat", resource.path("file_format").asText()); // No need for null check
                            resourcesList.add(newResource);
                        }
                    }

                    organicResultsList.add(
                            new OrganicResults(
                                    node.path("position").asInt(0),        // Default 0
                                    node.path("title").asText(),         // Default empty string
                                    node.path("result_id").asText(),     // Default empty string
                                    node.path("link").asText(),          // Default empty string
                                    node.path("snippet").asText(),       // Default empty string
                                    node.path("publication_info").path("summary").asText(),       // Default empty string
                                    organicAuthors,
                                    resourcesList
                            )
                    );
                }

            }

            return new ParsedGoogleScholarData(profilesAuthorsList, organicResultsList, pagination);


        } catch(Exception e) {
            throw new JsonHandlerException(e.getMessage());
        }

    }
}
