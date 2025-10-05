package org.jared.trujillo.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jared.trujillo.classes.types.scholar_data.ScholarAuthor;
import org.jared.trujillo.classes.types.scholar_data.ScholarGeneral;
import org.jared.trujillo.exceptions.JsonHandlerException;
import org.jared.trujillo.classes.types.OrganicResults;
import org.jared.trujillo.classes.types.Pagination;
import org.jared.trujillo.models.Article;
import org.jared.trujillo.models.author.Author;
import org.jared.trujillo.models.author.ProfileAuthor;
import org.jared.trujillo.models.author.DetailedAuthor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JacksonHandler {

    final String CONFIG_FILE_PATH = "config.json";
    ObjectMapper mapper = new ObjectMapper();

    public JacksonHandler() { }

    public ScholarGeneral parseScholarGeneralResponse(String jsonString) throws JsonHandlerException {

        try {
            JsonNode jsonNode = this.mapper.readTree(jsonString);

            JsonNode authorsNode = jsonNode.path("profiles").path("authors");

            List<Author> profilesAuthorsList = new ArrayList<>();
            if (!authorsNode.isMissingNode() && authorsNode.isArray()) {
                for (JsonNode authorNode : authorsNode) {
                    profilesAuthorsList.add(
                            new ProfileAuthor(
                                    authorNode.path("author_id").asText(""),
                                    authorNode.path("name").asText(""),
                                    authorNode.path("email").asText(""),
                                    authorNode.path("link").asText(""),
                                    authorNode.path("cited_by").asInt(0)
                            )
                    );
                }
            }

            JsonNode serpApiNode = jsonNode.path("serpapi_pagination");
            JsonNode otherPagesNode = serpApiNode.path("other_pages");
            Map<String, String> otherPagesMap = new HashMap<>();
            if (!otherPagesNode.isMissingNode() && otherPagesNode.isObject()) {
                otherPagesMap = this.mapper.convertValue(otherPagesNode, new TypeReference<>() {});
            }
            Pagination pagination = new Pagination(
                    serpApiNode.path("current").asInt(0),
                    serpApiNode.path("next").asText(""),
                    otherPagesMap
            );

            JsonNode organicResultsNode = jsonNode.path("organic_results");
            List<OrganicResults> organicResultsList = new ArrayList<>();
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

            return new ScholarGeneral(pagination, profilesAuthorsList, organicResultsList);

        } catch (Exception e) {
            throw new JsonHandlerException("Failed to parse Scholar General JSON response: " + e.getMessage(), e);
        }
    }

    public ScholarAuthor parseScholarAuthorResponse(String jsonString, String authorId) throws  JsonHandlerException {

        try {
            JsonNode jsonNode = this.mapper.readTree(jsonString);

            JsonNode authorNode = jsonNode.path("author");
            JsonNode articlesNode = jsonNode.path("articles");
            JsonNode serpApiNode = jsonNode.path("serpapi_pagination");

            List<Article> articleList = new ArrayList<>();
            if (articlesNode != null && articlesNode.isArray()) {
                for (JsonNode articleNode: articlesNode) {
                    articleList.add(new Article(
                            articleNode.path("citation_id").asText(""),
                            articleNode.path("title").asText(""),
                            articleNode.path("link").asText(""),
                            articleNode.path("authors").asText(""),
                            articleNode.path("publication").asText(""),
                            articleNode.path("cited_by") != null ? articleNode.path("cited_by").path("value").asInt() : -1
                    ));
                }
            }

            DetailedAuthor author = new DetailedAuthor(
                    authorId,
                    authorNode.path("name").asText(""),
                    authorNode.path("email").asText(""),
                    authorNode.path("thumbnail").asText(""),
                    authorNode.path("affiliations").asText(""),
                    articleList
            );

            Pagination pagination = new Pagination(
                    serpApiNode.path("next").asText("")
            );

            if (serpApiNode.path("previous") != null) {
                pagination.setPrev(serpApiNode.path("previous").asText(""));
            }

            return new ScholarAuthor(pagination, author);

        }  catch (Exception e) {
            throw new JsonHandlerException("Failed to parse Scholar General JSON response: " + e.getMessage(), e);
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
