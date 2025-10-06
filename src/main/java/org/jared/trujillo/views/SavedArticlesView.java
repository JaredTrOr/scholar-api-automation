package org.jared.trujillo.views;

import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.jared.trujillo.controllers.ArticleController;
import org.jared.trujillo.models.Article;

import java.awt.*;
import java.lang.Runnable;
import java.sql.SQLException;
import java.util.List;

public class SavedArticlesView extends BorderPane {

    private final TableView<Article> tableView = new TableView<>();
    private final ArticleController articleController;
    private final Runnable backAction;
    private final HostServices hostServices;

    public SavedArticlesView(
            ArticleController articleController,
            Runnable backAction,
            HostServices hostServices
    ) {
        this.articleController = articleController;
        this.backAction = backAction;
        this.hostServices = hostServices;
        setupUI();
        loadArticles();
    }

    private void setupUI() {
        Button backButton = new Button("← Back to Search");
        backButton.setOnAction(e -> this.backAction.run());
        Label titleLabel = new Label("Saved Articles");
        titleLabel.setId("mainTitle");
        BorderPane headerPane = new BorderPane();
        headerPane.setLeft(backButton);
        headerPane.setCenter(titleLabel);
        headerPane.setPadding(new Insets(0, 0, 10, 0));

        TableColumn<Article, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(300);

        TableColumn<Article, String> authorsCol = new TableColumn<>("Authors");
        authorsCol.setCellValueFactory(new PropertyValueFactory<>("authors"));
        authorsCol.setPrefWidth(250);

        TableColumn<Article, String> publicationCol = new TableColumn<>("Publication");
        publicationCol.setCellValueFactory(new PropertyValueFactory<>("publication")); // Assumes property is 'publicationDate'
        publicationCol.setPrefWidth(200);

        TableColumn<Article, Integer> citedByCol = new TableColumn<>("Cited By");
        citedByCol.setCellValueFactory(new PropertyValueFactory<>("citedBy"));
        citedByCol.setPrefWidth(80);

        // -- Special Column for the Clickable Link --
        TableColumn<Article, String> linkCol = new TableColumn<>("Link");
        linkCol.setCellValueFactory(new PropertyValueFactory<>("link"));
        linkCol.setCellFactory(column -> new TableCell<Article, String>() {
            private final Hyperlink link = new Hyperlink();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    link.setText("Open Link");
                    link.setOnAction(e -> hostServices.showDocument(item));
                    setGraphic(link);
                }
            }
        });
        linkCol.setPrefWidth(100);

        TableColumn<Article, Void> deleteCol = new TableColumn<>("Actions");
        deleteCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.setOnAction(event -> {
                    Article article = getTableView().getItems().get(getIndex());
                    handleDeleteArticle(article);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });

        tableView.getColumns().addAll(titleCol, authorsCol, publicationCol, citedByCol, linkCol, deleteCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox contentBox = new VBox(10, headerPane, tableView);
        contentBox.setPadding(new Insets(10));
        this.setCenter(contentBox);
    }

    private void loadArticles() {
        try {
            List<Article> articlesList = articleController.getAllSavedArticles();

            ObservableList<Article> observableArticles = FXCollections.observableArrayList(articlesList);

            tableView.setItems(observableArticles);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load saved articles.");
        }
    }

    private void handleDeleteArticle(Article article) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the article: '" + article.getTitle() + "'?", ButtonType.YES, ButtonType.CANCEL);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Article?");

        confirmation.showAndWait().ifPresent(response -> {

            if (response == ButtonType.YES) {
                try {
                    articleController.deleteArticleById(article.getCitationId());
                    tableView.getItems().remove(article);
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Could not delete the article.");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}