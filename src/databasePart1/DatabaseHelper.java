package databasePart1;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import application.User;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt
	
	// Password generation
	private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final char SPECIAL_CHARACTER = '!';
    private static final int PASSWORD_LENGTH = 8;  

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "email VARCHAR(255), "
				+ "role VARCHAR(255), "
				+ "passwordReset BOOLEAN DEFAULT FALSE)";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	    		+ "role VARCHAR(255), "
	            + "expiresAt DateTime, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	}

	// Import test users to the database
	public void initializeTestUsers() throws SQLException {
		try (InputStream inputStream = getClass().getResourceAsStream("test-users.txt")) {
		    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		    String line;
		    while ((line = reader.readLine()) != null) {
		        String[] parts = line.split(",");
		        if (parts.length == 4) {
		            String userName = parts[0].trim();
		            String password = parts[1].trim();
		            String role = parts[2].trim();
		            String email = parts[3].trim();

		            User user = new User(userName, password, role, email);
		            register(user);
		        }
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, email, role) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getEmail());
			pstmt.setString(4, user.getRole());
			pstmt.executeUpdate();
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	// Set the passwordReset flag for a user
	public boolean setPasswordReset(String username, boolean resetFlag) {
	    String query = "UPDATE cse360users SET passwordReset = ? WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setBoolean(1, resetFlag);
	        pstmt.setString(2, username);

	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0; // successful set
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Retrieves the passwordReset flag for a given user
	public boolean getPasswordResetFlag(String username) {
	    String query = "SELECT passwordReset FROM cse360users WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);

	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getBoolean("passwordReset");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public boolean update(User user, String oldUsername) throws SQLException {
	    String query = "UPDATE cse360users SET userName = ?, password = ?, email = ? WHERE userName = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, user.getUserName());
	        pstmt.setString(2, user.getPassword());
	        pstmt.setString(3, user.getEmail());
	        pstmt.setString(4, oldUsername); 

	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0; 
	    }
	}
	
	public User getUserByUsername(String username) throws SQLException {
	    String query = "SELECT * FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return new User(
	                        rs.getString("userName"),
	                        rs.getString("password"),
	                        rs.getString("role"),
	                        rs.getString("email")
	                );
	            }
	        }
	    }
	    return null;  // Return null if user not found
	}
	
	// Get all users from database
	public List<User> getAllUsers() {
	    List<User> users = new ArrayList<>();
	    String query = "SELECT * FROM cse360users";

	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            User user = new User(
	                    rs.getString("userName"),
	                    rs.getString("password"),
	                    rs.getString("role"),
	                    rs.getString("email")
	            );
	            users.add(user);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return users;
	}

	public boolean updateUser(String oldUsername, User user) throws SQLException {
	    String query = "UPDATE cse360users SET userName = ?, password = ?, email = ?, role = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, user.getUserName());
	        pstmt.setString(2, user.getPassword());
	        pstmt.setString(3, user.getEmail());
	        pstmt.setString(4, user.getRole());
	        pstmt.setString(5, oldUsername);  // Match the old username

	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0;  // Return true if update was successful
	    }
	}

	
	
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRole(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("role"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Retrieves the email address for a given username
	public String getUserEmail(String username) {
	    String query = "SELECT email FROM cse360users WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            return rs.getString("email");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	// Update user information given the old user and the updated user information
	public boolean updateUser(User oldUser, User newUser) {
		// check if the user exists
		if (doesUserExist(oldUser.getUserName())) {
			// get user id by UserName
			String selectQuery = "SELECT id FROM cse360users WHERE userName = ?";
			try (PreparedStatement pstmt1 = connection.prepareStatement(selectQuery)) {
				pstmt1.setString(1, oldUser.getUserName());
				ResultSet rs = pstmt1.executeQuery();
				
				// update user details with newUser information
				if (rs.next()) {
					int userId = rs.getInt("id");
					
					//String updateQuery = "UPDATE cse360users SET userName = ?, password = ?, role = ? WHERE id = ?";
					String updateQuery = "UPDATE cse360users SET userName = ?, password = ?, email = ?, role = ? WHERE id = ?";
					try (PreparedStatement pstmt2 = connection.prepareStatement(updateQuery)) {
						pstmt2.setString(1, newUser.getUserName());
						pstmt2.setString(2, newUser.getPassword());
						pstmt2.setString(3, newUser.getEmail());
						pstmt2.setString(4, newUser.getRole());
						pstmt2.setInt(5, userId);
						
						int rowsAffected = pstmt2.executeUpdate();
						if (rowsAffected > 0) {
							return true;
						}
					}
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String role, Timestamp expiresAt) {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code, role, expiresAt) VALUES (?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setString(2, role);
	        pstmt.setTimestamp(3, expiresAt);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE AND expiresAt > ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Get roles from invite code
	public String getRoleFromInviteCode(String code) {
	    String query = "SELECT role FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("role");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	//Delete User from the database
	public boolean deleteUser(String username) {
	    String query = "DELETE FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0; // if deletion successful
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // if deletion fails
	}


	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
	
	// Generate one-time password
	public String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARACTER);

        String combinedChars = UPPERCASE + LOWERCASE + DIGITS;
        while (password.length() < PASSWORD_LENGTH) {
            password.append(combinedChars.charAt(random.nextInt(combinedChars.length())));
        }

        return password.toString();
    }

}
