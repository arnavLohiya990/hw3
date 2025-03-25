package application;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
    // R.McQuesten, 2025-02-04, Add data members for new account info fields
    private String userName;
    private String password;
    private String role;
    private String userFullName;
    private String userEmail;
    private int userId;

    // Constructor to initialize a new User object with userName, password, and role.
    // R.McQuesten, 2025-02-04, Add new account fields to constructor
    public User(String userName, String password, String role, String userFullName, String userEmail) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.userFullName = userFullName;
        this.userEmail = userEmail;
    }
    
    // Arnav Lohiya Feb 12th, added the userId parameter
    public User(int userId,String userName, String password, String role, String userFullName, String userEmail) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.userFullName = userFullName;
        this.userEmail = userEmail;
        this.userId = userId;
    }
    
    // Sets the role of the user.
    public void setRole(String role) {
    	this.role=role;
    }

    // R.McQuesten, 2025-02-04, Add getters for new account info fields
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getUserFullName() { return userFullName; }
    public String getUserEmail() { return userEmail; }
    //Arnav Lohiya Feb 12th
    public int getUserId() { return userId; }
    public void setUserId(int userId) {this.userId = userId;}
}
