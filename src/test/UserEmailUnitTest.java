package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.User;

import static org.junit.jupiter.api.Assertions.*;

public class UserEmailUnitTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testUser", "testPassword", "testRole", "test@example.com");
    }

    @Test
    void testSetEmail() {
        user.setEmail("newemail@example.com");
        assertEquals("newemail@example.com", user.getEmail(), "Email should be updated correctly");
    }

    @Test
    void testGetEmail() {
        assertEquals("test@example.com", user.getEmail(), "Initial email should match the constructor input");
    }
} 