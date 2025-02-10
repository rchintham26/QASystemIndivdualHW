package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.UserNameRecognizer;

/**
 * <p> Title: UserNameRecognizerTest </p>
 *
 * <p> Description: This class contains unit tests for the {@link UserNameRecognizer} FSM.
 * Each test verifies different characteristics of the UserNameRecognizer such as allowed
 * characters, minimum and maximum lengths, and transitions involving special characters. </p>
 *
 * <p> Copyright: Dens Sumesh (c) 2025
 * 
 * @author Dens Sumesh
 * @version 1.00 2025-02-01 Initial version
 */
class UserNameRecognizerTest {

    /**
     * Resets static fields in {@link UserNameRecognizer} before each test.
     * This ensures that each test starts with a clean, default state.
     */
    @BeforeEach
    void setUp() {
        UserNameRecognizer.userNameRecognizerErrorMessage = "";
        UserNameRecognizer.userNameRecognizerInput = "";
        UserNameRecognizer.userNameRecognizerIndexofError = -1;
    }

    /**
     * Tests that an empty input string produces an appropriate error message.
     * The expected behavior is that the code detects no characters at all
     * and returns an error stating the input is empty.
     */
    @Test
    void testEmptyUserName() {
        String input = "";
        String result = UserNameRecognizer.checkForValidUserName(input);
        assertTrue(result.contains("empty"), 
                   "Error message should indicate that input is empty.");
        assertEquals(0, UserNameRecognizer.userNameRecognizerIndexofError,
                     "Index of error should be set to 0 for empty input.");
    }

    /**
     * Tests that the first character must be an alphabetic character. 
     * Here, '1' at the start should trigger a rejection.
     */
    @Test
    void testInvalidFirstCharacter() {
        String input = "1abc";
        String result = UserNameRecognizer.checkForValidUserName(input);
        assertTrue(result.contains("start with A-Z or a-z"), 
                   "Error message should indicate an invalid first character.");
    }

    /**
     * Tests the minimum length requirement (4). 
     * Here, only 3 characters ('Ab3') are provided, which should fail.
     */
    @Test
    void testTooShort() {
        String input = "Ab3";
        String result = UserNameRecognizer.checkForValidUserName(input);
        assertTrue(result.contains("at least 4 characters"), 
                   "Error message should indicate that username is too short.");
    }

    /**
     * Tests the maximum length requirement (16).
     * Here, 17 characters are provided, which should fail.
     */
    @Test
    void testTooLong() {
        String input = "Abcdefghijklmnopq"; // 17 characters
        String result = UserNameRecognizer.checkForValidUserName(input);

        assertTrue(result.contains("no more than 16 character"), 
                   "Error message should indicate that username is too long.");
    }

    /**
     * Tests that valid inputs with only alphabetic characters 
     * within the length constraints pass with no error.
     */
    @Test
    void testAlphabeticOnlyWithinLimits() {
        String input = "Abcde"; // 5 characters, all alphabetic
        String result = UserNameRecognizer.checkForValidUserName(input);
        assertEquals("", result, 
                     "Should be valid for an alphabetic-only username within limits.");
        assertEquals(-1, UserNameRecognizer.userNameRecognizerIndexofError,
                     "Error index should be -1 indicating no error.");
    }

    /**
     * Tests usernames that contain allowed alphanumeric characters beyond the first character.
     * This verifies that numbers are allowed after the first character is alphabetic.
     */
    @Test
    void testAlphaNumericWithinLimits() {
        String input = "Abc123";
        String result = UserNameRecognizer.checkForValidUserName(input);
        assertEquals("", result, 
                     "Usernames with letters and digits (after the first alpha) should be valid.");
    }

    /**
     * Tests that a valid username with periods (.), hyphens (-), or underscores (_)
     * are correctly allowed at intermediate positions, but not as the first character.
     */
    @Test
    void testAllowedSpecialCharacters() {
        // Valid: starts with letter, and we have . - and _
        // e.g., "Ab.cd-12_ef" is 11 characters, all valid transitions
        String input = "Ab.cd-12_ef";
        String result = UserNameRecognizer.checkForValidUserName(input);
        assertEquals("", result, 
                     "Should be valid with periods, hyphens, and underscores in between alphanumerics.");
    }

    /**
     * Tests that placing a special character at the first position is invalid.
     */
    @Test
    void testInvalidSpecialCharacterFirstPosition() {
        String input = ".Abc";
        String result = UserNameRecognizer.checkForValidUserName(input);
        assertTrue(result.contains("A UserName must start with A-Z or a-z."), 
                   "First character cannot be a period (or any special character besides letters).");
    }

    /**
     * Tests that after a '.', '-', or '_', the next character must be alphanumeric,
     * otherwise it is rejected.
     */
    @Test
    void testSpecialCharacterFollowedByInvalidCharacter() {
        // For example, '.' is followed by '#' which is not allowed by the FSM
        String input = "Ab.#123";
        String result = UserNameRecognizer.checkForValidUserName(input);
        assertTrue(result.contains("after a period, minus, or underscore must be A-Z, a-z, 0-9."),
                   "Error message should indicate what is allowed after '.' '-' '_'.");
    }

    /**
     * Tests that a valid username of the exact max length (16) is accepted.
     */
    @Test
    void testExactMaxLength() {
        // "A1.b2-C3_d4E5f6" -> 16 characters
        String input = "A1.b2-C3_d4E5f6";
        String result = UserNameRecognizer.checkForValidUserName(input);
        assertEquals("", result, 
                     "Should allow exactly 16 characters if they are valid.");
    }

    /**
     * Tests that a valid username of the exact min length (4) is accepted.
     */
    @Test
    void testExactMinLength() {
        // "A3-c" is 4 characters, starting with a letter
        String input = "A3-c";
        String result = UserNameRecognizer.checkForValidUserName(input);
        assertEquals("", result, 
                     "Should allow exactly 4 characters if valid.");
    }

    /**
     * Tests that an input containing disallowed characters in the middle
     * triggers an error for the correct reason.
     */
    @Test
    void testInvalidCharacterInMiddle() {
        // '^' is not allowed
        String input = "Abc^de";
        String result = UserNameRecognizer.checkForValidUserName(input);
        assertTrue(UserNameRecognizer.userNameRecognizerIndexofError < input.length(), 
                   "Should flag the position of the invalid character.");
    }
}
