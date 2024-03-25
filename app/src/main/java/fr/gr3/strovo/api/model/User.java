package fr.gr3.strovo.api.model;

import org.json.JSONException;
import org.json.JSONObject;

/** Représente un utilisateur. */
public class User {

    /** Adresse mail de l'utilisateur */
    private String email;

    /** Mot de passe de l'utilisateur */
    private String password;

    /** Prénom de l'utilisateur */
    private String firstname;

    /** Nom de l'utilisateur */
    private String lastname;

    /**
     * Crée un utilisateur.
     * @param email adresse mail
     * @param password mot de passe
     * @param firstname prénom
     * @param lastname nom
     */
    public User(String email, String password, String firstname, String lastname) {
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        jsonObject.put("firstname", firstname);
        jsonObject.put("lastname", lastname);

        return jsonObject;
    }
}
