package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import databasePart1.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;


    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;

    }
    public void show( Stage primaryStage, User user) {
    	
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Label welcomeLabel = new Label("Welcome!!");
	    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // check user roles for admin permissions
	    Button continueButton = new Button("Continue to your Page");
	    continueButton.setOnAction(a -> {
	    	String[] roles = extractRoles(user.getRole());
	    	
	    	boolean isAdmin = false;
            for (String role : roles) {
                if (role.trim().equalsIgnoreCase("admin")) {
                    isAdmin = true;
                    break;
                }
            }

            if (isAdmin) {
                new AdminHomePage(databaseHelper).show(primaryStage, user);
            } else {
                new UserHomePage(databaseHelper).show(primaryStage, user);
            }
	    });
	    
	    // quit applicaiton button
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit();
	    });
	    

	    layout.getChildren().addAll(welcomeLabel);
	    
	    layout.getChildren().addAll(continueButton,quitButton);
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }
    
    private String[] extractRoles(String roles) {
        if (roles == null || roles.isEmpty()) {
            return new String[]{"Default"};
        }
        return roles.contains(",") ? 
            roles.split("\\s*,\\s*") : 
            new String[]{roles.trim()};
    }
}