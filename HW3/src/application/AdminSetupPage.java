package application;

import application.EmailValidator;
import application.FullNameValidator;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

import java.util.List;
import java.util.ArrayList;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
@SuppressWarnings("unused")
public class AdminSetupPage {
    // R.McQuesten, 2025-02-21, Set const so we can change this once and it'll cascade evenly across the platform	
    private static final String TITLE_STYLE = "-fx-font-size: 24px; -fx-font-weight: bold;";
    private static final String INSTR_STYLE = "-fx-font-size: 16px; -fx-padding: 5 0 10 0;";
    private static final String GEN_STYLE = "-fx-font-size: 14px; -fx-font-weight: bold;";	
    private static final String ERROR_STYLE = "-fx-text-fill: red; -fx-font-size: 12px;";
    private static final double BTN_WIDTH = 250;              // R.McQuesten, 2025-02-21, Set btn width for consistency
    private static final double INPUT_WIDTH = 250;            // R.McQuesten, 2025-02-21, Set input field width so can change later easily 	
    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// R.McQuesten, 2025-02-03, New Users user story accnt info new field
    	
    	// Input fields
        TextField userNameField = new TextField();		// Input field for username
        TextField userFullNameField = new TextField(); 	// Input field for user's name aka userFullName
        TextField userEmailField = new TextField(); 	// Input field for user's email
        PasswordField passwordField = new PasswordField(); // Input field for user's password
        
        // Set prompt text of input fields
        userNameField.setPromptText("Enter Admin Username");
        userFullNameField.setPromptText("Enter Admin Full Name");
        userEmailField.setPromptText("Enter Admin E-mail");
        passwordField.setPromptText("Enter Password");       
        
        // Set max width of input fields
        userNameField.setMaxWidth(INPUT_WIDTH);
        userFullNameField.setMaxWidth(INPUT_WIDTH);
        userEmailField.setMaxWidth(INPUT_WIDTH);
        passwordField.setMaxWidth(INPUT_WIDTH);
       
        // Labels
        Label titleLabel = new Label("*** MASTER ACCOUNT SETUP ***");   	// Label for title
        Label hiLbl = new Label("Enter your information below:");			// Label for subtitle
        Label userNameLbl = new Label("Username:");							// Label for the username field
        Label fullNameLbl = new Label("Full Name:");						// Label for the full name field
        Label emailLbl = new Label("E-Mail:");     							// Label for the email field
        Label passwordLbl = new Label("Password:");   					 	// Label for the password field
        Label setupLbl = new Label("Setup:");								// Label for the setup account        
        Label errorLabel = new Label(); 									// Label to show error messages
        
        // Set label style
        titleLabel.setStyle(TITLE_STYLE);
        hiLbl.setStyle(INSTR_STYLE);
        userNameLbl.setStyle(GEN_STYLE);
        fullNameLbl.setStyle(GEN_STYLE);
        emailLbl.setStyle(GEN_STYLE);
        passwordLbl.setStyle(GEN_STYLE);
        setupLbl.setStyle(GEN_STYLE);
        errorLabel.setStyle(ERROR_STYLE);          

        // Buttons
        Button setupButton = new Button("Setup");
        
        // Set button widths
        setupButton.setPrefWidth(BTN_WIDTH);
        
        // Setup button action handler
        setupButton.setOnAction(a -> {
		// Retrieve user input
		String userName = userNameField.getText();
	
		// Retrieve new account information user input
		// R.McQuesten, 2025-02-04, Add functionality for new account info fields
		String userFullName = userFullNameField.getText();
		String userEmail = userEmailField.getText();
		String password = passwordField.getText();
		try {	
			// Validate user name
			// R.McQuesten, 2025-02-03, Add validation for username
			String userNameVali = UserNameRecognizer.checkForValidUserName(userName);
			if (!userNameVali.isEmpty()) {
				String errorUserName = "*** ERROR *** Invalid Username\n"+ userNameVali;
				errorLabel.setText(errorUserName);
				return;
			}
	
			// Validate password
			// R.McQuesten, 2025-02-03, Add validation for password
			String userPassVali = PasswordEvaluator.checkForPassword(password);
			if (!userPassVali.isEmpty()) {
				String userPassErrorString =  "*** ERROR *** Invalid Password\n "+userPassVali;
				errorLabel.setText(userPassErrorString);
				return;
			}
			
			// Validate email
			// R.McQuesten, 2025-02-05, Add validation for email
			if (!EmailValidator.validateEmailAddr(userEmail)) {
				errorLabel.setText("*** ERROR *** Invalid Email. Please enter email in form of user@domain.edu");
				return;
			}
			
			// Validate full name
			// R.McQuesten, 2025-02-05, Add validation for full name
			if (!FullNameValidator.validateFullName(userFullName)) {
				errorLabel.setText("*** ERROR *** Invalid Full Name. Please enter full name in form of First Last");
				return;
			}			
			
			// Create a new User object with admin role and register in the database
			// R.McQuesten, 2025-02-04, Add functionality for new account info fields
			// R.McQuesten, 2025-02-05, Whoops we should have this process down here too
			User user = new User(userName, password, "Admin", userFullName, userEmail);
			System.out.println("*** SUCCESS *** Administrator setup completed.");			
			databaseHelper.register(user);//Arnav Lohiya Feb 5th: User Must be created only after all validations are successful.
			
			// R.McQuesten, 2025-02-03, Redirect to User Login Page after register user
			new UserLoginPage(databaseHelper).show(primaryStage);
	
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error: " + e.getMessage());
			e.printStackTrace();
		}
	});
    	// R.McQuesten, 2025-02-03, Add account information fields
        // R.McQuesten, 2025-02-21, Add grid pane approach to consolidate lbls
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
        gridPane.add(setupLbl, 0, 4);
        gridPane.add(setupButton, 1, 4);
        gridPane.add(errorLabel, 0, 5, 2, 1);

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
        
        // Set style and scene
        primaryStage.setScene(new Scene(mainLayout, 800, 400));
        primaryStage.setTitle("Master Admin Setup");
        primaryStage.show();
    }
}
