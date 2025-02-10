package test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.sql.SQLException;
import org.junit.jupiter.api.*;

import databasePart1.DatabaseHelper;
import application.User;

public class DatabaseHelperUnitTest {
	private DatabaseHelper databaseHelper;
	
	// Connect to database before tests
	@BeforeEach
	void connect() throws SQLException {
		databaseHelper = new DatabaseHelper();
		databaseHelper.connectToDatabase();
	}
	

	// Disconnect from database after each test
	@AfterEach
	void disconnect() throws SQLException {
		databaseHelper.closeConnection();
	}
	
	// Test database connection
	@Test
    void testConnectToDatabase() {
        assertDoesNotThrow(() -> databaseHelper.connectToDatabase());
    }

	// Test empty database
    @Test
    void testIsDatabaseEmpty() throws SQLException {
        assertTrue(databaseHelper.isDatabaseEmpty());
    }
    
    // Test register and login
    @Test
    void testRegisterAndLogin() throws SQLException {
        User user = new User("testUser", "password", "user", "email");
        databaseHelper.register(user);
        assertTrue(databaseHelper.login(user));
    }
    
    // Test user existence method
    @Test
    void testDoesUserExist() throws SQLException {
        User user = new User("testUser", "password", "user", "email");
        databaseHelper.register(user);
        assertTrue(databaseHelper.doesUserExist("testUser"));
        assertFalse(databaseHelper.doesUserExist("nonexistentUser"));
    }

    // Get get user role method
    @Test
    void testGetUserRole() throws SQLException {
        User user = new User("testUser", "password", "user", "email");
        databaseHelper.register(user);
        assertEquals("user", databaseHelper.getUserRole("testUser"));
    }

    // Test invite generation and validation
    @Test
    void testGenerateAndValidateInvitationCode() {
    	Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + 5000);
        String code = databaseHelper.generateInvitationCode("student", expiresAt);
        assertNotNull(code);
        assertTrue(databaseHelper.validateInvitationCode(code));
        assertFalse(databaseHelper.validateInvitationCode(code));
    }
    
    // Test invite expiration
    @Test
    void testInvitationCodeExpiration() {
    	Timestamp expiresAt = new Timestamp(System.currentTimeMillis());
        String code = databaseHelper.generateInvitationCode("student", expiresAt);
        assertNotNull(code);
        
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        assertFalse(databaseHelper.validateInvitationCode(code));
    }

    // Test updating user information
    @Test
    void testUpdateUser() throws SQLException {
        User oldUser = new User("oldUser", "oldPassword", "oldRole", "email");
        databaseHelper.register(oldUser);

        User newUser = new User("newUser", "newPassword", "newRole", "email");
        assertTrue(databaseHelper.updateUser(oldUser, newUser));
        assertTrue(databaseHelper.login(newUser));
    }
    
    // Test closing database connection.
    @Test
    void testCloseConnection() {
        assertDoesNotThrow(() -> databaseHelper.closeConnection());
    }
	
}
