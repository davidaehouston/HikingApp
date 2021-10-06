package data;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UserTest {

    User testUser;
    String firstNameValid, lastNameValid, emailValid, passwordValid;

    @Before
    public void setUp() throws Exception {

        testUser = new User();
        firstNameValid = "validFirstName";
        lastNameValid = "validLastName";
        emailValid = "validEmail@gmail.com";
        passwordValid = "validPassword456";


    }

    @Test
    public void testConstructorNoArgs() {
        testUser = new User();
        assertNotNull(testUser);
    }

    @Test
    public void testConstructorWithArgs() {
        testUser = new User(firstNameValid, lastNameValid, emailValid);
        assertEquals(firstNameValid, testUser.getUserFirstName());
        assertEquals(lastNameValid, testUser.getUserLastName());
        assertEquals(emailValid, testUser.getUserEmail());
    }

    @Test
    public void getSetUserFirstName() {
        testUser = new User();
        testUser.setUserFirstName(firstNameValid);
        assertEquals(firstNameValid, testUser.getUserFirstName());
    }

    @Test
    public void getSetUserLastName() {
        testUser = new User();
        testUser.setUserLastName(lastNameValid);
        assertEquals(lastNameValid, testUser.getUserLastName());
    }

    @Test
    public void getSetUserEmail() {
        testUser = new User();
        testUser.setUserEmail(emailValid);
        assertEquals(emailValid, testUser.getUserEmail());
    }

}