package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

public class ResetPasswordPage {
    private final DatabaseHelper databaseHelper;

    public ResetPasswordPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
        Label titleLabel = new Label("Change Password");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");
        newPasswordField.setMaxWidth(250);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button saveButton = new Button("Save");

        saveButton.setOnAction(a -> {
            String newPassword = newPasswordField.getText().trim();
            errorLabel.setText("");

            String passwordResult = PasswordEvaluator.evaluatePassword(newPassword);
            if (passwordResult.isEmpty()) {
                try {
                	// update user password
                    user.setPassword(newPassword);
                    boolean success = databaseHelper.updateUser(user.getUserName(), user);

                    if (success) {
                    	// reset flag and logout user
                    	databaseHelper.setPasswordReset(user.getUserName(), false);
                        new UserLoginPage(databaseHelper).show(primaryStage);
                    } else {
                        errorLabel.setText("Password update failed. Please try again.");
                    }
                } catch (Exception e) {
                    errorLabel.setText("Database error: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                errorLabel.setText(passwordResult);
            }
        });

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(titleLabel, newPasswordField, saveButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Change Password");
        primaryStage.show();
    }
}
