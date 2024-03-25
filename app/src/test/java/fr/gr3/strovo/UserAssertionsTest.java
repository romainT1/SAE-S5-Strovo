package fr.gr3.strovo;

import org.junit.Assert;
import org.junit.Test;

import fr.gr3.strovo.utils.UserAssertions;

/** Tests de la classe {@link UserAssertions} */
public class UserAssertionsTest {

    @Test
    public void isFirstnameValidTest() {
        Assert.assertTrue(UserAssertions.isFirstnameValid(generateString('t', UserAssertions.FIRSTNAME_MIN)));
        Assert.assertTrue(UserAssertions.isFirstnameValid(generateString('t', UserAssertions.FIRSTNAME_MAX)));

        Assert.assertFalse(UserAssertions.isFirstnameValid(null));
        Assert.assertFalse(UserAssertions.isFirstnameValid("    "));
        Assert.assertFalse(UserAssertions.isFirstnameValid(generateString('t', UserAssertions.FIRSTNAME_MIN-1)));
        Assert.assertFalse(UserAssertions.isFirstnameValid(generateString('t', UserAssertions.FIRSTNAME_MAX+1)));
    }

    @Test
    public void isLastnameValidTest() {
        Assert.assertTrue(UserAssertions.isLastnameValid(generateString('t', UserAssertions.LASTNAME_MIN)));
        Assert.assertTrue(UserAssertions.isLastnameValid(generateString('t', UserAssertions.LASTNAME_MAX)));

        Assert.assertFalse(UserAssertions.isLastnameValid(null));
        Assert.assertFalse(UserAssertions.isLastnameValid("    "));
        Assert.assertFalse(UserAssertions.isLastnameValid(generateString('t', UserAssertions.LASTNAME_MAX+1)));
        Assert.assertFalse(UserAssertions.isLastnameValid(generateString('t', UserAssertions.LASTNAME_MIN-1)));
    }

    @Test
    public void isEmailValidTest() {
        Assert.assertTrue(UserAssertions.isEmailValid("test@iut-rodez.fr"));
        Assert.assertTrue(UserAssertions.isEmailValid("t@t.fr"));

        Assert.assertFalse(UserAssertions.isEmailValid("test@iut-rodez .fr"));
        Assert.assertFalse(UserAssertions.isEmailValid("test@ iut-rodez.fr"));
        Assert.assertFalse(UserAssertions.isEmailValid("testiut-rodez.fr"));
    }

    @Test
    public void isPasswordValidTest() {
        Assert.assertTrue(UserAssertions.isPasswordValid(generateString('t', UserAssertions.PASSWORD_MIN)));
        Assert.assertTrue(UserAssertions.isPasswordValid(generateString('t', UserAssertions.PASSWORD_MAX)));

        Assert.assertFalse(UserAssertions.isPasswordValid(null));
        Assert.assertFalse(UserAssertions.isPasswordValid(generateString('t', UserAssertions.PASSWORD_MAX+1)));
        Assert.assertFalse(UserAssertions.isPasswordValid(generateString('t', UserAssertions.PASSWORD_MIN-1)));
    }

    private String generateString(char c, int nombre) {
        String finalString = "";
        for (int i = 0; i < nombre ; i++) {
            finalString += c;
        }
        return finalString;
    }
}
