package application;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * <p> Title: Email Address Validator. </p>
 * 
 * <p> Description: A comprehensive email validation utility that ensures email addresses
 * conform to standard formatting rules and RFC 5322 requirements. The validator checks
 * for proper formatting of local and domain parts, length restrictions, and character
 * validity. </p>
 * 
 * <p> Copyright: Dens Sumesh © 2025 </p>
 * 
 * <p> @author  Dens Sumesh
 * @version 0.01   2025-02-01  Base version
 * </p>
 */
public class EmailValidator {
    
    /**********************************************************************************************
     * 
     * Result attributes to be used for email validation where detailed error messages will
     * enhance the user experience.
     * 
     */
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    
    public static String emailErrorMessage = "";     // The error message text
    public static String emailInput = "";            // The input being processed
    public static int emailIndexOfError = -1;        // The index where the error was located
    /**
     * Performs a comprehensive validation of an email address including basic regex checks
     * and additional RFC 5322-related checks.
     * 
     * @param email  The email address to validate
     * @return       true if the email is valid, false if validation errors were found
     * @throws       IllegalArgumentException if the email parameter is null
     */
    public boolean isValidEmail(String email) {
        // Reset/Initialize error state and record the input
        emailInput = email;
        emailErrorMessage = "";
        emailIndexOfError = -1;
        // Check for null parameter
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        // Check for empty email
        if (email.isEmpty()) {
            emailErrorMessage = "Email cannot be empty";
            return false;
        }

        // Check for length exceeding max allowed
        if (email.length() > MAX_EMAIL_LENGTH) {
            emailErrorMessage = "Email exceeds maximum length of " + MAX_EMAIL_LENGTH + " characters";
            emailIndexOfError = MAX_EMAIL_LENGTH;
            return false;
        }

        // Check against regex pattern
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            emailErrorMessage = "Invalid email format";
            emailIndexOfError = 0; // Approximate index for error
            return false;
        }

        // At this point, the email passed basic regex checks.
        // Additional domain and local-part checks for RFC 5322 compliance:
        String[] parts = email.split("@");
        if (parts.length == 2) {
            // Local-part check: ensure it does not exceed 64 characters
            if (parts[0].length() > 64) {
                emailErrorMessage = "Local part exceeds 64 characters";
                emailIndexOfError = 64;
                return false;
            }

            // Domain checks
            String domain = parts[1];
            if (!domain.contains(".")) {
                emailErrorMessage = "Domain must contain at least one dot";
                // The error index is roughly where the domain starts
                emailIndexOfError = email.indexOf("@") + 1; 
                return false;
            }

            if (domain.startsWith(".") || domain.endsWith(".")) {
                emailErrorMessage = "Domain cannot start or end with a dot";
                emailIndexOfError = domain.startsWith(".") 
                    ? (email.indexOf("@") + 1)   // Index of the first dot in domain
                    : (email.length() - 1);     // Index of the last character if it is a dot
                return false;
            }
        }

        // If all checks pass, the email is valid
        return true;
    }

    /**
     * Gets the current error message from the last validation attempt.
     * 
     * @return the error message, or empty string if no error occurred
     */
    public String getErrorMessage() {
        return emailErrorMessage;
    }

    /**
     * Gets the index where the error was found in the last validation attempt.
     * 
     * @return the index of the error, or -1 if no error occurred
     */
    public int getErrorIndex() {
        return emailIndexOfError;
    }
}
