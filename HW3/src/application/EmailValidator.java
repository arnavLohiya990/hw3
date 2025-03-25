// R.McQuesten, 2025-02-05, Add email validation using regular expression
//  Or regex for short. We use it to leverage pattern matching in strings
//  Documentation used: https://www.geeksforgeeks.org/check-email-address-valid-not-java/
//  To help understand and apply the pattern we use to check for valid emails
//  We know form is of: userName@domain.com or domain.org or domain.edu

package application;

// Import packages for processing regex
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@SuppressWarnings("unused")
public class EmailValidator {
	// This subroutine validates email addr input
	public static boolean validateEmailAddr(String emailAddr) {
		// Let's force the new account setups to use school emails for security
		// Purposes, thus we just need to adjust the regex to fit this pattern
		// Of user@domain.edu so we replace the last part [a-zA-Z]{2,7} with edu
		
		// The carat ^ is an anchor for the start of the string
		// The dollar sign $ is an anchor for the end of the string
		// Thus, we are ensuring the pattern matches for the entire string
		// Not just considering substrings between the plus signs
		String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
	               			"(?:[a-zA-Z0-9-]+\\.)+edu$";
		
		// Not going to allow for mult subdomains but functionality is if needed
		// Last change, we want to allow for multiple subdomains like asu.edu
		// So, again we make this pattern fit properly wrt our constraints
		//	String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
		//             			"(?:[a-zA-Z0-9-]+\\.)+edu$";		
		
		// We then compile the regex using a method from Pattern class
		Pattern pattern = Pattern.compile(regex);
		
		// We then verify if email matches the pattern
		// If it does, then its a valid email input
		// If not, then it is invalid and we should
		// Show an appropriate error message and let
		// The user input this information in again
		// If its null then it doesn't match and
		// If the pattern doesnt match the regex
		// Then its not a valid input, we return
		// True or False depending wrt validity
		return emailAddr != null && pattern.matcher(emailAddr).matches();
	}
}