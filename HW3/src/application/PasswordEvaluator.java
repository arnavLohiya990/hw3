package application;


public class PasswordEvaluator {
	/**
	 * <p> Title: Directed Graph-translated Password Assessor. </p>
	 * 
	 * <p> Description: A demonstration of the mechanical translation of Directed Graph 
	 * diagram into an executable Java program using the Password Evaluator Directed Graph. 
	 * The code detailed design is based on a while loop with a cascade of if statements</p>
	 * 
	 * <p> Copyright: Lynn Robert Carter © 2022 </p>
	 * 
	 * @author Lynn Robert Carter
	 * 
	 * @version 0.00		2018-02-22	Initial baseline 
	 * 
	 */

	/**********************************************************************************************
	 * 
	 * Result attributes to be used for GUI applications where a detailed error message and a 
	 * pointer to the character of the error will enhance the user experience.
	 * 
	 */

	public static String passwordErrorMessage = "";		// The error message text
	public static String passwordInput = "";			// The input being processed
	@SuppressWarnings("unused")
	private static int passwordSize = 0;
	public static int passwordIndexofError = -1;		// The index where the error was located
	public static boolean foundUpperCase = false;
	public static boolean foundLowerCase = false;
	public static boolean foundNumericDigit = false;
	public static boolean foundSpecialChar = false;
	public static boolean foundLongEnough = false;
	public static boolean foundOtherChar = false;
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// The flag that specifies if the FSM is 
	private static int stateP = 0;						// The current state value
	private static int nextStateP = 0;					// The next state value
	
	/**********
	 * This private method display the input line and then on a line under it displays an up arrow
	 * at the point where an error should one be detected.  This method is designed to be used to 
	 * display the error message on the console terminal.
	 * 
	 * @param input				The input string
	 * @param currentCharNdx	The location where an error was found
	 * @return					Two lines, the entire input line followed by a line with an up arrow
	 */
	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			currentChar = ' ';
			running = false;
		}
	}
	
	@SuppressWarnings("unused")
	private static void displayInputState() {
		// Display the entire input line
		System.out.println(inputLine);
		System.out.println(inputLine.substring(0,currentCharNdx) + "?");
		System.out.println("The password size: " + inputLine.length() + "  |  The currentCharNdx: " + 
				currentCharNdx + "  |  The currentChar: \"" + currentChar + "\"");
	}

	/**********
	 * This method is a mechanical transformation of a Directed Graph diagram into a Java
	 * method.
	 * 
	 * @param input		The input string for directed graph processing
	 * @return			An output string that is empty if every things is okay or it will be
	 * 						a string with a help description of the error follow by two lines
	 * 						that shows the input line follow by a line with an up arrow at the
	 *						point where the error was found.
	 */
	public static String checkForPassword(String input) {
		if(input.length() <= 0) {
			passwordIndexofError = 0;	// Error at first character;
			return "\n*** ERROR *** The input is empty";
		}
		
		// The local variables used to perform the Finite State Machine simulation
			stateP = 0;							// This is the FSM state number
			inputLine = input;					// Save the reference to the input line as a global
			currentCharNdx = 0;					// The index of the current character
			currentChar = inputLine.charAt(0);		// The current character from above indexed position
				
			// The Finite State Machines continues until the end of the input is reached or at some 
			// state the current character does not match any valid transition to a next state

			passwordInput = input;	// Save a copy of the input
			running = true;						// Start the loop
			nextStateP = -1;					// There is no next state
			passwordSize = 0;
			foundUpperCase = false;				// Reset the Boolean flag
			foundLowerCase = false;				// Reset the Boolean flag
			foundNumericDigit = false;			// Reset the Boolean flag
			foundSpecialChar = false;			// Reset the Boolean flag		
			foundLongEnough = false;
			foundOtherChar = false;// Reset the Boolean flag
			
			while(running) {
				
				switch(stateP) {
				case 0:
					
					//Checks for the size of the password
					if(input.length() >= 8) {
						foundLongEnough = true;
					} 
					
					//Check if all requirements for the password have been meet
					if((foundUpperCase == true) &&
							(foundLowerCase == true)&&
							(foundNumericDigit == true)&&
							(foundSpecialChar == true)&&
							(foundLongEnough == true))
					{
						nextStateP = 1;
						
					}
					//Checks for upper case [1] and follow actions
					if (currentChar >= 'A' && currentChar <= 'Z' ) {
						passwordSize++;
						foundUpperCase = true;
						moveToNextCharacter();
						nextStateP = 0;
					}
					//Check for lower case [2] and follow actions
					else if (currentChar >= 'a' && currentChar <= 'z' ) {
						passwordSize++;
						foundLowerCase = true;
						moveToNextCharacter();
						nextStateP = 0;	
					}
					//Check for numeric digit [3] and follow actions
					else if (currentChar >= '0' && currentChar <= '9' ) {
						passwordSize++;
						foundNumericDigit = true;
						moveToNextCharacter();
						nextStateP = 0;	
					}
					//check for special character [4] and follow actions
					else if("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currentChar) >= 0) {
						passwordSize++;
						foundSpecialChar = true;
						moveToNextCharacter();
						nextStateP = 0;	
					} 
					//Check for other chars [5]
					else {
						foundOtherChar = true;
						running = false;
						break;
					}
					break;
					
				case 1:
					//Final State
					running = false;
					break;
					
					
				
				
				}//switch bracket
				
				//Switch States
				if(running) {
					stateP = nextStateP;
						
				}
		
				
			}
			passwordIndexofError = currentCharNdx;	// Set index of a possible error;
			passwordErrorMessage = "\n*** ERROR *** ";
			
			// The following code is a slight variation to support just console output.
			switch (stateP) {
			case 0:
				String errMessage = "";
				// State 0 is not a final state, so we can return a very specific error message
				//Errors message for missing upper case
				if (!foundUpperCase)
					errMessage += "Missing Upper case; ";
				
				//Errors message for missing lower case
				if (!foundLowerCase)
					errMessage += "Missing Lower case; ";
				
				//Errors message for missing numeric digit
				if (!foundNumericDigit)
					errMessage += "Missing Numeric digits; ";
				
				//Errors message for missing special character
				if (!foundSpecialChar)
					errMessage += "Missing Special character; ";
				
				//Errors message for not being long enough
				if (!foundLongEnough)
					errMessage += "Not Long Enough; ";
				
				if (errMessage == "")
					return "";
				
				passwordIndexofError = currentCharNdx;
				return errMessage + "conditions were not satisfied";


				
			case 1:
				//final State 
				// Valid Password
				passwordIndexofError  = -1;
				passwordErrorMessage = "";
				return passwordErrorMessage;
		

			default:
				return "";
			}
			}
}