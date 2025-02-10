package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.PasswordEvaluator;

/**
 * <p>
 * Title: PasswordEvaluatorTest
 * </p>
 *
 * <p>
 * Description: This class contains unit tests for the {@link PasswordEvaluator} class.
 * Each test validates a specific requirement of the password evaluation logic, ensuring
 * robust coverage of edge cases and proper error handling.
 * </p>
 *
 * <p>
 * Copyright: Dens Sumesh (c) 2025
 * </p>
 *
 * @author Dens Sumesh
 * @version 1.00
 */
class PasswordEvaluatorTest {

    /**
     * Sets up the test environment by resetting static fields in
     * {@link PasswordEvaluator} to ensure each test starts in a clean state.
     */
    @BeforeEach
    void setUp() {
        PasswordEvaluator.passwordErrorMessage = "";
        PasswordEvaluator.passwordInput = "";
        PasswordEvaluator.passwordIndexofError = -1;
        PasswordEvaluator.foundUpperCase = false;
        PasswordEvaluator.foundLowerCase = false;
        PasswordEvaluator.foundNumericDigit = false;
        PasswordEvaluator.foundSpecialChar = false;
        PasswordEvaluator.foundLongEnough = false;
    }

    /**
     * Tests that an empty password string triggers the correct error message.
     * An empty password should be rejected immediately without further checks.
     */
    @Test
    void testEmptyPassword() {
        String result = PasswordEvaluator.evaluatePassword("");
        assertTrue(result.contains("Password cannot be empty"), 
                   "Should detect empty password error");
    }

    /**
     * Tests that an invalid character '=' triggers an error, as '=' is not
     * in the allowed special-character set. Also checks that the index of
     * error points to the correct position.
     */
    @Test
    void testInvalidCharacter() {
        // Password containing an invalid character: '=' (not in allowed set)
        String result = PasswordEvaluator.evaluatePassword("Pass=word123");
        assertTrue(result.contains("An invalid character has been found!"), 
                   "Should detect an invalid character '='");
        assertEquals(4, PasswordEvaluator.passwordIndexofError, 
                     "Error index should point to the invalid character");
    }

    /**
     * Tests a password missing an uppercase letter. It has lowercase, digit,
     * special characters, and is sufficiently long, but lacks uppercase.
     */
    @Test
    void testMissingUpperCase() {
        // Valid length, has lower, digit, special, but no uppercase
        String result = PasswordEvaluator.evaluatePassword("abc1~abc1");
        assertTrue(result.contains("Upper case"), 
                   "Should report missing upper case");
        assertTrue(result.contains("password conditions were not satisfied"), 
                   "Should indicate the password is not valid");
        assertFalse(PasswordEvaluator.foundUpperCase, 
                    "Upper case flag should remain false");
    }

    /**
     * Tests a password missing a lowercase letter. It has uppercase, digit,
     * special characters, and is sufficiently long, but lacks lowercase.
     */
    @Test
    void testMissingLowerCase() {
        // Valid length, has upper, digit, special, but no lowercase
        String result = PasswordEvaluator.evaluatePassword("ABC1~ABC1");
        assertTrue(result.contains("Lower case"), 
                   "Should report missing lower case");
        assertFalse(PasswordEvaluator.foundLowerCase, 
                    "Lower case flag should remain false");
    }

    /**
     * Tests a password missing a digit. It has uppercase, lowercase, special
     * characters, and is sufficiently long, but no digit is included.
     */
    @Test
    void testMissingDigit() {
        // Valid length, has upper, lower, special, but no digit
        String result = PasswordEvaluator.evaluatePassword("Ab~Abcde");
        assertTrue(result.contains("Numeric digits"), 
                   "Should report missing numeric digit");
        assertFalse(PasswordEvaluator.foundNumericDigit, 
                    "Numeric digit flag should remain false");
    }

    /**
     * Tests a password missing a special character. It has uppercase, lowercase,
     * a digit, and is sufficiently long, but lacks any valid special character.
     */
    @Test
    void testMissingSpecialChar() {
        // Valid length, has upper, lower, digit, but no special character
        String result = PasswordEvaluator.evaluatePassword("Abcde123");
        assertTrue(result.contains("Special character"), 
                   "Should report missing special character");
        assertFalse(PasswordEvaluator.foundSpecialChar, 
                    "Special char flag should remain false");
    }

    /**
     * Tests a password that is too short (fewer than 8 characters).
     * Even if it contains all categories of characters, its length is insufficient.
     */
    @Test
    void testLengthNotLongEnough() {
        // Only 7 characters
        // Suppose it has uppercase, lowercase, digit, special, but is short
        String result = PasswordEvaluator.evaluatePassword("Ab1~Ab1");
        assertTrue(result.contains("Long Enough"), 
                   "Should report 'Long Enough' is missing");
        assertFalse(PasswordEvaluator.foundLongEnough, 
                    "Length requirement flag should remain false");
    }

    /**
     * Tests a password that has exactly 8 characters, meeting all conditions:
     * uppercase, lowercase, digit, and special character.
     */
    @Test
    void testExactlyEightCharacters() {
        // This is exactly 8 characters, meeting all conditions
        // Contains: uppercase (A), lowercase (b), digit (1), special (~)
        String result = PasswordEvaluator.evaluatePassword("Ab1~Cd2#");
        assertEquals("", result, "Should be a valid password with no errors");
        assertTrue(PasswordEvaluator.foundUpperCase, 
                   "Upper case flag should be true");
        assertTrue(PasswordEvaluator.foundLowerCase, 
                   "Lower case flag should be true");
        assertTrue(PasswordEvaluator.foundNumericDigit, 
                   "Numeric digit flag should be true");
        assertTrue(PasswordEvaluator.foundSpecialChar, 
                   "Special char flag should be true");
        assertTrue(PasswordEvaluator.foundLongEnough, 
                   "Length requirement flag should be true");
    }

    /**
     * Tests a password with more than 8 characters, ensuring it still meets
     * the uppercase, lowercase, digit, and special character requirements.
     */
    @Test
    void testMoreThanEightCharacters() {
        // This is more than 8 characters
        // Contains uppercase (A), lowercase (b, c, d), digit (1), special (#)
        String result = PasswordEvaluator.evaluatePassword("Abcd123#xyz");
        assertEquals("", result, "Should be a valid password with no errors");
        assertTrue(PasswordEvaluator.foundUpperCase, 
                   "Upper case flag should be true");
        assertTrue(PasswordEvaluator.foundLowerCase, 
                   "Lower case flag should be true");
        assertTrue(PasswordEvaluator.foundNumericDigit, 
                   "Numeric digit flag should be true");
        assertTrue(PasswordEvaluator.foundSpecialChar, 
                   "Special char flag should be true");
        assertTrue(PasswordEvaluator.foundLongEnough, 
                   "Length requirement flag should be true");
    }

    /**
     * Tests a more complex valid password, including multiple special
     * characters, digits, uppercase and lowercase letters, and length
     * greater than 8.
     */
    @Test
    void testValidComplexPassword() {
        // A longer, more "complex" password
        String password = "A1b2C3d4~!@#";
        String result = PasswordEvaluator.evaluatePassword(password);
        assertEquals("", result, "Should pass as a valid password");
        assertTrue(PasswordEvaluator.foundUpperCase, 
                   "Should contain uppercase letters");
        assertTrue(PasswordEvaluator.foundLowerCase, 
                   "Should contain lowercase letters");
        assertTrue(PasswordEvaluator.foundNumericDigit, 
                   "Should contain digits");
        assertTrue(PasswordEvaluator.foundSpecialChar, 
                   "Should contain special characters");
        assertTrue(PasswordEvaluator.foundLongEnough, 
                   "Should be long enough");
    }
}
