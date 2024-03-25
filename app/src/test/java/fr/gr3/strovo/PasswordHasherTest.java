package fr.gr3.strovo;

import org.junit.Assert;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import fr.gr3.strovo.utils.PasswordHasher;

public class PasswordHasherTest {

    @Test
    public void passwordHasherTest() throws NoSuchAlgorithmException {
        String password = "MonSuperMoDePasse123";

        Assert.assertNotEquals(password, PasswordHasher.hashPassword(password));
        // Assure que 2 mot de passes identiques hashés ont le même hash
        Assert.assertEquals(PasswordHasher.hashPassword(password), PasswordHasher.hashPassword(password));

    }
}
