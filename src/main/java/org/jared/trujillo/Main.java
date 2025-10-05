package org.jared.trujillo;

import javafx.application.Application;
import org.jared.trujillo.views.BrowserView; // Import your new UI class

public class Main {
    public static void main(String[] args) {
        Application.launch(BrowserView.class, args);
    }
}