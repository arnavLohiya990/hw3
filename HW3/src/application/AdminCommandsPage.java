package application;

import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
//Anthony h. feb 6, added for check boxes
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import databasePart1.*;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
//feb 6, added textfield for textboc input
import javafx.scene.control.TextField;
import javafx.collections.*;
import java.util.ArrayList;
import java.util.List;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

@SuppressWarnings("unused")
public class AdminCommandsPage {	
//	private DatabaseHelper newDBhelper = new DatabaseHelper(); 
	String changeThisUser;
	ArrayList<String> chosenUserRoles = new ArrayList<>(); 
	String userNameNotFoundError = "This username does not exist in the database. Try again";
	
	private final DatabaseHelper databaseHelper;	
	
	private User user;
    
	public AdminCommandsPage(User user, DatabaseHelper databaseHelper) {
			this.user = user;
	        this.databaseHelper = databaseHelper;
	}	
	
	/**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
    public void show(DatabaseHelper databaseHelper,Stage primaryStage) {
	//added a new dbhelper for a related error in popup window
//	    newDBhelper = databaseHelper; 
	    VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

	    //Back button 
	    Button backButton = new Button("Go back");

	    //positioning for back button
	    HBox newhbox = new HBox(); 
	    newhbox.setAlignment(Pos.TOP_LEFT);
	    newhbox.getChildren().add(backButton);

	    //Modify User button 
	    Button modifyUserButton = new Button("Modify User Roles");

	    //positioning for modify user button 
	    HBox secHBox = new HBox();
	    secHBox.setAlignment(Pos.TOP_RIGHT);
	    secHBox.getChildren().addAll(modifyUserButton);
	    
	    // Label to display the title of the page - changed to Admin Command Page 
	    Label userLabel = new Label("Admin Command Page");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    //Anthony h. feb 6 changed to use new function by me to display a user and ALL their information, minus password
	    ListView<String> allUserInfoList = new ListView<String>();
	    ObservableList<String> allUserInfoListed = FXCollections.observableArrayList();
	    List<String> completeUserInfo = DatabaseHelper.fetchAllUsersAndInformation();
	    
	    allUserInfoListed.addAll(completeUserInfo);
	    allUserInfoList.setItems(allUserInfoListed);
	    
	    //feb 6, Anthony H added this line to keep track of which user the Admin is currently selecting to edit 
	    String selectedUser = " ";
	    	    
	    //Anthony h. feb 6. replaced with updated modifyuser action
	    //modifyUserButton action
	    //will create a pop up window when a user is selected from the list
	    //pop up window will display username and well as a check list of the user's role
	    modifyUserButton.setOnAction(e ->  {
	    showPopUpWindow();
	    System.out.println("Modfiy User Roles Button Worked!");
	    });
	   
	    //backButton action 
	    backButton.setOnAction(a -> {
	    	new AdminHomePage(user, databaseHelper).show(primaryStage);
	    });
        
        layout.getChildren().addAll(newhbox,secHBox, userLabel, allUserInfoList);
	    Scene inviteScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(inviteScene);
	    primaryStage.setTitle("Admin Command Page");
    	
    }
    
    @SuppressWarnings("static-access")
	private void showPopUpWindow() {
    	Stage popUpWindow1 = new Stage(); 
    	popUpWindow1.initModality(Modality.APPLICATION_MODAL); //needed so this window wont mess with main window of the user 
    	popUpWindow1.setTitle("User and their Roles:\n");
    		
    	//Done button, to send the admin's choice in an array of roles to the other function 
    	//button action will also get ride of popup 
    	Button closeOut = new Button("Done");
    	
    	//Anthony H. feb5, changed vbox size and scene size . Created Hbox for nicer UI 
    	VBox popUpLayout = new VBox(5, closeOut);
    	   	
    	//Anthony H. feb 5th, added check boxes for user
    	//creating check boxes for each role
    	//import javafx.scene.control.CheckBox;
    	//each button is added to the vbox
    	CheckBox adminCB = new CheckBox("Admin");
    	CheckBox instructorCB = new CheckBox("Instructor");
    	CheckBox staffCB = new CheckBox("Staff");
    	CheckBox studentCB = new CheckBox("Student");
    	CheckBox reviewerCB = new CheckBox("Reviewer");
    	
    	//this will determine if each checkbox is checked or unchecked
    	//these results will be sent to the updateUserRolesIntoTables function
    	Button checkBoxesChosen = new Button("Select These Roles for User");
    	
    	//Button to actually take username input
    	Button submitUserName = new Button("Select This User");
    	
    	//adding a text box that the admin can enter a username into 
    	TextField userNameInput = new TextField(); 
    	userNameInput.setPromptText("Enter UserName");
    	
		//roles chosen by the admin are only added to the arraylist once this button is pressed
    	checkBoxesChosen.setOnAction(a ->{
    		//each role should start with a capital letter 
    		if(adminCB.isSelected()) {
    			chosenUserRoles.add("Admin");
    		}
    		
    		if(instructorCB.isSelected()) {
    			chosenUserRoles.add("Instructor");
    		}
    		
    		if(staffCB.isSelected()) {
    			chosenUserRoles.add("Staff");
    		}
    		
    		if(studentCB.isSelected()) {
    			chosenUserRoles.add("Student");
    		}
    		
    		if(reviewerCB.isSelected()) {
    			chosenUserRoles.add("Reviewer");
    		}	
    	});
    	
    	submitUserName.setOnAction(a -> {
    		//must turn the text field into a string to use .isEmpty()
    		//changeThisUser =userNameInput.getText().trim();
    		changeThisUser = userNameInput.getText();
    		
    		if(!changeThisUser.isEmpty() ) {
    			Label tempLabel = new Label(changeThisUser);
    			
    			//commented out this ine since it prints out the user in the pop up window 
    			//popUpLayout.getChildren().add(new Label(changeThisUser));
    			userNameInput.clear(); 
    			
    		}
    	});

    	//Anthony H. feb 5th changed code so once the close Done button is pressed, the arraylist of users + username is passed to updateRolesIntoTable function
    	//once you press done, a test message is outputted to the console 
    	//close out button = Done button 
    	closeOut.setOnAction(a -> {
    		System.out.println("This User's: " + changeThisUser + " new Roles are now: " + chosenUserRoles);
    		
    		//this is used to make sure that atleast 1 user in the database is an admin.
    		//if chosenUserRoles contains admin, this if statement needs to run to make sure the only admin isnt deleted
    		
    		if (databaseHelper.atleastOneAdmin("Admin")) {
    			System.out.println("admin exists");
    			databaseHelper.updateRolesIntoTable(chosenUserRoles, changeThisUser);
    		} else {
    			System.out.println("no admin exists");
    		}		
    		
    		if( databaseHelper.fetchUserRoles(changeThisUser) == chosenUserRoles ) {
    			System.out.println("The user " + changeThisUser + " had their roles correctly updated");
    			popUpWindow1.close();
    			
    			//must emtpy out the arraylist everytime this is successful 
    			chosenUserRoles.clear();
    		}else {
    			popUpWindow1.close();
    			chosenUserRoles.clear();
    			//System.out.println("\nThere has been an error when updating user's roles");
    		}
    		
    	});
    	
    	HBox closeOutButtonLayout = new HBox();
    	closeOutButtonLayout.setAlignment(Pos.TOP_LEFT);
    	closeOutButtonLayout.getChildren().addAll(closeOut);
    	
    	HBox selectedUserRoles = new HBox();
    	selectedUserRoles.setAlignment(Pos.BOTTOM_CENTER);
    	selectedUserRoles.getChildren().addAll(checkBoxesChosen);
    	 
    	popUpLayout.getChildren().addAll(adminCB, instructorCB, staffCB, studentCB, reviewerCB, selectedUserRoles, userNameInput, submitUserName, closeOutButtonLayout);
    	Scene popUpScene = new Scene(popUpLayout, 400, 300);
    	popUpWindow1.setScene(popUpScene);
    	popUpWindow1.showAndWait();  	
    }
}
