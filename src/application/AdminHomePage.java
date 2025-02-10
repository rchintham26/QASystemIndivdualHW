package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
	
	
	private final DatabaseHelper databaseHelper;
	
	public AdminHomePage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}
	
    public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox();
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the admin
	    Label adminLabel = new Label("Hello, Admin!");
	    
	    Button inviteButton = new Button("Invite User");
        inviteButton.setOnAction(a -> {
            new InvitationPage().show(databaseHelper, primaryStage, user);
        });
	    
	    Button editUsersButton = new Button("Edit Users");
	    
	    VBox buttonContainer = new VBox();
        buttonContainer.setAlignment(Pos.CENTER);
	    
        String[] roles = {"Student", "Reviewer", "Instructor", "Staff"};
        for (String role : roles) {
            Button roleButton = new Button(role + " View");
            roleButton.setOnAction(a -> showRolePage(primaryStage, role, user));
            buttonContainer.getChildren().add(roleButton);
        }
	    
	    Button logoutButton = new Button("Logout");
	    Button backButton = new Button("Back");
	    
	    adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    layout.getChildren().add(adminLabel);
	    Scene adminScene = new Scene(layout, 800, 400);
	    
	    editUsersButton.setOnAction(a -> {
	    	new AdminEditUsersPage(databaseHelper).show(primaryStage, user);
	    });
	    
	    logoutButton.setOnAction(a -> {
	    	new UserLoginPage(databaseHelper).show(primaryStage);
	    });
	    
	    backButton.setOnAction(a -> {
	    	new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
	    });
	    
	    layout.getChildren().addAll(inviteButton, editUsersButton, buttonContainer, logoutButton, backButton);
	    
	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
    
    //temporary page to show different role views -- will expand to classes for bigger functionalities later
    private void showRolePage(Stage primaryStage, String role, User user) {
        VBox roleLayout = new VBox(10);
        roleLayout.setAlignment(Pos.CENTER);
        roleLayout.setStyle("-fx-padding: 20;");

        Label roleLabel = new Label("Welcome to " + role + " view");
        roleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> show(primaryStage, user));

        roleLayout.getChildren().addAll(roleLabel, backButton);
        Scene roleScene = new Scene(roleLayout, 800, 400);
        primaryStage.setScene(roleScene);
    }
}