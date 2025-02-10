package application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import databasePart1.*;
/**
* The SetupAdmin class handles the setup process for creating an administrator account.
* This is intended to be used by the first user to initialize the system with admin credentials.
*/
public class AdminSetupPage {
	
   private final DatabaseHelper databaseHelper;
   public AdminSetupPage(DatabaseHelper databaseHelper) {
       this.databaseHelper = databaseHelper;
   }
   public void show(Stage primaryStage) {
   	// Input fields for userName and password
       TextField userNameField = new TextField();
       userNameField.setPromptText("Enter Admin userName");
       userNameField.setMaxWidth(250);
       
       PasswordField passwordField = new PasswordField();
       passwordField.setPromptText("Enter Password");
       passwordField.setMaxWidth(250);
      
       TextField emailField = new TextField();
       emailField.setPromptText("Enter Email");
       emailField.setMaxWidth(250);
      
       // Label to display error messages
       Label errorLabel = new Label();
       errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
      
       Label errorLabel2 = new Label();
       errorLabel2.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
      
       Label errorLabel3 = new Label();
       errorLabel3.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
       Button setupButton = new Button("Setup");
      
       setupButton.setOnAction(a -> {
       	// Retrieve user input
           String userName = userNameField.getText();
           String password = passwordField.getText();
           String email = emailField.getText();
           
           EmailValidator emailValidator = new EmailValidator();
           emailValidator.isValidEmail(email);
           
           try {
           	// Validate Inputs
           	String usernameResult = UserNameRecognizer.checkForValidUserName(userName);
           	String passwordResult = PasswordEvaluator.evaluatePassword(password);
           	String emailResult = emailValidator.getErrorMessage();
           	
           	if (usernameResult == "" && passwordResult == "" && emailResult == "") {	
       			// Create a new User object with admin role and register in the database
               	User user=new User(userName, password, "admin", email);
               	databaseHelper.register(user);
               	System.out.println("Administrator setup completed.");
                  
               	// Navigate to the Welcome Login Page
               	new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
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
       VBox layout = new VBox(10, userNameField, passwordField, emailField, setupButton,
       					   errorLabel, errorLabel2, errorLabel3);
       layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
       primaryStage.setScene(new Scene(layout, 800, 400));
       primaryStage.setTitle("Administrator Setup");
       primaryStage.show();
   }
}

