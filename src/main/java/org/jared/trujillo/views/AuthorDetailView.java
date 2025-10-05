package org.jared.trujillo.views;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.jared.trujillo.classes.types.scholar_data.ScholarAuthor;
import org.jared.trujillo.controllers.ArticleController;
import org.jared.trujillo.controllers.AuthorController;
import org.jared.trujillo.models.Article;
import org.jared.trujillo.models.author.Author;
import org.jared.trujillo.models.author.DetailedAuthor;
import org.jared.trujillo.utils.ApiUrlBuilder;
import org.jared.trujillo.utils.JacksonHandler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AuthorDetailView extends BorderPane {

    private final VBox contentArea;
    private final Map<String, Map<String, String>> configFile;
    private final HostServices hostServices;

    private final AuthorController authorController;
    private final ArticleController articleController;

    public AuthorDetailView(
            Author author,
            AuthorController authorController,
            Runnable onBack,
            HostServices hostServices
    ) {
        JacksonHandler jsonHandler = new JacksonHandler();
        this.configFile = jsonHandler.getConfigFileApi();
        this.hostServices = hostServices;
        this.authorController = authorController;
        this.articleController = new ArticleController();

        this.setPadding(new Insets(10));
        Button backButton = new Button("← Back to Search");
        backButton.setOnAction(_ -> onBack.run());

        HBox topBar = new HBox(20, backButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        this.setTop(topBar);

        this.contentArea = new VBox(20);
        this.contentArea.setAlignment(Pos.CENTER);
        this.contentArea.setPadding(new Insets(20));
        this.setCenter(contentArea);

        fetchAuthorDetails(author);
    }

    // HANDLE FETCHED DATA
    private void fetchAuthorDetails(Author author) {
        ProgressIndicator progress = new ProgressIndicator();
        contentArea.getChildren().addAll(new Label("Fetching details for " + author.getName() + "..."), progress);

        new Thread(() -> {
            try {
                // API CONSTRUCTIONS
                final String ENGINE = this.configFile.get("author_profile").get("engine");
                final String RESTRICTIONS = this.configFile.get("author_profile").get("restrictions");
                Map<String, String> params = new HashMap<>();
                params.put("author_id", author.getAuthorId());
                String apiUrl = ApiUrlBuilder.buildUrl(ENGINE, RESTRICTIONS, params);

                // TODO: HTTP REQUEST, HANDLE ERROR RESPONSE
                ScholarAuthor scholarAuthorResponse = this.authorController.searchByAuthorId(apiUrl, author.getAuthorId());

                DetailedAuthor detailedAuthor = scholarAuthorResponse.getAuthor();
                detailedAuthor.setLink(author.getLink());

                Platform.runLater(() -> displayFetchedData(detailedAuthor));

            } catch (Exception e) {
                System.out.println(e.getMessage());
                Platform.runLater(() -> showError("Could not load author details."));
            }
        }).start();
    }

    // UI TO DISPLAY FETCHED DATA
    private void displayFetchedData(DetailedAuthor author) {
        contentArea.getChildren().clear();
        contentArea.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label("Author Details");
        titleLabel.setId("mainTitle");

        ImageView thumbnailView = null;
        if (author.getThumbnail() != null && !author.getThumbnail().isEmpty()) {
            try {
                thumbnailView = getImageView(author);
            } catch (Exception e) {
                System.err.println("Error loading thumbnail image: " + e.getMessage());
            }
        }

        Label nameLabel = new Label(author.getName());
        nameLabel.getStyleClass().add("section-title");

        Label emailLabel = new Label("Email: " + author.getEmail());
        Label affiliationLabel = new Label("Affiliation: " + author.getAffilations());
        Hyperlink googleScholarLink = new Hyperlink("View Google Scholar Profile");
        googleScholarLink.setOnAction(_ -> hostServices.showDocument(author.getLink()));

        VBox authorInfoBox = new VBox(10, nameLabel, emailLabel, affiliationLabel, googleScholarLink);
        HBox authorHeaderBox = new HBox(20);
        if (thumbnailView != null) {
            authorHeaderBox.getChildren().add(thumbnailView);
        }
        authorHeaderBox.getChildren().add(authorInfoBox);

        VBox articlesSection = this.createArticlesSection(author.getArticles());

        ScrollPane articlesScrollPane = new ScrollPane(articlesSection);
        articlesScrollPane.setFitToWidth(true); // Prevents horizontal scrollbar
        articlesScrollPane.getStyleClass().add("scroll-pane");

        VBox.setVgrow(articlesScrollPane, Priority.ALWAYS);


        contentArea.getChildren().addAll(
                titleLabel,
                authorHeaderBox,
                articlesScrollPane
        );
    }

    private VBox createArticlesSection(List<Article> articles) {
        VBox articlesContainer = new VBox(15);
        articlesContainer.setPadding(new Insets(20, 0, 0, 0));

        Label articlesTitle = new Label("Articles");
        articlesTitle.getStyleClass().add("section-title");
        articlesContainer.getChildren().add(articlesTitle);

        if (articles == null || articles.isEmpty()) {
            articlesContainer.getChildren().add(new Label("No articles found for this author."));
        } else {
            for (Article article : articles) {
                Node articleCard = this.createArticleCard(article);
                articlesContainer.getChildren().add(articleCard);
            }
        }
        return articlesContainer;
    }

    private Node createArticleCard(Article article) {
        VBox infoBox = new VBox(8);
        Hyperlink titleLink = new Hyperlink(article.getTitle());
        titleLink.setWrapText(true);
        titleLink.setOnAction(_ -> hostServices.showDocument(article.getLink()));
        titleLink.getStyleClass().add("card-title");

        Label authorsLabel = new Label(article.getAuthors());
        authorsLabel.getStyleClass().add("card-snippet");

        Label publicationLabel = new Label(article.getPublication());
        publicationLabel.getStyleClass().add("card-snippet");

        Label citedByLabel = new Label("Cited by: " + article.getCitedBy());
        citedByLabel.getStyleClass().add("card-author-heading");

        infoBox.getChildren().addAll(titleLink, authorsLabel, publicationLabel, citedByLabel);

        Button insertButton = this.getButton(article);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox cardLayout = new HBox(10, infoBox, spacer, insertButton);
        cardLayout.setPadding(new Insets(10));
        cardLayout.getStyleClass().add("article-card");
        cardLayout.setAlignment(Pos.CENTER_LEFT);

        return cardLayout;
    }

    private Button getButton(Article article) {
        Button insertButton = new Button("Insert into Database");

        insertButton.setOnAction(e -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Insertion");
            confirmationAlert.setHeaderText("Add article to the database?");
            confirmationAlert.setContentText("Are you sure you want to save the article:\n\"" + article.getTitle() + "\"?");

            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                handleInsertArticle(article, insertButton);
            }
        });
        return insertButton;
    }

    private void handleInsertArticle(Article article, Button button) {
        // 1. Give immediate feedback that we are starting the process
        button.setDisable(true);
        button.setText("Checking...");

        new Thread(() -> {
            try {
                // 2. Call the controller to check if the article exists
                if (this.articleController.articleExists(article)) {

                    // --- ARTICLE IS A DUPLICATE ---
                    // Notify the user and reset the button
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.INFORMATION, "Duplicate Article", "This article already exists in the database.");
                        button.setText("Insert into Database");
                        button.setDisable(false);
                    });

                } else {

                    // --- ARTICLE IS NEW, PROCEED WITH INSERTION ---
                    Platform.runLater(() -> button.setText("Saving..."));
                    this.articleController.insertArticle(article);

                    // Notify the user of success
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "The article was successfully saved!");
                        button.setText("Saved!"); // Keep button disabled
                    });
                }
            } catch (SQLException e) {
                // 3. Catch any database error from either the check or the insert
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred: " + e.getMessage());
                    button.setText("Insert into Database");
                    button.setDisable(false);
                });
            }
        }).start();
    }

    /**
     * A helper method to create and show alerts, reducing duplicate code.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static ImageView getImageView(DetailedAuthor author) {
        Image thumbnailImage = new Image(author.getThumbnail());
        ImageView thumbnailView = new ImageView(thumbnailImage);
        thumbnailView.setFitWidth(100);
        thumbnailView.setFitHeight(100);
        thumbnailView.setPreserveRatio(true);
        thumbnailView.setSmooth(true);
        thumbnailView.setCache(true);
        return thumbnailView;
    }

    private void showError(String message) {
        contentArea.getChildren().clear();
        contentArea.setAlignment(Pos.CENTER);
        contentArea.getChildren().add(new Label(message));
    }
}