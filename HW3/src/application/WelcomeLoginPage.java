package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import databasePart1.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show( Stage primaryStage, User user) {
    	
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Label welcomeLabel = new Label("*** Welcome!! ***");
	    welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
	    
	    // Button to navigate to the user's respective page based on their role
	    Button continueButton = new Button("Continue to your Page");
	    continueButton.setOnAction(a -> {
	    	String role = user.getRole();
	    	System.out.println(role);
	    	
	    	if(role.equals("Admin")) {
	    		new AdminHomePage(user, databaseHelper).show(primaryStage);
	    	} else if(role.equals("Student")){
	    		new StudentHomePage(user, databaseHelper).show(primaryStage);
	    		
	    	} else if(role.equals("Instructor")){
	    		new InstructorHomePage(user, databaseHelper).show(primaryStage);
	    		
	    	} else if(role.equals("Staff")) {
	    		new StaffHomePage(user, databaseHelper).show(primaryStage);//Arnav Lohiya Fefb 5th: Changed from InstrcutorHomePage to StaffHomePage.
	    		
	    	} else if(role.equals("Reviewer")) {
	    		new ReviewerHomePage(user, databaseHelper).show(primaryStage);
	    		
	    	} else if(role.equals("user")) {
	    		new UserHomePage(user, databaseHelper).show(primaryStage);	
	    	}
	    }); //end of continueButton 
	    
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });
	    
	    layout.getChildren().addAll(welcomeLabel,continueButton, quitButton);
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page"); 
    }
}
