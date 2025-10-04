package org.jared.trujillo.views;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jared.trujillo.controllers.AuthorController;
import org.jared.trujillo.handlers.ApiUrlBuilder;
import org.jared.trujillo.handlers.JacksonHandler;
import org.jared.trujillo.models.authors.Author;

import java.util.HashMap;
import java.util.Map;

// This class is a self-contained component for the detail screen.
public class AuthorDetailView extends BorderPane {

    private VBox contentArea;
    private AuthorController authorController;
    private JacksonHandler jsonHandler;
    private Map<String, Map<String, String>> configFile;

    /**
     * Constructor for the Author Detail View.
     * @param author The author whose details are to be displayed.
     * @param authorController A controller instance to make API calls.
     * @param onBack A callback function to execute when the back button is pressed.
     */
    public AuthorDetailView(Author author, AuthorController authorController, Runnable onBack) {
        this.configFile = jsonHandler.getConfigFileApi();

        this.authorController = authorController;
        this.setPadding(new Insets(10));

        Button backButton = new Button("← Back to Search");
        backButton.setOnAction(e -> onBack.run()); // Execute the provided back action

        Label titleLabel = new Label("Author Details");
        titleLabel.setId("mainTitle");

        HBox topBar = new HBox(20, backButton, titleLabel);
        topBar.setAlignment(Pos.CENTER_LEFT);
        this.setTop(topBar);

        this.contentArea = new VBox(20);
        this.contentArea.setAlignment(Pos.CENTER);
        this.contentArea.setPadding(new Insets(20));
        this.setCenter(contentArea);

        fetchAuthorDetails(author);
    }

    /**
     * Fetches author data in a background thread and updates the UI.
     * @param author The author to fetch details for.
     */
    private void fetchAuthorDetails(Author author) {
        // Show loading indicator
        ProgressIndicator progress = new ProgressIndicator();
        contentArea.getChildren().addAll(new Label("Fetching details for " + author.getName() + "..."), progress);

        new Thread(() -> {
            try {
                // This logic is now moved from BrowserView into this dedicated class
                final String ENGINE = this.configFile.get("author_profile").get("engine");
                final String RESTRICTIONS = this.configFile.get("author_profile").get("restrictions");
                Map<String, String> params = new HashMap<>();
                params.put("author_id", author.getAuthorId());

                String apiUrl = ApiUrlBuilder.buildUrl(ENGINE, RESTRICTIONS, params);

                // TODO: Implement the real API call in your controller
                // AuthorDetails details = this.authorController.getAuthorDetails(apiUrl);

                // --- MOCK DATA FOR DEMONSTRATION ---
                Thread.sleep(1500);
                String mockName = author.getName();
                String mockAffiliation = "University of Advanced Studies";
                int mockCitations = (int) (Math.random() * 25000 + 500);
                // --- End Mock Data ---

                Platform.runLater(() -> displayFetchedData(mockName, mockAffiliation, mockCitations));

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showError("Could not load author details."));
            }
        }).start();
    }

    private void displayFetchedData(String name, String affiliation, int citations) {
        contentArea.getChildren().clear();
        contentArea.setAlignment(Pos.TOP_LEFT);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("section-title");

        Label affiliationLabel = new Label("Affiliation: " + affiliation);
        Label citationsLabel = new Label("Total Citations: " + citations);
        // TODO: Add more detailed fields here (h-index, articles, etc.)

        contentArea.getChildren().addAll(nameLabel, affiliationLabel, citationsLabel);
    }

    private void showError(String message) {
        contentArea.getChildren().clear();
        contentArea.setAlignment(Pos.CENTER);
        contentArea.getChildren().add(new Label(message));
    }
}