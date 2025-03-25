package application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;
import java.util.Optional;

//Anthony Hernandez feb 3
//added to give admin access to admin commands
import databasePart1.*;

/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

@SuppressWarnings("unused")
public class AdminHomePage {
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
	
	private final DatabaseHelper databaseHelper;
	private Connection connection = null; 			// ?
	private Statement statement = null; 			// ?
    private static final double BTN_WIDTH = 200; 	// R.McQuesten, 2025-02-21, Set btn width for consistency
	private User user;
    
	public AdminHomePage(User user, DatabaseHelper databaseHelper) {
			this.user = user;
	        this.databaseHelper = databaseHelper;
	}
	
    public void show(Stage primaryStage) {
	    // Labels
	    Label adminLabel = new Label("*** Hello, " + user.getUserFullName() + " ***"); 	// label to display the welcome message for the admin
	    adminLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
	    
	    // Buttons
	    Button inviteButton = new Button("Invite Users");				//invite page button 
	    Button adminCommandsButton = new Button("Modify Users"); 		//admin command button
	    Button otpBtn = new Button("Assign OTP");		  		 		// R.McQuesten, 2025-02-05, OTP btn
	    Button logoutBtn = new Button("Logout");						// R.McQuesten, 2025-02-21, Logout btn
	    Button quitBtn = new Button("Quit");							// R.McQuesten, 2025-02-21, Quit btn

	    // R.McQuesten, 2025-02-21, Set button width
	    inviteButton.setPrefWidth(BTN_WIDTH);
	    adminCommandsButton.setPrefWidth(BTN_WIDTH);
	    otpBtn.setPrefWidth(BTN_WIDTH);
	    logoutBtn.setPrefWidth(BTN_WIDTH);
	    quitBtn.setPrefWidth(BTN_WIDTH);
	    
	    // Action handlers
	    
	    // R.McQuesten, 2025-02-05, OTP btn action handler
	    otpBtn.setOnAction(a -> {
	    	TextInputDialog dialogBox = new TextInputDialog();
	    	dialogBox.setTitle("Create One-Time Password");
	    	dialogBox.setHeaderText("Enter Username to assign OTP to:");
	    	Optional<String> res = dialogBox.showAndWait();
	    	
	    	res.ifPresent(userName -> {
	    		String otp = databaseHelper.getOneTimePassword(userName);
	    		
	    		if (otp != null) {
	    			Alert alert = new Alert(Alert.AlertType.INFORMATION);
	    			alert.setTitle("OTP Creation Success");
	    			alert.setHeaderText("One-Time Password for " + userName);
	    			alert.setContentText("Created OTP: " + otp);
	    			alert.showAndWait();
	    		} else {
	    			Alert alertError = new Alert(Alert.AlertType.ERROR);
	    			alertError.setTitle("*** ERROR ***");
	    			alertError.setHeaderText("OTP Creation Failed");
	    			alertError.setContentText("Invalid Username: The username entered does not exist");
	    			alertError.showAndWait();
	    		}
	        });
	    });	    
        
	    // Handle invite btn
	    inviteButton.setOnAction(a -> {
            Scene currentScene = primaryStage.getScene();
            new RoleInvitationSelectorPage(currentScene, databaseHelper, primaryStage).show();
        });
        
	    // Handle admin commands btn
        adminCommandsButton.setOnAction(a -> {
        	new AdminCommandsPage(user, databaseHelper).show(databaseHelper, primaryStage);
        });
        
	    // R.McQuesten, 2025-02-21, Logout btn action handler
	    logoutBtn.setOnAction(a -> {
	    	Alert confirmLogout = new Alert(Alert.AlertType.CONFIRMATION,
	    			"Are you sure you want to logout?", ButtonType.YES, ButtonType.NO);
	    	confirmLogout.setTitle("Confirm Logout");
	    	confirmLogout.setHeaderText("Logout");
	    	confirmLogout.showAndWait().ifPresent(r -> {
	    		if (r == ButtonType.YES) {
	    			new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
	    		}
	    	});
	    });
	    
	    // R.McQuesten, 2025-02-21, Quit btn action handler
	    quitBtn.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });	   
        
    	// Set layout
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        layout.getChildren().addAll(adminLabel, inviteButton, adminCommandsButton, otpBtn);
        addDeleteUserButton(layout, databaseHelper);
        layout.getChildren().addAll(logoutBtn, quitBtn);

        // Create admin scene
        Scene adminScene = new Scene(layout, 800, 400);	
        
	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");   
    }
      
    // ENZO'S Added feature: Admin can delete users but not themselves or other admins
    private void showUserDeletionPrompt(DatabaseHelper databaseHelper) {
        List<String> users = databaseHelper.getAllUsers();
        users.remove("admin"); // Ensure admin can not be removed

        if (users.isEmpty()) {
            showAlert("*** ERROR *** No Users", "There are no users available to delete.", Alert.AlertType.INFORMATION);
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(users.get(0), users);
        dialog.setTitle("Delete User");
        dialog.setHeaderText("Select a user to delete:");
        dialog.setContentText("Choose user:");
        dialog.showAndWait().ifPresent(selectedUser -> {
            // Confirmation dialog before deletion
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, 
                "Are you sure you want to delete user '" + selectedUser + "'?", ButtonType.YES, ButtonType.NO);
            confirmation.showAndWait();

            if (confirmation.getResult() == ButtonType.YES) {
                try {
                    databaseHelper.deleteUser(selectedUser);
                    showAlert("*** SUCCESS ***", "User '" + selectedUser + "' has been deleted.", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    showAlert("*** ERROR ***", "Failed to delete user: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    // Helper method to display alerts
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
     
    // Added button for deleting users in the admin interface
    public void addDeleteUserButton(VBox layout, DatabaseHelper databaseHelper) {
        Button deleteUserButton = new Button("Delete User");
        deleteUserButton.setPrefWidth(BTN_WIDTH);
        deleteUserButton.setOnAction(event -> showUserDeletionPrompt(databaseHelper));
        layout.getChildren().add(deleteUserButton);
    }
}
