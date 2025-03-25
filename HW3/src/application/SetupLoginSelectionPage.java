package application;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import databasePart1.*;

/**
 * The SetupLoginSelectionPage class allows users to choose between setting up a new account
 * or logging into an existing account. It provides two buttons for navigation to the respective pages.
 */
public class SetupLoginSelectionPage {
    
    private final DatabaseHelper databaseHelper;
    private static final double BTN_WIDTH = 100; // R.McQuesten, 2025-02-21, Set btn width for consistency

    public SetupLoginSelectionPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        // Labels
    	Label titleLabel = new Label("*** WELCOME ***");
        Label hiLbl = new Label("Choose an option below:");
        Label setupLbl = new Label("Account Setup:");
        Label loginLbl = new Label("Account Login:");
        Label quitLbl = new Label("Quit:");
        
        // Set label styles
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        hiLbl.setStyle("-fx-font-size: 16px; -fx-padding: 5 0 10 0;");        
        setupLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        loginLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        quitLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Buttons
        Button setupButton = new Button("SetUp");
        Button loginButton = new Button("Login");
        Button quitBtn = new Button("Quit");
        
        // Set button widths
        setupButton.setPrefWidth(BTN_WIDTH);
        loginButton.setPrefWidth(BTN_WIDTH);
        quitBtn.setPrefWidth(BTN_WIDTH);
      
        // Action handlers
        
        // Handle setup account
        setupButton.setOnAction(a -> {
            new SetupAccountPage(databaseHelper).show(primaryStage);
        });
        
        // Handle login to account
        loginButton.setOnAction(a -> {
            new UserLoginPage(databaseHelper).show(primaryStage);
        });
        
	    // R.McQuesten, 2025-02-21, Quit btn action handler
	    quitBtn.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });	 

        // Create a GridPane for alignment
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(15);

        // Place label & button pairs in the grid
        gridPane.add(setupLbl, 0, 0);
        gridPane.add(setupButton, 1, 0);
        gridPane.add(loginLbl, 0, 1);
        gridPane.add(loginButton, 1, 1);
        gridPane.add(quitLbl, 0, 2);
        gridPane.add(quitBtn, 1, 2);

        // Create a VBox for the main layout
        VBox mainLayout = new VBox(15);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-padding: 20;");

        // Add all nodes to the VBox
        mainLayout.getChildren().addAll(
            titleLabel,
            hiLbl,
            gridPane
        );

        // Set scene
        Scene scene = new Scene(mainLayout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Account Setup/Login");
        primaryStage.show();
    }
}
