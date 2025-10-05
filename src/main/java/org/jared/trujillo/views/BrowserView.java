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
import org.jared.trujillo.classes.types.ScholarData;
import org.jared.trujillo.controllers.AuthorController;
import org.jared.trujillo.utils.ApiUrlBuilder;
import org.jared.trujillo.utils.JacksonHandler;
import org.jared.trujillo.classes.types.OrganicResults;
import org.jared.trujillo.models.Author;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BrowserView extends Application {

    private TextField searchField;
    private VBox cardsContainer;
    private AuthorController authorController;
    private final JacksonHandler jsonHandler = new JacksonHandler();
    private Map<String, Map<String, String>> configFile;
    private HostServices hostServices;

    private Stage primaryStage;
    private Scene searchScene;

    // --- CORRECTION 1: Add a field to store the main search layout ---
    private BorderPane searchRoot;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Scholar Search Interface");
        this.hostServices = getHostServices();

        this.authorController = new AuthorController();
        this.cardsContainer = new VBox();
        this.searchField = new TextField();

        // --- CORRECTION 2: Use the 'searchRoot' field to build the main UI ---
        this.searchRoot = new BorderPane();
        VBox topContainer = new VBox(10);
        topContainer.setPadding(new Insets(10, 10, 0, 10));

        Label mainTitle = new Label("Google Scholar API Search");
        mainTitle.setId("mainTitle");
        HBox titleBox = new HBox(mainTitle);
        titleBox.setAlignment(Pos.CENTER);

        HBox searchBar = createSearchBar();
        topContainer.getChildren().addAll(titleBox, searchBar);
        searchRoot.setTop(topContainer);

        cardsContainer.setPadding(new Insets(10));
        cardsContainer.setSpacing(10);
        ScrollPane scrollPane = new ScrollPane(cardsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        searchRoot.setCenter(scrollPane);

        // Create the scene ONCE using the searchRoot and store it
        this.searchScene = new Scene(searchRoot);
        String css = Objects.requireNonNull(this.getClass().getResource("/styles/browser-styles.css")).toExternalForm();
        searchScene.getStylesheets().add(css);

        primaryStage.setScene(searchScene);
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
        authorButton.setOnAction(e -> searchByAuthor(searchField.getText()));
        topicButton.setOnAction(e -> searchByTopic(searchField.getText()));
        searchBar.getChildren().addAll(new Label("Search:"), searchField, authorButton, topicButton);
        return searchBar;
    }

    private Node createProfileAuthorCard(Author author) {
        VBox card = new VBox(5);
        card.getStyleClass().add("profile-card");
        Label nameLabel = new Label(author.getName());
        nameLabel.getStyleClass().add("card-title");
        Hyperlink detailsLink = new Hyperlink("View Details");
        if (author.getAuthorId() != null && !author.getAuthorId().isEmpty()) {
            detailsLink.setOnAction(e -> showAuthorDetails(author));
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
            titleLink.setOnAction(e -> hostServices.showDocument(result.getLink()));
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
                    authorLink.setOnAction(e -> showAuthorDetails(author));
                } else {
                    authorLink.setDisable(true);
                }
                authorsPane.getChildren().add(authorLink);
            }
            card.getChildren().addAll(authorsHeading, authorsPane);
        }
        return card;
    }

    // --- CORRECTION 3: This method now swaps the content (root) instead of the whole Scene ---
    private void showAuthorDetails(Author author) {
        // Create the detail view component
        AuthorDetailView detailView = new AuthorDetailView(
                author,
                this.authorController,
                // The "back" action now sets the root back to our stored search layout
                () -> searchScene.setRoot(searchRoot)
        );

        // Instead of creating a new Scene, just set the root of the EXISTING scene
        searchScene.setRoot(detailView);
    }
    /*
     ================================================================================
     == The following methods are unchanged from your original code. ==
     ================================================================================
    */
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
                configFile = this.jsonHandler.getConfigFileApi();
                final String ENGINE = configFile.get("author_articles").get("engine");
                final String RESTRICTIONS = configFile.get("author_articles").get("restrictions");
                Map<String, String> parameters = new HashMap<>();
                parameters.put("q", query);
                String apiUrl = ApiUrlBuilder.buildUrl(ENGINE, RESTRICTIONS, parameters);
                ScholarData data = authorController.searchByAuthorAndTopic(apiUrl);
                Platform.runLater(() -> displayResults(data));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    cardsContainer.getChildren().clear();
                    cardsContainer.getChildren().add(new Label("Error: Could not fetch data."));
                });
            }
        }).start();
    }

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
                configFile = jsonHandler.getConfigFileApi();
                final String ENGINE = configFile.get("author_articles").get("engine");
                final String RESTRICTIONS = configFile.get("author_articles").get("restrictions");
                Map<String, String> parameters = new HashMap<>();
                parameters.put("q", "author:"+query);
                String apiUrl = ApiUrlBuilder.buildUrl(ENGINE, RESTRICTIONS, parameters);
                ScholarData data = authorController.searchByAuthorAndTopic(apiUrl);
                Platform.runLater(() -> displayResults(data));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    cardsContainer.getChildren().clear();
                    cardsContainer.getChildren().add(new Label("Error: Could not fetch data."));
                });
            }
        }).start();
    }

    private void displayResults(ScholarData data) {
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

    private Node createProfileSection(ScholarData data) {
        VBox profileSection = new VBox(10);
        Label profileTitle = new Label("Profiles (Most Relevant)");
        profileTitle.getStyleClass().add("section-title");
        HBox profilesHBox = new HBox(10);
        profilesHBox.setPadding(new Insets(5));
        for (Author author : data.getProfileAuthors()) {
            profilesHBox.getChildren().add(createProfileAuthorCard(author));
        }
        ScrollPane scrollPane = new ScrollPane(profilesHBox);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("scroll-pane");
        profileSection.getChildren().addAll(profileTitle, scrollPane);
        return profileSection;
    }

    private Node createArticlesSection(ScholarData data) {
        VBox articlesSection = new VBox(10);
        Label articlesTitle = new Label("Articles & Authors");
        articlesTitle.getStyleClass().add("section-title");
        for (OrganicResults result : data.getOrganicResults()) {
            articlesSection.getChildren().add(createOrganicResultCard(result));
        }
        return articlesSection;
    }
}