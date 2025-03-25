package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;

import databasePart1.DatabaseHelper;

//Auythor: Arnav Lohiya
//Date: Feb 5th, 2025
//Description: This page allows a user who has multiple roles to select which role they want to perform operations as. Once the role option (Button) is clicked, they are redirected to that role's page.

public class UserRoleSelectionPage {

	private final DatabaseHelper databaseHelper;
	private User user;//Arnav Lohiya Feb 12th
	public UserRoleSelectionPage(User user, DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
		this.user = user;
	}
    public void show(Stage primaryStage, ArrayList<String> roles) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label titleLabel = new Label("Hello, it looks like you have multiple roles!");
	    titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    Label subtitlelabel = new Label("Choose what role you would like to perform.");
	    subtitlelabel.setStyle("-fx-font-size: 14px;");

	    layout.getChildren().addAll(titleLabel, subtitlelabel);
	    Scene userScene = new Scene(layout, 800, 400);
	    
	    //Run a loop through the roles list and create buttons and redirection links for each button according to the role mentioned.
	    for(int i = 0; i < roles.size(); i++) {
	    	 final int index = i;
	    	Button roleButton = new Button(roles.get(i));
	    	roleButton.setOnAction(a-> {
	    		if (roles.get(index).equals("Admin")) {
		    		new AdminHomePage(user, databaseHelper).show(primaryStage);
		    	} else if(roles.get(index).equals("Student")){
		    		new StudentHomePage(user,databaseHelper).show(primaryStage);
		    		
		    	} else if(roles.get(index).equals("Instructor")){
		    		new InstructorHomePage(user, databaseHelper).show(primaryStage);
		    		
		    	} else if(roles.get(index).equals("Staff")) {
		    		new StaffHomePage(user, databaseHelper).show(primaryStage);
		    		
		    	} else if(roles.get(index).equals("Reviewer")) {
		    		new ReviewerHomePage(user, databaseHelper).show(primaryStage);
		    		
		    	} else if(roles.get(index).equals("User")) {
		    		new UserHomePage(user, databaseHelper).show(primaryStage);
			}
	    	});
	    	layout.getChildren().add(roleButton);
	    	
	    	}
  
	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Role Selection Page");
    	
    }
}
