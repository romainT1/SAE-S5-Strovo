package fr.gr3.strovo.model;

/** Représente un utilisateur. */
public class User {

    /** Prénom de l'utilisateur */
    private String firstname;

    /** Nom de l'utilisateur */
    private String lastname;

    /** Adresse mail de l'utilisateur */
    private String email;

    /** Mot de passe de l'utilisateur */
    private String password;

    /**
     * Crée un utilisateur.
     * @param firstname prénom
     * @param lastname nom
     * @param email adresse mail
     * @param password mot de passe
     */
    public User(String firstname, String lastname, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
