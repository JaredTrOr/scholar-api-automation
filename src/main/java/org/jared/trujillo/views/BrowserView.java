package org.jared.trujillo.views;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import org.jared.trujillo.classes.types.scholar_data.ScholarGeneral;
import org.jared.trujillo.controllers.AuthorController;
import org.jared.trujillo.utils.ApiUrlBuilder;
import org.jared.trujillo.utils.JacksonHandler;
import org.jared.trujillo.classes.types.OrganicResults;
import org.jared.trujillo.models.author.Author;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BrowserView extends Application {

    private TextField searchField;
    private VBox cardsContainer;
    private AuthorController authorController;
    private JacksonHandler jsonHandler;
    private Map<String, Map<String, String>> configFile;
    private HostServices hostServices;
    private Scene searchScene;
    private BorderPane searchRoot;

    // CONSTRUCTOR---------------------------------------------------------------------------------
    @Override
    public void init() {
        this.jsonHandler = new JacksonHandler();
        this.authorController = new AuthorController();
        this.configFile = this.jsonHandler.getConfigFileApi();
        this.hostServices = getHostServices();
    }

    // CREATION OF UI ELEMENTS-----------------------------------------------------------------------
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Scholar Search Interface");

        this.authorController = new AuthorController();
        this.cardsContainer = new VBox();
        this.searchField = new TextField();

        this.searchRoot = new BorderPane();
        VBox topContainer = new VBox(10);
        topContainer.setPadding(new Insets(10, 10, 0, 10));

        Label mainTitle = new Label("Google Scholar API Search");
        mainTitle.setId("mainTitle");
        HBox titleBox = new HBox(mainTitle);
        titleBox.setAlignment(Pos.CENTER);

        HBox searchBar = this.createSearchBar();
        topContainer.getChildren().addAll(titleBox, searchBar);
        this.searchRoot.setTop(topContainer);

        this.cardsContainer.setPadding(new Insets(10));
        this.cardsContainer.setSpacing(10);
        ScrollPane scrollPane = new ScrollPane(this.cardsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        this.searchRoot.setCenter(scrollPane);

        this.searchScene = new Scene(this.searchRoot);
        String css = Objects.requireNonNull(this.getClass().getResource("/styles/browser-styles.css")).toExternalForm();
        this.searchScene.getStylesheets().add(css);

        primaryStage.setScene(this.searchScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private HBox createSearchBar() {
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER);
        searchField.setPromptText("Enter author or topic...");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        Button authorButton = new Button("By Author");
        Button topicButton = new Button("By Topic");
        authorButton.setOnAction(_ -> searchByAuthor(searchField.getText()));
        topicButton.setOnAction(_ -> searchByTopic(searchField.getText()));
        searchBar.getChildren().addAll(new Label("Search:"), searchField, authorButton, topicButton);
        return searchBar;
    }

    private Node createProfileSection(ScholarGeneral data) {
        VBox profileSection = new VBox(10);
        Label profileTitle = new Label("Profiles (Most Relevant)");
        profileTitle.getStyleClass().add("section-title");
        HBox profilesHBox = new HBox(10);
        profilesHBox.setPadding(new Insets(5));
        for (Author author : data.getProfileAuthors()) {
            profilesHBox.getChildren().add(this.createProfileAuthorCard(author));
        }
        ScrollPane scrollPane = new ScrollPane(profilesHBox);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("scroll-pane");
        profileSection.getChildren().addAll(profileTitle, scrollPane);
        return profileSection;
    }

    private Node createArticlesSection(ScholarGeneral data) {
        VBox articlesSection = new VBox(10);
        Label articlesTitle = new Label("Articles & Authors");
        articlesTitle.getStyleClass().add("section-title");
        for (OrganicResults result : data.getOrganicResults()) {
            articlesSection.getChildren().add(createOrganicResultCard(result));
        }
        return articlesSection;
    }

    private Node createProfileAuthorCard(Author author) {
        VBox card = new VBox(5);
        card.getStyleClass().add("profile-card");
        Label nameLabel = new Label(author.getName());
        nameLabel.getStyleClass().add("card-title");
        Hyperlink detailsLink = new Hyperlink("View Details");
        if (author.getAuthorId() != null && !author.getAuthorId().isEmpty()) {
            detailsLink.setOnAction(_ -> showAuthorDetails(author));
        } else {
            detailsLink.setDisable(true);
        }
        card.getChildren().addAll(nameLabel, detailsLink);
        return card;
    }

    private Node createOrganicResultCard(OrganicResults result) {
        VBox card = new VBox(10);
        card.getStyleClass().add("article-card");
        Hyperlink titleLink = new Hyperlink(result.getTitle());
        titleLink.getStyleClass().add("card-title");
        titleLink.setWrapText(true);
        if (result.getLink() != null && !result.getLink().isEmpty()) {
            titleLink.setOnAction(_ -> hostServices.showDocument(result.getLink()));
        }
        Label snippetLabel = new Label(result.getSnippet());
        snippetLabel.getStyleClass().add("card-snippet");
        card.getChildren().addAll(titleLink, snippetLabel);
        if (result.getAuthorsList() != null && !result.getAuthorsList().isEmpty()) {
            Label authorsHeading = new Label("Authors:");
            authorsHeading.getStyleClass().add("card-author-heading");
            FlowPane authorsPane = new FlowPane(10, 5);
            for (Author author : result.getAuthorsList()) {
                Hyperlink authorLink = new Hyperlink(author.getName());
                if (author.getAuthorId() != null && !author.getAuthorId().isEmpty()) {
                    authorLink.setOnAction(_ -> showAuthorDetails(author));
                } else {
                    authorLink.setDisable(true);
                }
                authorsPane.getChildren().add(authorLink);
            }
            card.getChildren().addAll(authorsHeading, authorsPane);
        }
        return card;
    }


    // MORE OF LOGIC AND API CALLS ----------------------------------------------------------------
    private void searchByAuthor(String query) {
        if (query == null || query.trim().isEmpty()) {
            cardsContainer.getChildren().clear();
            cardsContainer.getChildren().add(new Label("Please enter an author to search."));
            return;
        }
        cardsContainer.getChildren().clear();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        VBox searchingBox = new VBox(10, new Label("Searching..."), progressIndicator);
        searchingBox.setAlignment(Pos.CENTER);
        cardsContainer.getChildren().add(searchingBox);
        new Thread(() -> {
            try {
                // BUILD API REQUEST
                configFile = jsonHandler.getConfigFileApi();
                final String ENGINE = configFile.get("author_articles").get("engine");
                final String RESTRICTIONS = configFile.get("author_articles").get("restrictions");
                Map<String, String> parameters = new HashMap<>();
                parameters.put("q", "author:"+query);
                String apiUrl = ApiUrlBuilder.buildUrl(ENGINE, RESTRICTIONS, parameters);

                // API REQUEST
                ScholarGeneral data = authorController.searchByAuthorAndTopic(apiUrl);
                Platform.runLater(() -> displayResults(data));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                Platform.runLater(() -> {
                    cardsContainer.getChildren().clear();
                    cardsContainer.getChildren().add(new Label("Error: Could not fetch data."));
                });
            }
        }).start();
    }

    private void searchByTopic(String query) {
        if (query == null || query.trim().isEmpty()) {
            cardsContainer.getChildren().clear();
            cardsContainer.getChildren().add(new Label("Please enter a topic to search."));
            return;
        }

        cardsContainer.getChildren().clear();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        VBox searchingBox = new VBox(10, new Label("Searching..."), progressIndicator);
        searchingBox.setAlignment(Pos.CENTER);
        cardsContainer.getChildren().add(searchingBox);

        new Thread(() -> {
            try {
                // SEARCH BY TOPIC URL CREATION
                final String ENGINE = this.configFile.get("author_articles").get("engine");
                final String RESTRICTIONS = this.configFile.get("author_articles").get("restrictions");
                Map<String, String> parameters = new HashMap<>();
                parameters.put("q", query);
                String apiUrl = ApiUrlBuilder.buildUrl(ENGINE, RESTRICTIONS, parameters);

                ScholarGeneral data = authorController.searchByAuthorAndTopic(apiUrl);
                Platform.runLater(() -> displayResults(data));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                Platform.runLater(() -> {
                    cardsContainer.getChildren().clear();
                    cardsContainer.getChildren().add(new Label("Error: Could not fetch data."));
                });
            }
        }).start();
    }

    private void displayResults(ScholarGeneral data) {
        cardsContainer.getChildren().clear();
        boolean hasProfiles = data != null && data.getProfileAuthors() != null && !data.getProfileAuthors().isEmpty();
        boolean hasArticles = data != null && data.getOrganicResults() != null && !data.getOrganicResults().isEmpty();
        if (!hasProfiles && !hasArticles) {
            cardsContainer.getChildren().add(new Label("No results found for your query."));
            return;
        }
        if (hasProfiles) {
            cardsContainer.getChildren().add(createProfileSection(data));
        }
        if (hasArticles) {
            cardsContainer.getChildren().add(createArticlesSection(data));
        }
    }

    private void showAuthorDetails(Author author) {
        AuthorDetailView detailView = new AuthorDetailView(
                author,
                this.authorController,
                () -> this.searchScene.setRoot(searchRoot),
                this.hostServices
        );

        this.searchScene.setRoot(detailView);
    }
}