package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class AdminEditUsersPage {
    private final DatabaseHelper databaseHelper;
    private final TableView<User> userTable = new TableView<>();

    public AdminEditUsersPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<User, String> passwordColumn = new TableColumn<>("Password");
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<User, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button resetPasswordButton = new Button("Reset Password");

            {
                editButton.setOnAction(event -> {
                    User selectedUser = getTableView().getItems().get(getIndex());
                    Dialog<User> dialog = new Dialog<>();
                    dialog.setTitle("Edit User");
                    dialog.setHeaderText("Edit User Details");

                    ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                    TextField usernameField = new TextField(selectedUser.getUserName());
                    TextField passwordField = new TextField(selectedUser.getPassword());
                    TextField emailField = new TextField(selectedUser.getEmail());

            	    // Error fields
            	    Label errorLabel = new Label();
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                    Label errorLabel2 = new Label();
                    errorLabel2.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");       
                    Label errorLabel3 = new Label();
                    errorLabel3.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

            	    // Role selection
            	    ComboBox<String> roleComboBox = new ComboBox<>();
            	    roleComboBox.getItems().addAll("Admin", "User","Reviewer","Instructor","Student"); // TODO: Add roles here functionally, only placeholder exists now
     
                     Label roleLabel = new Label("Select Role:");
                    VBox roleSelectionBox = new VBox(5);
                    roleSelectionBox.setAlignment(Pos.CENTER);
                    List<CheckBox> roleCheckBoxes = new ArrayList<>();
                    
                    String[] roles = {"Admin", "User", "Student", "Reviewer", "Instructor", "Staff"};
                    for (String r : roles) {
                        CheckBox checkBox = new CheckBox(r);
                        if (selectedUser.getRole().contains(r.toLowerCase())) {
                            checkBox.setSelected(true);
                        }
                        roleCheckBoxes.add(checkBox);
                        roleSelectionBox.getChildren().add(checkBox);
                    }

            	    // Layout for input fields
            	    VBox layout = new VBox(10);
            	    layout.getChildren().addAll(
            	        new Label("Username:"), usernameField,
            	        new Label("Password:"), passwordField,
            	        new Label("Email:"), emailField,
                        roleLabel, roleSelectionBox,
            	        errorLabel , errorLabel2, errorLabel3
            	        
            	    );
            	    dialog.getDialogPane().setContent(layout);

            	    //Validate before saving
            	    Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
            	    saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, e -> {
		               	// Validate Inputs
            	    	String username = usernameField.getText();
            	        String password = passwordField.getText();
            	        String email = emailField.getText();
            	    	
            	        EmailValidator emailValidator = new EmailValidator();
            	        String emailResult = emailValidator.isValidEmail(email) ? "" : emailValidator.getErrorMessage();
            	        String usernameResult = UserNameRecognizer.checkForValidUserName(username);
            	        String passwordResult = PasswordEvaluator.evaluatePassword(password);

		               	
		               	if (usernameResult == "" && passwordResult == "" && emailResult == "") {	
		               		//Passed All Tests
		               	}
		               	
		               	else {
		               		e.consume();	//Dialog box doesn't close
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
            	    });
            	    
            	    dialog.setResultConverter(dialogButton -> {
            	        if (dialogButton == saveButtonType) {
            	            List<String> selectedRoles = new ArrayList<>();
            	            for (CheckBox checkBox : roleCheckBoxes) {
            	                if (checkBox.isSelected()) {
            	                    selectedRoles.add(checkBox.getText().toLowerCase());
            	                }
            	            }
            	            String role = String.join(",", selectedRoles);
            	            return new User(usernameField.getText(), passwordField.getText(), role, emailField.getText());
            	        }
            	        return null;
            	    });
            	    
            	    dialog.showAndWait().ifPresent(updatedUser -> {
            	        try {
            	            databaseHelper.updateUser(selectedUser.getUserName(), updatedUser);
            	            loadAllUsers(); 
            	        } catch (Exception e) {
            	            e.printStackTrace();
            	        }
            	    });
            	});

             
                deleteButton.setOnAction(event -> {
                    User selectedUser = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this user?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            databaseHelper.deleteUser(selectedUser.getUserName());
                            loadAllUsers();
                        }
                    });
                });
                
                resetPasswordButton.setOnAction(event -> {
                    User selectedUser = getTableView().getItems().get(getIndex());
                    
                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("Reset Password");
                    dialog.setHeaderText("Generate and Set New Password");

                    ButtonType generateButtonType = new ButtonType("Set New Password", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().addAll(generateButtonType, ButtonType.CANCEL);

                    Label newPasswordLabel = new Label("New Password:");
                    TextField newPasswordField = new TextField();
                    newPasswordField.setEditable(false); // Read-only field

                    Button generateButton = new Button("Generate");
                    generateButton.setOnAction(e -> {
                        String newPassword = databaseHelper.generatePassword();
                        newPasswordField.setText(newPassword);
                    });

                    VBox resetLayout = new VBox(10);
                    resetLayout.getChildren().addAll(newPasswordLabel, newPasswordField, generateButton);
                    dialog.getDialogPane().setContent(resetLayout);
                    
                    
                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == generateButtonType && !newPasswordField.getText().isEmpty()) {
                            return newPasswordField.getText();
                        }
                        return null;
                    });

                    dialog.showAndWait().ifPresent(newPassword -> {
                        try {
                            selectedUser.setPassword(newPassword);
                            databaseHelper.updateUser(selectedUser.getUserName(), selectedUser);
                            databaseHelper.setPasswordReset(selectedUser.getUserName(), true);
                            
                            System.out.println("Set password:" + newPassword);
                        	loadAllUsers();
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Password successfully reset!");
                            successAlert.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton, resetPasswordButton);
                    setGraphic(buttons);
                }
            }
        });

        userTable.getColumns().addAll(usernameColumn, passwordColumn, emailColumn, roleColumn, actionColumn);
        loadAllUsers();

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new AdminHomePage(databaseHelper).show(primaryStage, user));

        layout.getChildren().addAll(userTable, backButton);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Edit Users");
    }

    private void loadAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList(databaseHelper.getAllUsers());
        userTable.setItems(users);
    }
}
