package org.jared.trujillo.controllers;

import org.jared.trujillo.db.DatabaseManager;
import org.jared.trujillo.models.Article;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArticleController {

    public boolean articleExists(Article article) throws SQLException {
        String sql = "SELECT 1 FROM public.articles WHERE link = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, article.getLink());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if a record is found
            }
        }
    }

    public void insertArticle(Article article) throws SQLException {
        String sql = "INSERT INTO public.articles(title, citationId, authors, publicationDate, abstract, link, keywords, citedBy) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, article.getTitle());
            pstmt.setString(2, article.getCitationId());
            pstmt.setString(3, article.getAuthors());
            pstmt.setString(4, article.getPublication());
            pstmt.setString(5, "No abstract available");
            pstmt.setString(6, article.getLink());
            pstmt.setString(7, "No keywords available");
            pstmt.setInt(8, article.getCitedBy());

            pstmt.executeUpdate();
        }
    }

}
