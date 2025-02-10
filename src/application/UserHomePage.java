package application;
import databasePart1.DatabaseHelper;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserHomePage {
    private final DatabaseHelper databaseHelper;
    
    public UserHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER); // Ensures proper alignment
        layout.setStyle("-fx-padding: 20;");

        Label userLabel = new Label("Hello, " + user.getUserName());
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button continuePage = new Button("Edit Account");
        continuePage.setOnAction(e -> new EditUserPage(databaseHelper).show(primaryStage, user));
        

        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        // user.setRole("student, reviewer, instructor, staff");
        
        String[] roles = extractRoles(user.getRole());
        
        if (roles.length == 1) {
            // if the user has only one role, display the role label on the main page
            Label roleLabel = new Label("Welcome to " + roles[0].substring(0, 1).toUpperCase() + roles[0].substring(1).toLowerCase() + " view");
            roleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            layout.getChildren().add(roleLabel);
        } else {
            // if the user has multiple roles, display buttons for each role
            for (String role : roles) {
                System.out.println(role);
                Button roleButton = new Button(role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase() + " View");
                final String currentRole = role.trim();
                roleButton.setOnAction(e -> showRolePage(primaryStage, currentRole, user));
                buttonContainer.getChildren().add(roleButton);
            }
        }
        
        Button logoutButton = new Button("Log out");
        logoutButton.setOnAction(e -> new UserLoginPage(databaseHelper).show(primaryStage));

        layout.getChildren().addAll(userLabel, continuePage, buttonContainer, logoutButton);

        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Welcome Page");
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
    
// function to extract roles from String to an array.
    private String[] extractRoles(String roles) {
        if (roles == null || roles.isEmpty()) {
            return new String[]{"Default"};
        }
        return roles.contains(",") ? 
            roles.split("\\s*,\\s*") : 
            new String[]{roles.trim()};
    }
}
	