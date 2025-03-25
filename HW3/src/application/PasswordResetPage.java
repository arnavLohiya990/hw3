// R.McQuesten, 2025-02-05 
// Password Reset Page
// Using OTP approach

package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import databasePart1.*;

public class PasswordResetPage {
    private final DatabaseHelper databaseHelper;
    private final String userName;

    public PasswordResetPage(DatabaseHelper databaseHelper, String userName) {
        this.databaseHelper = databaseHelper;
        this.userName = userName;
    }

    public void show(Stage primaryStage) {
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter New Password");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button resetButton = new Button("Reset Password");
        resetButton.setOnAction(a -> {
            String newPassword = newPasswordField.getText();
            String passwordError = PasswordEvaluator.checkForPassword(newPassword);

            if (!passwordError.isEmpty()) {
                errorLabel.setText(passwordError);
                return;
            }

            try {
                String query = "UPDATE cse360users SET password = ? WHERE userName = ?";
                try (PreparedStatement pstmt = databaseHelper.getConnection().prepareStatement(query)) {
                    pstmt.setString(1, newPassword);
                    pstmt.setString(2, userName);
                    pstmt.executeUpdate();
                }

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Password Reset Successful");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Your password has been updated. Please log in again.");
                successAlert.showAndWait();

                new UserLoginPage(databaseHelper).show(primaryStage);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10, newPasswordField, resetButton, errorLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 400, 200));
        primaryStage.setTitle("Reset Password");
        primaryStage.show();
    }
}
