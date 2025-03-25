package application;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
    // R.McQuesten, 2025-02-21, Set const so we can change this once and it'll cascade evenly across the platform
    private static final String TITLE_STYLE = "-fx-font-size: 24px; -fx-font-weight: bold;";
    private static final String INSTR_STYLE = "-fx-font-size: 16px; -fx-padding: 5 0 10 0;";
    private static final String GEN_STYLE = "-fx-font-size: 14px; -fx-font-weight: bold;";
    private static final String ERROR_STYLE = "-fx-text-fill: red; -fx-font-size: 12px;";
    private static final double BTN_WIDTH = 250; 	// R.McQuesten, 2025-02-21, Set btn width for consistency
    private static final double INPUT_WIDTH = 250;	// R.McQuesten, 2025-02-21, Set input field width so can change later easily    
    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        // Labels
    	Label titleLabel = new Label("*** ACCOUNT LOGIN ***");      // Label to show title
        Label hiLbl = new Label("Enter your credentials below:");	// Label for user to know what to do       
        Label userNameLbl = new Label("Username:"); 				// Label for the username text field       
        Label passwordLbl = new Label("Password:"); 				// Label for the password/OTP text field
        Label loginLbl = new Label("Login:"); 						// Label for login
        Label backLbl = new Label("Back:");							// Label to return to previous page
        Label quitLbl = new Label("Quit:");							// Label to quit application
        Label errorLabel = new Label(); 							// Label to display error messages 

        // Set label style
        titleLabel.setStyle(TITLE_STYLE);
        hiLbl.setStyle(INSTR_STYLE);
        userNameLbl.setStyle(GEN_STYLE);
        passwordLbl.setStyle(GEN_STYLE);
        loginLbl.setStyle(GEN_STYLE);
        backLbl.setStyle(GEN_STYLE);
        quitLbl.setStyle(GEN_STYLE);
        errorLabel.setStyle(ERROR_STYLE);
        
        // Input fields
        TextField userNameField = new TextField(); 			// Input field for userName
        PasswordField passwordField = new PasswordField();  // Input field for user's password or OTP
        
        // Set input field prompt text
        passwordField.setPromptText("Enter Password");
        userNameField.setPromptText("Enter Username");
        
        // Set input field width
        userNameField.setPrefWidth(INPUT_WIDTH);
        passwordField.setPrefWidth(INPUT_WIDTH);

        // Buttons
        Button loginButton = new Button("Login"); 	// Button to login to the system
        Button backBtn = new Button("Back");		// Button to return to previous page
        Button quitBtn = new Button("Quit");		// Button to quit the application
        
        // Set btn width
        loginButton.setPrefWidth(BTN_WIDTH);
        backBtn.setPrefWidth(BTN_WIDTH);
        quitBtn.setPrefWidth(BTN_WIDTH);

        // Handle login button
        loginButton.setOnAction(a -> {
            // Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            
            try {
                boolean validOtp = databaseHelper.validateOneTimePassword(userName, password);
                // This is validation for OTP. If valid & used, navigate to password reset page
                // and destroy the one-time password.
                if (validOtp) {
                    new PasswordResetPage(databaseHelper, userName).show(primaryStage);
                    databaseHelper.destroyOneTimePassword(userName);
                    return;
                }            	

                // R.McQuesten, 2025-02-04, Retrieve the user's role from the db for login w/o OTP
                String role = databaseHelper.getUserRole(userName);                
      
                // Get non-critical login info but it's still account info
                String userFullName = databaseHelper.getUserFullName(userName);
                String userEmail = databaseHelper.getUserEmail(userName);
                User user = new User(userName, password, role, userFullName, userEmail);
               
                if (role != null) {
                    // R.McQuesten, 2025-02-05, Additional OTP logic 
                    int userID = databaseHelper.login(user);
                    
                    // More login validation logic
                    if (userID <= 0) {
                        errorLabel.setText("*** ERROR *** Invalid username or password.");
                        return;
                    } else {
                        user.setUserId(userID);
                    }

                    // Arnav Lohiya Feb 5th: Redirection based on how many roles the user has.
                    ArrayList<String> userRoles = databaseHelper.fetchUserRoles(userName);
                    if (userRoles.size() == 0) {
                        errorLabel.setText("*** ERROR *** User doesn't have roles associated with them.");
                        return;
                    }

                    if (userRoles.size() == 1) {
                        // Redirect to specific user's page
                        role = userRoles.get(0);
                        switch (role) {
                            case "Admin":
                                new AdminHomePage(user, databaseHelper).show(primaryStage);
                                break;
                            case "Student":
                                new StudentHomePage(user, databaseHelper).show(primaryStage);
                                break;
                            case "Instructor":
                                new InstructorHomePage(user, databaseHelper).show(primaryStage);
                                break;
                            case "Staff":
                                new InstructorHomePage(user, databaseHelper).show(primaryStage);
                                break;
                            case "Reviewer":
                                new ReviewerHomePage(user, databaseHelper).show(primaryStage);
                                break;
                            case "User":
                                new UserHomePage(user, databaseHelper).show(primaryStage);
                                break;
                        }
                    } else {
                        // Multiple roles: go to role selection page
                        UserRoleSelectionPage userSelectionPage = new UserRoleSelectionPage(user, databaseHelper);
                        userSelectionPage.show(primaryStage, userRoles);
                    }
                } else {
                    errorLabel.setText("*** ERROR *** User account does not exist.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Back button handler
        backBtn.setOnAction(a -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
	    // R.McQuesten, 2025-02-21, Quit btn action handler
	    quitBtn.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });	 
        
        // Create GridPane to align labels & fields
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(15);

        // Place label & input pairs in grid
        // Format of the grid is (var, col, row)        
        gridPane.add(userNameLbl, 0, 0);
        gridPane.add(userNameField, 1, 0);
        gridPane.add(passwordLbl, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(loginLbl, 0, 2);
        gridPane.add(loginButton, 1, 2);
        gridPane.add(backLbl, 0, 3);
        gridPane.add(backBtn, 1, 3); 
        gridPane.add(quitLbl, 0, 4);
        gridPane.add(quitBtn, 1, 4);
        gridPane.add(errorLabel, 0, 5, 2, 1);

        // Set main layout
        VBox mainLayout = new VBox(15);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-padding: 20;");

        // Add labels to main layout
        mainLayout.getChildren().addAll(
            titleLabel,
            hiLbl,
            gridPane
        );

        // Set scene
        Scene scene = new Scene(mainLayout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}
