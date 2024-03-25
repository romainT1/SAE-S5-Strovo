package fr.gr3.strovo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Représente un utilisateur */
public class UserAssertions {

    /** Longueur minimale prénom */
    public static final int FIRSTNAME_MIN = 2;

    /** Longueur maximale prénom */
    public static final int FIRSTNAME_MAX = 15;

    /** Longueur minimale nom */
    public static final int LASTNAME_MIN = 2;

    /** Longueur maximale nom */
    public static final int LASTNAME_MAX = 15;

    /** Longueur minimale adresse mail */
    public static final int EMAIL_MIN = 4;

    /** Longueur maximale adresse mail */
    public static final int EMAIL_MAX = 32;

    /** Longueur minimale mot de passe */
    public static final int PASSWORD_MIN = 8;

    /** Longueur maximale mot de passe */
    public static final int PASSWORD_MAX = 64;

    /**
     * Vérifie que la longueur d'une chaine de caractère est valide et qu'elle n'est pas
     * uniquement constituée d'espaces.
     * @return true si valide, false sinon
     */
    private static boolean isNotEmptyAndSizeValid(String string, int minLenght, int maxLenght) {
        if (string == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(String.format("^(?!\\s*$).{%d,%d}$", minLenght, maxLenght));
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }

    /**
     * Vérifie la validité d'un prénom. Un prénom est valide si
     * <ul>
     *     <li>il n'est pas null</li>
     *     <li>sa longueur >= FIRSTNAME_MIN</li>
     *     <li>il n'est pas constitué uniquement d'espaces</li>
     *     <li>sa longueur <= FIRSTNAME_MAX</li>
     * </ul>
     * @param firstname prénom
     * @return true si valide, false sinon
     */
    public static boolean isFirstnameValid(String firstname) {
        return isNotEmptyAndSizeValid(firstname, FIRSTNAME_MIN, FIRSTNAME_MAX);
    }

    /**
     * Vérifie la validité d'un nom. Un nom est valide si
     * <ul>
     *     <li>il n'est pas null</li>
     *     <li>sa longueur >= LASTNAME_MIN</li>
     *     <li>il n'est pas constitué uniquement d'espaces</li>
     *     <li>sa longueur <= LASTNAME_MAX</li>
     * </ul>
     * @param lastname nom
     * @return true si valide, false sinon
     */
    public static boolean isLastnameValid(String lastname) {
        return isNotEmptyAndSizeValid(lastname, LASTNAME_MIN, LASTNAME_MAX);

    }

    /**
     * Vérifie la validité d'une adresse mail. Une adresse mail est valide si
     * <ul>
     *     <li>elle n'est pas null</li>
     *     <li>sa longueur >= EMAIL_MIN</li>
     *     <li>elle n'est pas constituée uniquement d'espaces</li>
     *     <li>sa longueur <= EMAIL_MAX</li>
     * </ul>
     * @param email adresse mail à vérifier
     * @return true si adresse mail valide, false sinon
     */
    public static boolean isEmailValid(String email) {
        isNotEmptyAndSizeValid(email, EMAIL_MIN, EMAIL_MAX);
        Pattern patternEmail = Pattern.compile("^[^\\s@]{1,64}@[^\\s@]{1,64}\\.[a-zA-Z]{2,5}$");
        Matcher matcher = patternEmail.matcher(email);
        return matcher.find();
    }

    /**
     * Vérifie la validité d'un mot de passe. Un mot de passe est valide si
     * <ul>
     *     <li>il n'est pas null</li>
     *     <li>sa longueur >= PASSWORD_MIN</li>
     *     <li>il n'est pas constitué uniquement d'espaces</li>
     *     <li>sa longueur <= PASSWORD_MAX</li>
     * </ul>
     * @param password mot de passe
     * @return true si valide, false sinon
     */
    public static boolean isPasswordValid(String password) {
        if (password == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(String.format("^(.){%d,%d}$", PASSWORD_MIN, PASSWORD_MAX));
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }
}
