package application;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import databasePart1.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */
public class InvitationPage {
    private String role;
    private DatePicker datePicker;
    private TextField timeField;
    private List<CheckBox> roleCheckBoxes = new ArrayList<>();

    /**
     * Displays the Invite Page in the provided primary stage.
     *
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(DatabaseHelper databaseHelper, Stage primaryStage, User user) {
        VBox layout = new VBox(15); // Add spacing between elements
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // Label to display the title of the page
        Label userLabel = new Label("Generate Invitation");
        userLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Role selection
        Label roleLabel = new Label("Select Role:");
        
        VBox roleSelectionBox = new VBox(5);
        roleSelectionBox.setAlignment(Pos.CENTER);
        
        String[] roles = {"Student", "Reviewer", "Instructor", "Staff"};
        for (String r : roles) {
            CheckBox checkBox = new CheckBox(r);
            roleCheckBoxes.add(checkBox);
            roleSelectionBox.getChildren().add(checkBox);
        }

//        ComboBox<String> selectCodeRole = new ComboBox<>();
//        selectCodeRole.getItems().setAll("Student", "Reviewer", "Instructor", "Staff");
//        selectCodeRole.setValue("Student");
//        selectCodeRole.setMinWidth(200);

        // Date and Time selection
        Label expirationLabel = new Label("Set Expiration:");
        
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setMinWidth(150);
        
        timeField = new TextField(LocalTime.now().withSecond(0).toString().split("\\.")[0]);
        timeField.setPromptText("HH:mm:ss");
        timeField.setMaxWidth(100);
        
        // Create HBox for date and time
        HBox dateTimeBox = new HBox(10);
        dateTimeBox.setAlignment(Pos.CENTER);
        dateTimeBox.getChildren().addAll(datePicker, timeField);

        // Button to generate the invitation code
        Button showCodeButton = new Button("Generate Invitation Code");
        showCodeButton.setStyle("-fx-font-size: 14px;");
        
        // Label to display the generated invitation code
        Label inviteCodeLabel = new Label("");
        inviteCodeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Navigation buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button backButton = new Button("Back");
        Button logoutButton = new Button("Logout");
        buttonBox.getChildren().addAll(backButton, logoutButton);

//        // Event handlers
//        selectCodeRole.setOnAction(event -> {
//            role = selectCodeRole.getValue().toLowerCase();
//        });

        showCodeButton.setOnAction(a -> {
            try {
            	// get selected roles from checkboxes
            	List<String> selectedRoles = new ArrayList<>();
                for (CheckBox checkBox : roleCheckBoxes) {
                    if (checkBox.isSelected()) {
                        selectedRoles.add(checkBox.getText().toLowerCase());
                    }
                }
                role = String.join(",", selectedRoles);

                if (role.isEmpty()) {
                    inviteCodeLabel.setText("Please select at least one role");
                    inviteCodeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: red;");
                    return;
                }
                
                System.out.println(role);
            	
                LocalDateTime dateTime = LocalDateTime.of(
                    datePicker.getValue(),
                    LocalTime.parse(timeField.getText() + ".00")
                );
                Timestamp timestamp = Timestamp.valueOf(dateTime);
                String invitationCode = databaseHelper.generateInvitationCode(role, timestamp);
                inviteCodeLabel.setText("Invitation Code: " + invitationCode);
                inviteCodeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: green;");
            } catch (Exception e) {
                inviteCodeLabel.setText("Please enter a valid date and time");
                inviteCodeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: red;");
            }
        });

        logoutButton.setOnAction(a -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

        backButton.setOnAction(e -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
        });

        // Add all elements to layout
        layout.getChildren().addAll(
            userLabel,
            roleLabel,
            roleSelectionBox,
            expirationLabel,
            dateTimeBox,
            showCodeButton,
            inviteCodeLabel,
            buttonBox
        );

        Scene inviteScene = new Scene(layout, 800, 500);
        primaryStage.setScene(inviteScene);
        primaryStage.setTitle("Generate Invitation");
    }
}