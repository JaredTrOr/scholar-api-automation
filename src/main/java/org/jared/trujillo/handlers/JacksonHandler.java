package org.jared.trujillo.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jared.trujillo.models.ParsedGoogleScholarData;
import org.jared.trujillo.exceptions.JsonHandlerException;
import org.jared.trujillo.models.OrganicResults;
import org.jared.trujillo.models.Pagination;
import org.jared.trujillo.models.authors.Author;
import org.jared.trujillo.models.authors.ProfilesAuthor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JacksonHandler {

    final String CONFIG_FILE_PATH = "config.json";
    ObjectMapper mapper = new ObjectMapper();

    public JacksonHandler() { }

    public ParsedGoogleScholarData parseGoogleScholarResponse(String jsonString) throws JsonHandlerException {
        // Assuming 'mapper' is an ObjectMapper instance available in your class
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode jsonNode = mapper.readTree(jsonString);

            // --- PROFILE GOOGLE SUGGESTIONS (Corrected) ---
            JsonNode authorsNode = jsonNode.path("profiles").path("authors");

            List<Author> profilesAuthorsList = new ArrayList<>();
            // Safety check: Only loop if the node exists and is an array
            if (!authorsNode.isMissingNode() && authorsNode.isArray()) {
                for (JsonNode authorNode : authorsNode) {
                    profilesAuthorsList.add(
                            new ProfilesAuthor(
                                    authorNode.path("author_id").asText(""),
                                    authorNode.path("name").asText(""),
                                    authorNode.path("email").asText(""),
                                    authorNode.path("link").asText(""),
                                    authorNode.path("cited_by").asInt(0)
                            )
                    );
                }
            }

            // --- PAGINATION (Already good) ---
            JsonNode serpApiNode = jsonNode.path("serpapi_pagination");
            JsonNode otherPagesNode = serpApiNode.path("other_pages");
            Map<String, String> otherPagesMap = new HashMap<>();
            if (!otherPagesNode.isMissingNode() && otherPagesNode.isObject()) {
                otherPagesMap = mapper.convertValue(otherPagesNode, new TypeReference<>() {});
            }
            Pagination pagination = new Pagination(
                    serpApiNode.path("current").asInt(0),
                    serpApiNode.path("next").asText(""),
                    otherPagesMap
            );

            // --- ORGANIC RESULTS (Corrected) ---
            JsonNode organicResultsNode = jsonNode.path("organic_results");
            List<OrganicResults> organicResultsList = new ArrayList<>();
            // Safety check: Only loop if the node exists and is an array
            if (!organicResultsNode.isMissingNode() && organicResultsNode.isArray()) {
                for (JsonNode node : organicResultsNode) {

                    // GET AUTHORS FROM ORGANIC DATA
                    JsonNode organicAuthorsNode = node.path("publication_info").path("authors");
                    List<Author> organicAuthors = new ArrayList<>();
                    // Safety check
                    if (!organicAuthorsNode.isMissingNode() && organicAuthorsNode.isArray()) {
                        for (JsonNode authorNode : organicAuthorsNode) {
                            Author newAuthor = new Author(
                                    authorNode.path("author_id").asText(""),
                                    authorNode.path("name").asText("")
                            );
                            newAuthor.setLink(authorNode.path("link").asText(""));
                            organicAuthors.add(newAuthor);
                        }
                    }

                    // GET RESOURCES
                    JsonNode resourcesNode = node.path("resources");
                    List<Map<String, String>> resourcesList = new ArrayList<>();
                    // Safety check
                    if (!resourcesNode.isMissingNode() && resourcesNode.isArray()) {
                        for (JsonNode resource : resourcesNode) {
                            Map<String, String> newResource = new HashMap<>();
                            newResource.put("title", resource.path("title").asText());
                            newResource.put("link", resource.path("link").asText());
                            newResource.put("fileFormat", resource.path("file_format").asText());
                            resourcesList.add(newResource);
                        }
                    }

                    organicResultsList.add(
                            new OrganicResults(
                                    node.path("position").asInt(0),
                                    node.path("title").asText(""),
                                    node.path("result_id").asText(""),
                                    node.path("link").asText(""),
                                    node.path("snippet").asText(""),
                                    node.path("publication_info").path("summary").asText(),
                                    organicAuthors,
                                    resourcesList
                            )
                    );
                }
            }

            return new ParsedGoogleScholarData(profilesAuthorsList, organicResultsList, pagination);

        } catch (Exception e) {
            // It's often better to wrap the original exception for more detailed debugging
            throw new JsonHandlerException("Failed to parse Google Scholar JSON response: " + e.getMessage(), e);
        }
    }

    public Map<String, Map<String, String>> getConfigFileApi() {
        try {
             return this.mapper.readValue(
                     new File(this.CONFIG_FILE_PATH),
                     new TypeReference<Map<String, Map<String, String>>>() {}
             );
        } catch(Exception e) {
            System.out.println("WTF");
            throw new RuntimeException(e);
        }
    }
}
