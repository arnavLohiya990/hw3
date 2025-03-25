package application;


import java.util.List;

import databasePart1.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBase;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

@SuppressWarnings("unused")
public class InvitationPage {
	private Scene previousScene;
	
	public InvitationPage(Scene previousScene) {
		this.previousScene = previousScene;
	}

	/**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
    public void show(DatabaseHelper databaseHelper,Stage primaryStage, List<String> roles) {
    	VBox layout = new VBox(10);
    	layout.setAlignment(Pos.CENTER);
    	layout.setPadding(new Insets(10));
    	
	    // Label to display the title of the page
	    Label userLabel = new Label("Invitation Link");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    String invitationCode = databaseHelper.generateInvitationCode(roles);
	    
	    // TextField to display the link
        TextField linkField = new TextField(invitationCode);
        linkField.setEditable(false);
        linkField.setStyle("-fx-font-size: 14px;");
	    // Button to generate the invitation code
        Button copyButton = new Button("Copy Link");
        copyButton.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(invitationCode);
            clipboard.setContent(content);
        });

        // Back Button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (previousScene != null) {
                primaryStage.setScene(previousScene);
            }
        });

        // Add components to layout
        layout.getChildren().addAll(userLabel, linkField, copyButton, backButton);

        // Set the new scene
        Scene inviteScene = new Scene(layout, 400, 200);
        primaryStage.setScene(inviteScene);
        primaryStage.setTitle("Invite Page");
    }
}
