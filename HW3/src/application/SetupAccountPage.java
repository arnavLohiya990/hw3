package application;

import application.UserNameRecognizer;
import application.PasswordEvaluator;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
@SuppressWarnings("unused")
public class SetupAccountPage {
    // R.McQuesten, 2025-02-21, Set const so we can change this once and it'll cascade evenly across the platform	
    private static final String TITLE_STYLE = "-fx-font-size: 24px; -fx-font-weight: bold;";
    private static final String INSTR_STYLE = "-fx-font-size: 16px; -fx-padding: 5 0 10 0;";
    private static final String GEN_STYLE = "-fx-font-size: 14px; -fx-font-weight: bold;";	
    private static final String ERROR_STYLE = "-fx-text-fill: red; -fx-font-size: 12px;";
    private static final double BTN_WIDTH = 250;              // R.McQuesten, 2025-02-21, Set btn width for consistency
    private static final double INPUT_WIDTH = 250;            // R.McQuesten, 2025-02-21, Set input field width so can change later easily 
    private final DatabaseHelper databaseHelper;              // DatabaseHelper to handle database operations
    
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        // Input fields
        TextField userNameField = new TextField();           // Input field for userName
        TextField userFullNameField = new TextField();       // Input field for user's name
        TextField userEmailField = new TextField();          // Input field for user's email
        PasswordField passwordField = new PasswordField();   // Input field for user's password
        TextField inviteCodeField = new TextField();         // Input field for user's invite code
        
        // Set prompt text
        userNameField.setPromptText("Enter Username");
        userFullNameField.setPromptText("Enter Full Name");
        userEmailField.setPromptText("Enter E-mail");
        passwordField.setPromptText("Enter Password");
        inviteCodeField.setPromptText("Enter InvitationCode");
        
        // Set input field widths
        userNameField.setPrefWidth(INPUT_WIDTH);
        userFullNameField.setPrefWidth(INPUT_WIDTH);
        userEmailField.setPrefWidth(INPUT_WIDTH);
        passwordField.setPrefWidth(INPUT_WIDTH);
        inviteCodeField.setPrefWidth(INPUT_WIDTH);
        
        // Labels
        Label titleLabel = new Label("*** ACCOUNT SETUP ***");   	// Label for title
        Label hiLbl = new Label("Enter your information below:");	// Label for subtitle
        Label userNameLbl = new Label("Username:");					// Label for the username field
        Label fullNameLbl = new Label("Full Name:");				// Label for the full name field
        Label emailLbl = new Label("E-Mail:");     					// Label for the email field
        Label passwordLbl = new Label("Password:");   			 	// Label for the password field
        Label inviteLbl = new Label("Invite Code:"); 				// Label for the invitation code
        Label setupLbl = new Label("Setup:");						// Label for the setup account
        Label backLbl = new Label("Back:");							// Label to return to previous page
        Label quitLbl = new Label("Quit:");							// Label to quit the application
        Label errorLabel = new Label();              			 	// Label for errors
        
        // Set label style
        titleLabel.setStyle(TITLE_STYLE);
        hiLbl.setStyle(INSTR_STYLE);
        userNameLbl.setStyle(GEN_STYLE);
        fullNameLbl.setStyle(GEN_STYLE);
        emailLbl.setStyle(GEN_STYLE);
        passwordLbl.setStyle(GEN_STYLE);
        inviteLbl.setStyle(GEN_STYLE);      
        setupLbl.setStyle(GEN_STYLE);
        backLbl.setStyle(GEN_STYLE);
        quitLbl.setStyle(GEN_STYLE);
        errorLabel.setStyle(ERROR_STYLE);

        // Buttons
        Button setupButton = new Button("Setup");
        Button backBtn = new Button("Back");
        Button quitBtn = new Button("Quit");

        // Set button widths
        setupButton.setPrefWidth(BTN_WIDTH);
        backBtn.setPrefWidth(BTN_WIDTH);
        quitBtn.setPrefWidth(BTN_WIDTH);
        
        // Setup button handler
        setupButton.setOnAction(a -> {
            // R.McQuesten, 2025-02-03, new user fields
            String userName = userNameField.getText();
            String userFullName = userFullNameField.getText();
            String userEmail = userEmailField.getText();
            String password = passwordField.getText();
            String code = inviteCodeField.getText();
            
            // Validate userName
            String userNameVali = UserNameRecognizer.checkForValidUserName(userName);
            if (!userNameVali.isEmpty()) {
                errorLabel.setText(userNameVali);
                return;
            }
            
            // Validate password
            String userPassVali = PasswordEvaluator.checkForPassword(password);
            if (!userPassVali.isEmpty()) {
                errorLabel.setText(userPassVali);
                return;
            }
            
            // Database logic
            try {
                if (!databaseHelper.doesUserExist(userName)) {
                    if (databaseHelper.validateInvitationCode(code)) {
                        List<String> invitationRoles = databaseHelper.getRolesFromInvitationCode(code);
                        ArrayList<String> rolesArrayList = new ArrayList<>(invitationRoles);

                        // Create and register new user
                        User user = new User(userName, password, "user", userFullName, userEmail);
                        databaseHelper.register(user);
                        databaseHelper.updateRolesIntoTable(rolesArrayList, userName);

                        // Navigate to the User Login Page
                        new UserLoginPage(databaseHelper).show(primaryStage);
                    } else {
                        errorLabel.setText("*** ERROR *** Please enter a valid invitation code");
                    }
                } else {
                    errorLabel.setText("*** ERROR *** This User Name is taken! Please use another to setup an account");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
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
        
        // Grid pane to help with alignment
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(15);

        // Add label & fields to grid
        // Format of the grid is (var, col, row)
        gridPane.add(userNameLbl, 0, 0);
        gridPane.add(userNameField, 1, 0);
        gridPane.add(fullNameLbl, 0, 1);
        gridPane.add(userFullNameField, 1, 1);
        gridPane.add(emailLbl, 0, 2);
        gridPane.add(userEmailField, 1, 2);
        gridPane.add(passwordLbl, 0, 3);
        gridPane.add(passwordField, 1, 3);
        gridPane.add(inviteLbl, 0, 4);
        gridPane.add(inviteCodeField, 1, 4);
        gridPane.add(setupLbl, 0, 5);
        gridPane.add(setupButton, 1, 5);
        gridPane.add(backLbl, 0, 6);
        gridPane.add(backBtn, 1, 6);
        gridPane.add(quitLbl, 0, 7);
        gridPane.add(quitBtn, 1, 7);
        gridPane.add(errorLabel, 0, 8, 2, 1);

        // Set main layout
        VBox mainLayout = new VBox(15);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-padding: 20;");

        // Add labels to main layout 
        // And add grid pane to main layout
        mainLayout.getChildren().addAll(
            titleLabel,
            hiLbl,
            gridPane
        );

        // Set scene
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("User Setup");
        primaryStage.show();
    }
}
