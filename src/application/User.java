package application;
/**
* The User class represents a user entity in the system.
* It contains the user's details such as userName, password, and role.
*/
public class User {
   private String userName;
   private String password;
   private String role;
   private String email;
  
   // Constructor to initialize a new User object with userName, password, role, and email.
   public User( String userName, String password, String role, String email) {
       this.userName = userName;
       this.password = password;
       this.role = role;
       this.email = email;
   }
  
   // Sets the role of the user.
   public void setRole(String role) {
   	this.role = role;
   }
   public String getUserName() { return userName; }
   public String getPassword() { return password; }
   public String getEmail() { return email; }
   public String getRole() { return role; }

	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}

