// R.McQuesten, 2025-02-05, Add full name validation using regular expression
//  Or regex for short. We use it to leverage pattern matching in strings
//  Documentation used: https://www.w3schools.com/java/java_regex.asp

package application;

// Import packages for processing regex
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@SuppressWarnings("unused")
public class FullNameValidator {
	// This subroutine validates full name input
	public static boolean validateFullName(String userFullName) {
		// Let's force the new account setups to use full name in form of First Last
		// Set the regex pattern in order to find match for valid formats
		// The carat ^ is an anchor for the start of the string
		// The dollar sign $ is an anchor for the end of the string
		// Thus, we are ensuring the pattern matches for the entire string
		// Not just considering the case of substrings but full strings
		
		// For reference...
		// \s requires space b/w first and last name
		// [A-Z] makes sure last name starts with uppercase alphabetic char
		// [a-zA-Z]*$ lets us match for any num of alphabetic chars immediately
		//  following the initial uppercase alphabetic char in the last name
		String regex = "^[A-Z][a-zA-Z]*\\s[A-Z][a-zA-Z]*$";
				
		// We then compile the regex using a method from Pattern class
		Pattern pattern = Pattern.compile(regex);
		
		// We then verify if full name matches the pattern
		// If it does, then its a valid email input
		// If not, then it is invalid and we should
		// Show an appropriate error message and let
		// The user input this information in again
		// If its null then it doesn't match and
		// If the pattern doesnt match the regex
		// Then its not a valid input, we return
		// True or False depending wrt validity
		return userFullName != null && pattern.matcher(userFullName).matches();
	}
}
