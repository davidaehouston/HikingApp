package data;

import java.util.List;

/**
 * User class handles creation of user objects.
 */
public class User {

    //assign variables
    private String userFirstName;
    private String userLastName;
    private String userEmail;


    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * Constructor with arguments.
     * @param userFirstName
     * @param userLastName
     * @param userEmail
     */
    public User(String userFirstName, String userLastName, String userEmail) {
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;

    }

    /**
     * Gets and returns the users first name
     * @return
     */
    public String getUserFirstName() {
        return userFirstName;
    }

    /**
     * Sets the users first name
     * @param userFirstName
     */
    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    /**
     * Get and returns the users last name
     * @return
     */
    public String getUserLastName() {
        return userLastName;
    }

    /**
     * Sets the users last name
     * @param userLastName
     */
    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    /**
     * Gets and returns the users Email
     * @return
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Sets the users email
     * @param userEmail
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * Displays the user information in logs for example.
     * @return
     */
    @Override
    public String toString() {
        return "User{" +
                "userFirstName='" + userFirstName + '\'' +
                ", userLastName='" + userLastName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
