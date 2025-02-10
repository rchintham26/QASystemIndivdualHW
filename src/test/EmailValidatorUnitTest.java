package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.EmailValidator;

/**
 * <p>
 * Title: EmailValidatorTest
 * </p>
 * 
 * <p>
 * Description: This test class covers various scenarios to validate the correctness of
 * {@link EmailValidator}.
 * </p>
 * 
 * <p>Copyright: (c) 2025</p>
 * 
 * @author 
 * @version 1.00
 */
class EmailValidatorTest {

    private EmailValidator validator;

    /**
     * Initializes a new EmailValidator instance before each test.
     */
    @BeforeEach
    void setUp() {
        validator = new EmailValidator();
    }

    /**
     * Tests that passing a null email address throws an IllegalArgumentException.
     */
    @Test
    void testNullEmailThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            validator.isValidEmail(null);
        }, "A null email should throw IllegalArgumentException");
    }

    /**
     * Tests that an empty email address is considered invalid and sets an appropriate error message.
     */
    @Test
    void testEmptyEmail() {
        boolean isValid = validator.isValidEmail("");
        assertFalse(isValid, "An empty email should be invalid");
        assertTrue(validator.getErrorMessage().contains("empty"),
                   "Error message should indicate the email cannot be empty");
    }

    /**
     * Tests that an email exceeding 254 characters in length is invalid.
     * The error should point to the maximum length index and mention the maximum length.
     */
    @Test
    void testExceedMaxEmailLength() {
        // 255 characters: 1 char for 'a', 253 for the rest, plus '@', etc.
        StringBuilder sb = new StringBuilder();
        // local part: 250 chars
        for (int i = 0; i < 250; i++) {
            sb.append('a');
        }
        sb.append("@mail.com"); // total length > 254
        String longEmail = sb.toString();

        boolean isValid = validator.isValidEmail(longEmail);
        assertFalse(isValid, "An email longer than 254 chars should be invalid");
        assertTrue(validator.getErrorMessage().contains("exceeds maximum length"),
                   "Error message should indicate the email is too long");
        assertEquals(254, validator.getErrorIndex(),
                     "Error index should be at the 254th character (the max length)");
    }

    /**
     * Tests an email that fails the basic regex validation (missing '@').
     */
    @Test
    void testInvalidFormatNoAtSymbol() {
        boolean isValid = validator.isValidEmail("userdomain.com");
        assertFalse(isValid, "An email without '@' should be invalid");
        assertTrue(validator.getErrorMessage().contains("Invalid email format"),
                   "Error message should indicate invalid format");
        assertEquals(0, validator.getErrorIndex(), "Error index should be 0 for regex failure");
    }

    /**
     * Tests an email that fails the basic regex validation (invalid characters).
     */
    @Test
    void testInvalidCharactersInEmail() {
        boolean isValid = validator.isValidEmail("user@@domain.com");
        assertFalse(isValid, "Multiple '@' should fail regex validation");
        assertTrue(validator.getErrorMessage().contains("Invalid email format"),
                   "Error message should indicate invalid format");
    }

    /**
     * Tests that the local part cannot exceed 64 characters.
     */
    @Test
    void testLocalPartExceeding64Characters() {
        // Construct local-part with 65 characters
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 65; i++) {
            sb.append('a');
        }
        sb.append("@domain.com");
        String email = sb.toString();

        boolean isValid = validator.isValidEmail(email);
        assertFalse(isValid, "Local part exceeding 64 characters should be invalid");
        assertTrue(validator.getErrorMessage().contains("Local part exceeds 64 characters"),
                   "Error message should indicate local part is too long");
        assertEquals(64, validator.getErrorIndex(), 
                     "Error index should point to the 64th position for local part issues");
    }

    /**
     * Tests that the domain part must contain at least one dot.
     */
    @Test
    void testDomainWithoutDot() {
        boolean isValid = validator.isValidEmail("user@domain");
        assertFalse(isValid, "Domain without a dot ('.') should be invalid");
        assertTrue(validator.getErrorMessage().contains("must contain at least one dot"),
                   "Error message should indicate a missing dot in the domain");
        // The index should roughly be the position after the '@' symbol
        assertEquals("user@".length(), validator.getErrorIndex(),
                     "Error index should point to the domain's start");
    }

    /**
     * Tests that the domain cannot start with a dot.
     */
    @Test
    void testDomainStartsWithDot() {
        boolean isValid = validator.isValidEmail("user@.domain.com");
        assertFalse(isValid, "Domain starting with '.' should be invalid");
        assertTrue(validator.getErrorMessage().contains("cannot start or end with a dot"),
                   "Error message should indicate domain can't start with a dot");
        // Error index is the position after '@' (the dot)
        assertEquals("user@".length(), validator.getErrorIndex(),
                     "Error index should indicate the dot after '@'");
    }

    /**
     * Tests that the domain cannot end with a dot.
     */
    @Test
    void testDomainEndsWithDot() {
        boolean isValid = validator.isValidEmail("user@domain.com.");
        assertFalse(isValid, "Domain ending with '.' should be invalid");
        assertTrue(validator.getErrorMessage().contains("cannot start or end with a dot"),
                   "Error message should indicate domain can't end with a dot");
        assertEquals("user@domain.com.".length() - 1, validator.getErrorIndex(),
                     "Error index should point to the final dot");
    }

    /**
     * Tests a valid email format, ensuring the validation method returns true
     * and no error message is generated.
     */
    @Test
    void testValidEmail() {
        String email = "user.name+test@domain.co.uk";
        boolean isValid = validator.isValidEmail(email);
        assertTrue(isValid, "A properly formatted email should be valid");
        assertEquals("", validator.getErrorMessage(), 
                     "No error message should be set for valid emails");
        assertEquals(-1, validator.getErrorIndex(), 
                     "Error index should be -1 for valid emails");
    }

    /**
     * Tests another valid email variant, verifying multiple subdomains and
     * underscores are acceptable within the local part.
     */
    @Test
    void testValidEmailWithUnderscoreAndSubdomains() {
        String email = "user_name@sub.domain.example.com";
        boolean isValid = validator.isValidEmail(email);
        assertTrue(isValid, "Email with underscore and subdomains should be valid");
        assertEquals("", validator.getErrorMessage(),
                     "No error message should be set for a valid email");
        assertEquals(-1, validator.getErrorIndex(),
                     "Error index should be -1 for valid emails");
    }
}
