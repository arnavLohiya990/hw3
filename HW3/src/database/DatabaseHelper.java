package databasePart1;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

import application.Answer;
import application.Question;
import application.User;
import javafx.collections.FXCollections;

// Imported for list all users command
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

// Added for function related to listing a user's multiple roles function 
import java.io.FileWriter;
import java.io.IOException;

/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
@SuppressWarnings("unused")
public class DatabaseHelper {

    // JDBC driver name and database URL 
    static final String JDBC_DRIVER = "org.h2.Driver";   
    static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

    // Database credentials 
    static final String USER = "sa"; 
    static final String PASS = ""; 

    private Connection connection = null;
    private Statement statement = null; 

    public DatabaseHelper() {
        try {
            connectToDatabase();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); // Load the JDBC driver
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement(); 

            createTables();  // Create the necessary tables if they don’t exist
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    // R.McQuesten-TP1, 2025-02-04, Add cols for user full name, email
    // R.McQuesten-TP1, 2025-02-05, Add col for one time password implementation
    private void createTables() throws SQLException {      
        String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "userName VARCHAR(255) UNIQUE, "
                + "password VARCHAR(255), "
                + "role VARCHAR(225),"
                + "userFullName VARCHAR(255), "
                + "userEmail VARCHAR(255), "
                + "oneTimePassword VARCHAR(4))";
        statement.execute(userTable);

        // Create the invitation codes table
        String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
                + "code VARCHAR(10) PRIMARY KEY, "
                + "assignedRoles VARCHAR(225),"
                + "isUsed BOOLEAN DEFAULT FALSE,"
                + "expirationTime TIMESTAMP)";
        statement.execute(invitationCodesTable);

        // Create the questions table
        String questionsTable = "CREATE TABLE IF NOT EXISTS questions ("
                + "questionId INT AUTO_INCREMENT PRIMARY KEY,"
                + "ownerUserId INT,"
                + "title VARCHAR(40),"
                + "body VARCHAR(255),"
                + "isResolved BOOLEAN DEFAULT FALSE,"//Arnav Lohiya TP2 - 2025-02-26, added new column 'isResolved'
                + "FOREIGN KEY (ownerUserId) REFERENCES cse360users(id))";
        statement.execute(questionsTable);

        // Create the answers table
        String answersTable = "CREATE TABLE IF NOT EXISTS answers ("
                + "answerId INT AUTO_INCREMENT PRIMARY KEY,"
                + "ownerUserId INT,"
                + "parentQuestionId INT,"
                + "body VARCHAR(255),"
                + "markedAccepted BOOLEAN DEFAULT FALSE,"
                + "FOREIGN KEY (ownerUserId) REFERENCES cse360users(id),"
                + "FOREIGN KEY (parentQuestionId) REFERENCES questions(questionId)"
                + ")";
        statement.execute(answersTable);
        
        // R.McQuesten-TP2, 2025-02-24, Create feedbackTable to interface with PMs
        // Create a new table for storing private feedback messages between users
        // Now includes the questionId so we can indicate which question the message is about
        String feedbackTable = "CREATE TABLE IF NOT EXISTS feedback ("
                + "feedbackId INT AUTO_INCREMENT PRIMARY KEY, "
                + "senderUserId INT, "
                + "recipientUserId INT, "
                + "questionId INT, "
                + "message VARCHAR(255), "
                + "FOREIGN KEY (senderUserId) REFERENCES cse360users(id),"
                + "FOREIGN KEY (recipientUserId) REFERENCES cse360users(id),"
                + "FOREIGN KEY (questionId) REFERENCES questions(questionId) ON DELETE CASCADE"
                + ")";
        statement.execute(feedbackTable);

        // Check if the 'questionId' column 
        // exists in the feedback table
        // If it doesn't, add the column
        DatabaseMetaData meta = connection.getMetaData();
        // NOTE: H2 stores unquoted identifiers in uppercase.
        ResultSet rs = meta.getColumns(null, null, "FEEDBACK", "QUESTIONID");
        if (!rs.next()) {
            // Column does not exist, thus add it
            String alterQuery = "ALTER TABLE feedback ADD COLUMN questionId INT";
            statement.execute(alterQuery);
        }
        rs.close(); // Close the result set obj
    }

    // R.McQuesten-TP1, 2025-02-05, Getter subprogram returns full name of user
    public String getUserFullName(String userName) {
        String query = "SELECT userFullName FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("userFullName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    // R.McQuesten-TP1, 2025-02-05, Getter subprogram returns user email
    public String getUserEmail(String userName) {
        String query = "SELECT userEmail FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("userEmail");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
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
    // R.McQuesten-TP1, 2025-02-04, Add cols for new account info fields
    public void register(User user) throws SQLException {
        String insertUser = "INSERT INTO cse360users (userName, password, role, userFullName, userEmail) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getUserFullName());
            pstmt.setString(5, user.getUserEmail());
            pstmt.executeUpdate();
        }
    }

    // Validates a user's login credentials.
    //Arnav Lohiya, Feb 12th: I'm making this function return the user's ID as we need it for operations.
    public int login(User user) throws SQLException {
        String query = "SELECT id FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            //pstmt.setString(4, user.getUserFullName());
            //pstmt.setString(5, user.getUserEmail());         
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {  // Check if a result is found
                    return rs.getInt("id");  // Return the user ID
                } else {
                    return -1;  // Return -1 if no user is found
                }
            }
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
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Generates a new invitation code and inserts it into the database.
    public String generateInvitationCode(List<String> selectedRoles) {
        String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
        String rolesString = String.join(",", selectedRoles);
        LocalDateTime expirationTime = LocalDateTime.now().plusHours(24); // Expires in 24 hours
        Timestamp expirationTimestamp = Timestamp.valueOf(expirationTime);
        String query = "INSERT INTO InvitationCodes (code, assignedRoles,  expirationTime, isUsed) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            pstmt.setString(2, rolesString);
            pstmt.setTimestamp(3, expirationTimestamp); // Insert expiration timestamp
            pstmt.setBoolean(4, false); // Ensure the code is unused
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return code;
    }
    
    // Validates an invitation code to check if it is unused.
    public boolean validateInvitationCode(String code) {
        String query = "SELECT isUsed, expirationTime FROM InvitationCodes WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                boolean isUsed = rs.getBoolean("isUsed");
                Timestamp expirationTime = rs.getTimestamp("expirationTime");
                if (isUsed) {
                    System.out.println("Code is already used.");
                    return false;
                }
                if (expirationTime == null ) {
                    System.out.println("Code has expired.");
                    return false;
                }
                // Mark the code as used
                //List<String> invitationRoles = getRolesFromInvitationCode(code);
                //ArrayList<String> rolesArrayList = new ArrayList<>(invitationRoles);
                //updateRolesIntoTable(rolesArrayList, userName);
                markInvitationCodeAsUsed(code);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Marks the invitation code as used in the database.
    private void markInvitationCodeAsUsed(String code) {
         String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ? AND isUsed = FALSE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    //Get roles that were assigned on the invitation Code
    public List<String> getRolesFromInvitationCode(String invitationCode) {
        List<String> roles = new ArrayList<>();
        String query = "SELECT assignedRoles FROM InvitationCodes WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, invitationCode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String rolesString = rs.getString("assignedRoles"); // Retrieve from DB
                if (rolesString != null && !rolesString.isEmpty()) {
                    roles = Arrays.asList(rolesString.split(","));
                } 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }
    
    // ENZO'S ADDED FEATURE: Method to delete a user from the database, ensuring admins cannot delete themselves or other admins
    public boolean deleteUser(String username) throws SQLException {
        if (username.equalsIgnoreCase("admin")) { // Prevent deletion of the admin account
            System.out.println("Cannot delete an admin user.");
            return false;
        }
        // Check if the user being deleted is an admin
        String roleCheckQuery = "SELECT role FROM cse360users WHERE userName = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(roleCheckQuery)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                if (role.equalsIgnoreCase("admin")) {
                    System.out.println("Cannot delete another admin.");
                    return false;
                }
            }
        }
        // Delete user query
        String query = "DELETE FROM cse360users WHERE userName = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Returns true if the user was successfully deleted
        }
    }

    // ENZO'S ADDED FEATURE: Method to retrieve all users from the database, excluding the admin
    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        String query = "SELECT userName FROM cse360users WHERE role != 'admin'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(rs.getString("userName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    //Anthony Hernandez, Feb 4th, implemented admin command to display all users and their information 
    public static List<String> printUsersInformation() {
        ObservableList<String> userInfoListed = FXCollections.observableArrayList();
        String askFor = "SELECT * FROM cse360users";
        try(Connection dataConnect = DriverManager.getConnection(DB_URL,USER, PASS )) {
            PreparedStatement pstmt = dataConnect.prepareStatement(askFor);
            ResultSet inq = pstmt.executeQuery();
            while(inq.next()) {
                // R.McQuesten, 2025-02-04, Commenting out fields that aren't required per user story, rm comment if needed
                String userName = inq.getString("userName");
                //String userID = inq.getString("id");
                //String userPassword = inq.getString("password");
                String userRole = inq.getString("role");
                String userFullName = inq.getString("userFullName");
                //uncomment to see output in terminal 
                //System.out.println("User ID: "  +  userID + ", User Name: " + userName + ", Password: "  + userPassword + ", User's Roles: "  + userRole);
                //userInfoListed.add("Username:	" + userName + "User ID:	" + userID + "User Password:	" + userPassword + "User Role:	" + userRole + "\n");
                userInfoListed.add("Username: " + userName + "\tUser Full Name: " + userFullName + "\tUser Role: " + userRole + "\n");
            }
            inq.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfoListed;
    }

    //Anthony Hernandez, feb 4th, command used by the addRemoveUser class to display current users and their roles 
    public static List<String> printUserAndRoles(){
        ObservableList<String> userInfoListed = FXCollections.observableArrayList();
        String askFor = "SELECT userName, role FROM cse360users";
        try (Connection dataConnect = DriverManager.getConnection(DB_URL,USER, PASS )){
            PreparedStatement pstmt = dataConnect.prepareStatement(askFor);
            ResultSet inq = pstmt.executeQuery();
            while (inq.next()) {
                String userName = inq.getString("userName");
                String userRole = inq.getString("role");
                //uncomment to see output in terminal 
                //System.out.println("User ID: "  +  userID + ", User Name: " + userName + ", Password: "  + userPassword + ", User's Roles: "  + userRole);
                userInfoListed.add("Username:	" + userName + "		User Role:	" + userRole + "\n");      
            }
            inq.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfoListed;
    }

    //Arnav Lohiya Feb 5th: This function is used to update the roles of a user
    //userRoles can be like: ["student", "admin"] or ["admin"] or alike
    public void updateRolesIntoTable(ArrayList<String> userRoles, String username) {
        if(userRoles.size() == 0) {
            return;
        }
        String csvRoles = String.join(",", userRoles);
        String query = "UPDATE cse360users SET role = ? WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, csvRoles);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }
    
    //Arnav Lohiya Feb 5th: This function is used to fetch the roles of a user
    //input is the username
    //output is an ArrayList of roles like this: ["admin"] or ["admin", "student"]
    public ArrayList<String> fetchUserRoles(String username){
        if(username.equals("")) {
            System.out.print("DEBUG: fetchUserRoles has been called with empty string as username\n");
            return new ArrayList<String>();
        }
        //fetch the roles
        String query = "SELECT role FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                String csvRoles = rs.getString("role");
                String[] array = csvRoles.split(",");
                return new ArrayList<>(Arrays.asList(array));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.print("DEBUG: Error in fetchUserRoles. Reached the end without returning any value.");
        return new ArrayList<String>();
    }
    
    //Arnav Lohiya Feb 12th
    //Post a new question
    public boolean postNewQuestion(int ownerUserId, String title, String body) {
        String query = "INSERT INTO questions(ownerUserId, title, body) VALUES (?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, ownerUserId );  // Set the ownerUserId value
            pstmt.setString(2, title);      // Set the title value
            pstmt.setString(3, body);  
            int rowsAffected = pstmt.executeUpdate();  // Get the number of affected rows
            if (rowsAffected > 0) {  // If rows were inserted, return true
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Handle exception if any
        }
        return false; 
    }
    
    //Arnav Lohiya Feb 12th
    //Fetch All the questions 
    public ArrayList<Question> fetchQuestionList() {
        String query = "SELECT "
                + "questions.questionId,questions.ownerUserId, questions.title, questions.body, "
                + "cse360users.userFullName "
                + "FROM questions "
                + "JOIN cse360users ON cse360users.id = questions.ownerUserId "
                + "ORDER BY questionId DESC";
        ArrayList<Question> questionList = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            // Iterate through the ResultSet and map to Question objects
            while (rs.next()) {
                int questionId = rs.getInt("questionId");
                int ownerUserId = rs.getInt("ownerUserId");
                String ownerFullName = rs.getString("userFullName");
                String title = rs.getString("title");
                String body = rs.getString("body");
                // Create a new Question object and add to the list
                Question question = new Question(ownerFullName, ownerUserId, title, body, questionId);
                questionList.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questionList;
    }
    
    //Arnav Lohiya Feb 12th
    // Fetch the details of the given questionId
    public Question fetchQuestionDetails(int questionId) {
        if (questionId <= 0) {
            return null;
        }
        String query = "SELECT questions.*, cse360users.userFullName FROM questions JOIN cse360users ON "
                + "cse360users.id = questions.ownerUserId WHERE questionId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("questionId");
                int ownerUserId = rs.getInt("ownerUserId"); 
                String title = rs.getString("title");
                String body = rs.getString("body");
                String userFullName = rs.getString("userFullName");
                return new Question(userFullName, ownerUserId, title, body, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //Arnav Lohiya Feb 12th
    //This function takes in the updated value of a questions title and and body
    //This function also takes in the questionId
    //This function updated the question based on the new title/body.
    public boolean updateQuestion(int questionId, String newTitle, String newBody) {
        String query = "UPDATE questions SET title = ?, body = ? WHERE questionId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newTitle);
            pstmt.setString(2, newBody);
            pstmt.setInt(3, questionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    //Arnav Lohiya Feb 12th
    //This function takes in a questionId and deletes it from the database
    public boolean deleteQuestion(int questionId) {
        String query = "DELETE FROM questions WHERE questionId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Returns true if deletion was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Arnav Lohiya TP2, 2025-02-25
    //This function takes in the required parameters to insert a potential answer into the answers table.
    public boolean postPotentialAnswer(int ownerUserId, int parentQuestionId, String body) {
    	/*
    	 * "ownerUserId INT,"
                + "parentQuestionId INT,"
                + "body VARCHAR(255),"
                + "markedAccepted BOOLEAN DEFAULT FALSE,"
    	 * */
    	String insertAnswer = "INSERT INTO answers (ownerUserId, parentQuestionId, body)  VALUES(?,?,?)";
    	try (PreparedStatement pstmt = connection.prepareStatement(insertAnswer)) {
            pstmt.setInt(1, ownerUserId);
            pstmt.setInt(2, parentQuestionId);
            pstmt.setString(3, body);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
    	
    	
    	return false;
    }
    
    //Arnav Lohiya TP2, 2025-02-25
    //This function takes a question's questionId and returns the list of anwers posted for that question.
    //The order in which answers are being returned is the following:
    //1. The answers marked resolved
    //2. If multiple answers are resolved, the one which was post most recently will be first.
    public ArrayList<Answer> fetchAnswers(int questionId){
    	if(questionId <=0) {
    		return null;
    	}
    	String query = "SELECT answers.parentQuestionId, answers.answerId, answers.ownerUserId ,answers.body, answers.markedAccepted, cse360users.userFullName FROM answers JOIN cse360users ON cse360users.id = answers.ownerUserId WHERE parentQuestionId = ? ORDER BY answers.markedAccepted DESC, answerId DESC;";
    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            ArrayList<Answer> answersList = new ArrayList<>();
            while(rs.next()){
                String answerBody = rs.getString("body");
                boolean markedAccepted = rs.getBoolean("markedAccepted");
                String userFullName = rs.getString("userFullName");
                int answerId = rs.getInt("answerId");
                int ownerUserId = rs.getInt("ownerUserId");
                int questionId1 = rs.getInt("parentQuestionId");

                
                Answer temp = new Answer(answerId, ownerUserId, questionId1,answerBody, userFullName, markedAccepted);
                answersList.add(temp);
                
            }
            return answersList;
        } catch(SQLException e){
            e.printStackTrace();
        }
		return null;
    }
    
    //Arnav Lohiya TP2- 2025-02-26, 
    public boolean updateAnswerStatus(int answerId, boolean newStatus) {
    	if(answerId <=0) {
    		return false;
    	}
    	 String query = "UPDATE answers SET markedAccepted = ? WHERE answerId = ?";
         try (PreparedStatement pstmt = connection.prepareStatement(query)) {
             pstmt.setBoolean(1, newStatus);
             pstmt.setInt(2, answerId);
             return pstmt.executeUpdate() > 0;
         } catch (SQLException e) {
             e.printStackTrace();
         }
         return false;
    }
    
    // R.McQuesten-TP2, 2025-02-21, Handle sending private message feedback
    public boolean sendPM(int senderUserId, int recipientUserId, int questionId, String message) {
        String query = "INSERT INTO feedback (senderUserId, recipientUserId, questionId, message) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, senderUserId);
            pstmt.setInt(2, recipientUserId);
            pstmt.setInt(3, questionId);
            pstmt.setString(4, message);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // R.McQuesten-TP2, 2025-02-21, Handle getting private message feedback
    public ArrayList<String> getPM(int recipientUserId) {
        String query = "SELECT f.message, f.questionId, q.title, u.userFullName FROM feedback f " +
                       "JOIN questions q ON f.questionId = q.questionId " +
                       "JOIN cse360users u ON f.senderUserId = u.id " +
                       "WHERE f.recipientUserId = ?";
        ArrayList<String> pMsgs = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, recipientUserId);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                String questionTitle = rs.getString("title");
                String message = rs.getString("message");
                String senderName = rs.getString("userFullName");
                pMsgs.add("For question \"" + questionTitle + "\" - From " + senderName + ": " + message);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return pMsgs;
    }
    
    // R.McQuesten-TP1, 2025-02-05, Generates OTP for (forgetful) user
    public String getOneTimePassword(String userName) {
        String otp = UUID.randomUUID().toString().substring(0, 4); // Instantiate rand 4 char OTP
        String query = "UPDATE cse360users SET oneTimePassword = ? WHERE userName = ?"; // Added 2 clauses to query
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1,  otp);
            pstmt.setString(2,  userName);
            pstmt.executeUpdate();
            return otp;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;        
    }
    
    // R.McQuesten-TP1, 2025-02-05, OTP validation
    public boolean validateOneTimePassword(String userName, String otp) {
        String query = "SELECT * FROM cse360users WHERE userName = ? AND oneTimePassword = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, otp);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // R.McQuesten-TP1, 2025-02-05,
    // Ensure OTP is only usable once by removing OTP after each use
    public void destroyOneTimePassword(String userName) {
        String query = "UPDATE cse360users SET oneTimePassword = NULL WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1,  userName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }   
    
    // R.McQuesten-TP1, 2025-02-05,
    // Add get helper method such
    // That others can use db conn
    public Connection getConnection() {
        return connection;
    }

    //Anthony Hernandez. Feb 6th, made a function to get roles of all users without any input needed 
    public static List<String> fetchAllUsersAndInformation() {
        ObservableList<String> allUserInfoCollected = FXCollections.observableArrayList();
        String query = "SELECT * FROM cse360users";
        try(Connection dataConnect = DriverManager.getConnection(DB_URL,USER, PASS )) {
            PreparedStatement pstmt = dataConnect.prepareStatement(query);
            ResultSet info = pstmt.executeQuery();
            while(info.next()) {
                String userName = info.getString("userName");
                String csvRoles = info.getString("role");
                String fullName = info.getString("userFullName");
                String userEmail = info.getString("userEmail");
                //userfull name 
                //email 
                String [] array = csvRoles.split(", ");
                ArrayList<String> formattedList = new ArrayList<>(Arrays.asList(array));
                String formatted = "" + String.join(", " , formattedList);
                //allUserInfoCollected.add("Username:	" + userName + " " + formatted);
                allUserInfoCollected.add("Username:  " + userName + "	Email: " + userEmail + "	Full Name: " + fullName + "	Roles: " + formatted);
            }
            }catch (SQLException e) {
            e.printStackTrace();
        }
        return allUserInfoCollected;
    }

    //Anthony Hernandez. Feb 6th, made a function to get roles of all users without any input needed 
    public static List<String> fetchAllUsersAndRoles() {
        ObservableList<String> allUserInfoCollected = FXCollections.observableArrayList();
        String query = "SELECT userName, role FROM cse360users";
        try(Connection dataConnect = DriverManager.getConnection(DB_URL,USER, PASS )) {
            PreparedStatement pstmt = dataConnect.prepareStatement(query);
            ResultSet info = pstmt.executeQuery();
            while(info.next()) {
                String userName = info.getString("userName");
                String csvRoles = info.getString("role");
                String [] array = csvRoles.split(", ");
                ArrayList<String> formattedList = new ArrayList<>(Arrays.asList(array));
                String formatted = "User Roles: " + String.join(", " , formattedList);
                allUserInfoCollected.add("Username:	" + userName + " " + formatted);
            }
        }catch (SQLException e) {
                e.printStackTrace();
        }
        return allUserInfoCollected;
    }

    public static boolean atleastOneAdmin(String role) {
        String inq = "SELECT COUNT(*) FROM cse360users WHERE role = ?";
        try(Connection dataConnect = DriverManager.getConnection(DB_URL,USER, PASS )){
            PreparedStatement pstmt = dataConnect.prepareStatement(inq);
            pstmt.setString(1, role);
            try(ResultSet info = pstmt.executeQuery()){
                if(info.next()) {
                    return info.getInt(1) >= 2; 
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Closes the database connection and statement.
    public void closeConnection() {
        try { 
            if(statement != null) statement.close(); 
        } catch(SQLException se2) { 
            se2.printStackTrace();
        } 
        try { 
            if(connection != null) connection.close(); 
        } catch(SQLException se){ 
            se.printStackTrace(); 
        } 
    }
}
