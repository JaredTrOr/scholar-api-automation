package org.jared.trujillo.views;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane; // Import ScrollPane
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority; // Import Priority
import javafx.scene.layout.VBox;
import org.jared.trujillo.classes.types.scholar_data.ScholarAuthor;
import org.jared.trujillo.controllers.AuthorController;
import org.jared.trujillo.models.Article;
import org.jared.trujillo.models.author.Author;
import org.jared.trujillo.models.author.DetailedAuthor;
import org.jared.trujillo.utils.ApiUrlBuilder;
import org.jared.trujillo.utils.JacksonHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorDetailView extends BorderPane {

    private final VBox contentArea;
    private final AuthorController authorController;
    private final Map<String, Map<String, String>> configFile;
    private final HostServices hostServices;

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

        VBox articlesSection = createArticlesSection(author.getArticles());

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
                Node articleCard = createArticleCard(article);
                articlesContainer.getChildren().add(articleCard);
            }
        }
        return articlesContainer;
    }

    private Node createArticleCard(Article article) {
        VBox card = new VBox(8);
        card.getStyleClass().add("article-card");
        card.setPadding(new Insets(10));

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

        card.getChildren().addAll(titleLink, authorsLabel, publicationLabel, citedByLabel);
        return card;
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