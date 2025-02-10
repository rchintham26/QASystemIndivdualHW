package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import databasePart1.*;

public class EditUserPage {
    private final DatabaseHelper databaseHelper;

    public EditUserPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
    	// Input fields for updating details
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter new userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter new Password");
        passwordField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter new Email");
        emailField.setMaxWidth(250);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Label errorLabel2 = new Label();
        errorLabel2.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Label errorLabel3 = new Label();
        errorLabel3.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Button updateButton = new Button("Update");
        Button backButton = new Button("Back");

        // Disable input fields initially
        userNameField.setDisable(true);
        passwordField.setDisable(true);
        emailField.setDisable(true);
        updateButton.setDisable(true);

        // Fetch current user details when the confirm button is clicked
        try {
            User existingUser = databaseHelper.getUserByUsername(user.getUserName());
            if (existingUser != null) {

                // Enable fields for editing
                userNameField.setText(existingUser.getUserName());
                userNameField.setDisable(false);
                passwordField.setDisable(false);
                emailField.setText(existingUser.getEmail());
                emailField.setDisable(false);
                updateButton.setDisable(false);
            } else {
                errorLabel.setText("User not found. Please enter a valid username.");
                
                // Disable fields if user is not found
                userNameField.setDisable(true);
                passwordField.setDisable(true);
                emailField.setDisable(true);
                updateButton.setDisable(true);
            }
        } catch (SQLException e) {
            errorLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }



        // Handle update button click
        updateButton.setOnAction(a -> {
            String newUsername = userNameField.getText().trim();
            String newPassword = passwordField.getText().trim();
            String newEmail = emailField.getText().trim();

            errorLabel.setText(""); // Clear previous errors

            try {	
            	EmailValidator emailValidator = new EmailValidator();
                emailValidator.isValidEmail(newEmail);
                
                // Validate Inputs
               	String usernameResult = UserNameRecognizer.checkForValidUserName(newUsername);
               	String passwordResult = PasswordEvaluator.evaluatePassword(newPassword);
               	String emailResult = emailValidator.getErrorMessage();
               	
               	if (usernameResult == "" && passwordResult == "" && emailResult == "") {
               		User updatedUser = new User(newUsername, newPassword, "user", newEmail);
                    boolean success = databaseHelper.updateUser(user.getUserName(), updatedUser);
                    
                    if (success) {      
                        new UserLoginPage(databaseHelper).show(primaryStage);
                        
                    } else {
                        errorLabel.setText("Update failed. User may not exist.");
                    }
               	}
               	
               	else {
               		if (!usernameResult.isEmpty()) {
               			errorLabel.setText(usernameResult);
           				errorLabel2.setText("");
           				errorLabel3.setText("");
               		}
               		
               		if (!passwordResult.isEmpty()) {
               			errorLabel.setText(passwordResult);
               			errorLabel2.setText(usernameResult);
               			errorLabel3.setText("");
               		}
               		
               		if (!emailResult.isEmpty()) {
               			errorLabel.setText(emailResult);
                   		errorLabel2.setText(passwordResult);
                   		errorLabel3.setText(usernameResult);
               		}
               	}

            } catch (SQLException e) {
                errorLabel.setText("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        backButton.setOnAction(e -> {
        	new UserHomePage(databaseHelper).show(primaryStage, user);
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(
                userNameField, passwordField, emailField, updateButton, errorLabel, errorLabel2, errorLabel3, backButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Edit User Account");
        primaryStage.show();
    }
}
