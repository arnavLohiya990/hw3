package application;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import databasePart1.DatabaseHelper;

//Page used to select roles that will be assigned to the invitation code

@SuppressWarnings("unused")
public class RoleInvitationSelectorPage{
	private Scene previousScene;
    private final DatabaseHelper databaseHelper;
    private final Stage primaryStage;
    private final ObservableList<String> selectedRoles = FXCollections.observableArrayList();
	
	public RoleInvitationSelectorPage(Scene previousScene, DatabaseHelper databaseHelper, Stage primaryStage) {
		this.previousScene = previousScene;
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
	}

	public void show() {
		 VBox layout = new VBox(10);
	     layout.setAlignment(Pos.CENTER);
	     
	     Label titleLabel = new Label("Select Roles for Invite");
	        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	        // Role Selection
	        Label roleLabel = new Label("Select up to 3 roles:");
	        ObservableList<String> roles = FXCollections.observableArrayList("Admin", "Instructor", "Reviewer", "Student", "Staff");
	        ListView<String> roleListView = new ListView<>(roles);
	        roleListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	        roleListView.setMaxHeight(100);

	        // Display Selected Roles
	        Label selectedRolesLabel = new Label("Assigned Roles:");
	        ListView<String> selectedRolesView = new ListView<>(selectedRoles);
	        selectedRolesView.setMaxHeight(100);
	        
	        // Role Selection Handler
	        roleListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
	            if (newValue != null && selectedRoles.size() < 3) {
	                selectedRoles.add(newValue);
	            } else {
	                displayAlert("Error", "You can select a maximum of 3 roles.");
	            }
	        });
	        
	        // Remove Role Button
	        Button removeRoleButton = new Button("Remove Selected Role");
	        removeRoleButton.setOnAction(e -> {
	            String selectedRole = selectedRolesView.getSelectionModel().getSelectedItem();
	            if (selectedRole != null) {
	                selectedRoles.remove(selectedRole);
	            }
	        });
	        
	        
		    // Navigation Buttons
		    // Back Button
	        Button backButton = new Button("Back");
	        backButton.setOnAction(e -> {
	            if (previousScene != null) {
	                primaryStage.setScene(previousScene);
	            }
	        });

	        Button generateInviteButton = new Button("Generate Invite");
	        generateInviteButton.setOnAction(e -> {
	        Scene currentScene = primaryStage.getScene();
	        new InvitationPage(currentScene).show(databaseHelper, primaryStage, selectedRoles);
	        }); // Move to Invite Page
	        
	        // Add some spacing with padding
	        layout.setPadding(new Insets(20, 50, 20, 50)); // Adds space around edges
	        
	        // Layout
	        layout.getChildren().addAll(
	                titleLabel, roleLabel, roleListView, selectedRolesLabel, selectedRolesView,
	                removeRoleButton, generateInviteButton, backButton
	        );
	        
	        Scene scene = new Scene(layout, 600, 500);
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("Role Selector");
	        primaryStage.show();
	    }
	
			private void displayAlert(String title, String message) {
				Platform.runLater(() -> {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle(title);
					alert.setHeaderText(null);
					alert.setContentText(message);
					alert.showAndWait();
				});
			}
	}
