package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their
 * accounts. It validates the user's credentials and navigates to the
 * appropriate page upon successful login.
 */
public class UserLoginPage {

	private final DatabaseHelper databaseHelper;

	public UserLoginPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage) {
		// Input field for the user's userName, password
		TextField userNameField = new TextField();
		userNameField.setPromptText("Enter userName");
		userNameField.setMaxWidth(250);

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Enter Password");
		passwordField.setMaxWidth(250);

		// Label to display error messages
		Label errorLabel1 = new Label();
		errorLabel1.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

		Label errorLabel2 = new Label();
		errorLabel2.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

		Button loginButton = new Button("Login");
		Button backButton = new Button("Back");

		backButton.setOnAction(e -> {
			new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
		});

		loginButton.setOnAction(a -> {
			// Retrieve user inputs
			String userName = userNameField.getText();
			String password = passwordField.getText();

			try {
				// Extensive validation for subsequent login not needed.
				// Check valid username length
				if (userName.length() >= 4 && userName.length() <= 16) {
					// check valid password length
					if (password.length() >= 8) {
						User user = new User(userName, password, "", "");
						WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);

						// Retrieve the user's role from the database using userName
						String role = databaseHelper.getUserRole(userName);

						if (role != null) {
							user.setRole(role);

							if (databaseHelper.login(user)) {
								String email = databaseHelper.getUserEmail(userName);
								user.setEmail(email);
								
								// Check for password reset flag
								if (databaseHelper.getPasswordResetFlag(user.getUserName())) {


									ResetPasswordPage resetPasswordPage = new ResetPasswordPage(databaseHelper);
									resetPasswordPage.show(primaryStage, user);
									
								} else {
									welcomeLoginPage.show(primaryStage, user);
								}
							} else {
								// Display an error if the login fails
								errorLabel1.setText("Error logging in");
								errorLabel2.setText("");
							}
						} else {
							// Display an error if the account does not exist
							errorLabel1.setText("user account doesn't exists");
							errorLabel2.setText("");
						}
					} else {
						errorLabel1.setText("Password is too short.");
					}
				} else {
					errorLabel1.setText("Invalid Username length, must be between 4 and 16 characters");
				}

			} catch (SQLException e) {
				System.err.println("Database error: " + e.getMessage());
				e.printStackTrace();
			}
		});

		VBox layout = new VBox(10);
		layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
		layout.getChildren().addAll(userNameField, passwordField, loginButton, backButton, errorLabel1, errorLabel2);

		primaryStage.setScene(new Scene(layout, 800, 400));
		primaryStage.setTitle("User Login");
		primaryStage.show();
	}
}
