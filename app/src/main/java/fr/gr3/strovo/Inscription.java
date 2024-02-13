package fr.gr3.strovo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.gr3.strovo.exception.InscriptionException;
import fr.gr3.strovo.model.User;

/**
 * Activité inscription.
 */
public class Inscription extends AppCompatActivity {

    /** Url pour l'inscription */
    private static final String SIGNUP_URL = "http://10.2.14.28:8080/user/signup";

    /** Champ de saisie du prénom */
    private EditText firstname;

    /** Champ de saisie du nom */
    private EditText lastname;

    /** Champ de saisie de l'email */
    private EditText email;

    /** Champ de saisie mot de passe */
    private EditText password;

    /**
     * Exécuté lors de la création de l'activité.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        email = findViewById(R.id.email_inscription);
        password = findViewById(R.id.mot_de_passe_inscription);
        firstname = findViewById(R.id.prenom);
        lastname = findViewById(R.id.nom);
    }

    /**
     * Exécuté quand l'utilisateur clique sur le bouton d'inscription.
     * @param view vue
     */
    public void clicInscrire(View view) {
        // Récupération des informations de l'utilisateur
        // et appel API pour ajouter un utilisateur
        User user = new User(firstname.getText().toString(), lastname.getText().toString(),
                email.getText().toString(), password.getText().toString());

        // Vérification de la validité du prénom
        if (!UserAssertions.isFirstnameValid(user.getFirstname())) {
            Toast.makeText(this,
                    String.format(getText(R.string.errInscriptionFirstname).toString(),
                            UserAssertions.FIRSTNAME_MIN, UserAssertions.FIRSTNAME_MAX),
                    Toast.LENGTH_LONG).show();
        }
        // Vérification de la validité du nom
        else if (!UserAssertions.isLastnameValid(user.getLastname())) {
            Toast.makeText(this,
                    String.format(getText(R.string.errInscriptionLastname).toString(),
                            UserAssertions.LASTNAME_MIN, UserAssertions.LASTNAME_MAX),
                    Toast.LENGTH_LONG).show();
        }
        // Vérification de la validité de l'adresse mail
        else if (!UserAssertions.isEmailValid(user.getEmail())) {
            Toast.makeText(this,
                    String.format(getText(R.string.errInscriptionEmail).toString(),
                            UserAssertions.EMAIL_MAX),
                    Toast.LENGTH_LONG).show();
        }
        // Vérification de la validité du mot de passe
        else if (!UserAssertions.isPasswordValid(user.getPassword())) {
            Toast.makeText(this,
                    String.format(getText(R.string.errInscriptionPassword).toString(),
                            UserAssertions.PASSWORD_MIN, UserAssertions.PASSWORD_MAX),
                    Toast.LENGTH_LONG).show();
        }

        // Informations valide, inscription de l'utilisateur
        else {
            inscription(user);
        }
    }

    /**
     * Exécuté quand l'utilisateur clique sur le bouton retour.
     * @param view
     */
    public void clicRetour(View view) {
        // création d'une intention pour demander lancement de l'activité accueil
        Intent intention = new Intent(Inscription.this, MainActivity.class);
        // lancement de l'activité accueil via l'intention préalablement créée
        startActivity(intention);
    }

    /**
     *
     * @param user
     */
    private void inscription(User user) {
        // TODO requete api

        finish(); // SI reponse ok 201
    }
}
