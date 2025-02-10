package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");
        emailField.setMaxWidth(250);
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Label errorLabel2 = new Label();
        errorLabel2.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Label errorLabel3 = new Label();
        errorLabel3.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");		
        		
        Button setupButton = new Button("Setup");
        
        Button backButton = new Button("Back");
        
        backButton.setOnAction(e -> {
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        
        
        setupButton.setOnAction(a -> {
       		errorLabel.setText("");
			errorLabel2.setText("");
			errorLabel3.setText("");
			
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            String code = inviteCodeField.getText();
            
            EmailValidator emailValidator = new EmailValidator();
            emailValidator.isValidEmail(email);
            
            // Validate UserName and Password
            String usernameResult = UserNameRecognizer.checkForValidUserName(userName);
            String passwordResult = PasswordEvaluator.evaluatePassword(password);
            String emailResult = emailValidator.getErrorMessage();
            
            try {   	
               	if (usernameResult == "" && passwordResult == "" && emailResult == "") {
    				// Check if the user already exists
    				if(!databaseHelper.doesUserExist(userName)) {	
    					// Validate the invitation code
    					if(databaseHelper.validateInvitationCode(code)) {
            			
	            			// Create a new user and register them in the database    						
    						String role = databaseHelper.getRoleFromInviteCode(code);    						
			            	User user=new User(userName, password, role, email);
			                databaseHelper.register(user);
			                
			                // Navigate to the Welcome Login Page
			                new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
    					}
    					else {
            			errorLabel.setText("Please enter a valid invitation code");
    					}
    				}
    				else {
    					errorLabel.setText("This userName is taken!!.. " +
            				            "Please use another to setup an account");
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
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField,emailField, inviteCodeField, setupButton, 
        		                    errorLabel, errorLabel2, errorLabel3, backButton);
        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}