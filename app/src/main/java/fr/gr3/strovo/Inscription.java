package fr.gr3.strovo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.security.NoSuchAlgorithmException;

import fr.gr3.strovo.api.StrovoApi;
import fr.gr3.strovo.api.model.User;

/**
 * Activité inscription.
 */
public class Inscription extends AppCompatActivity {

    /** Champ de saisie du prénom */
    private EditText firstname;

    /** Champ de saisie du nom */
    private EditText lastname;

    /** Champ de saisie de l'email */
    private EditText email;

    /** Champ de saisie mot de passe */
    private EditText password;

    /** Queue pour effectuer la requête HTTP */
    private RequestQueue requestQueue;

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

        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * Exécuté quand l'utilisateur clique sur le bouton d'inscription.
     * @param view vue
     */
    public void clicInscrire(View view) throws NoSuchAlgorithmException {

        // Récupération des informations de l'utilisateur
        User user = new User(email.getText().toString(), password.getText().toString(),
                firstname.getText().toString(), lastname.getText().toString());

        // Vérification de la validité du prénom
        if (!UserAssertions.isFirstnameValid(user.getFirstname())) {
            showError(String.format(getString(R.string.errInscriptionFirstname),
                            UserAssertions.FIRSTNAME_MIN, UserAssertions.FIRSTNAME_MAX));
        }
        // Vérification de la validité du nom
        else if (!UserAssertions.isLastnameValid(user.getLastname())) {
            showError(String.format(getString(R.string.errInscriptionLastname),
                            UserAssertions.LASTNAME_MIN, UserAssertions.LASTNAME_MAX));
        }
        // Vérification de la validité de l'adresse mail
        else if (!UserAssertions.isEmailValid(user.getEmail())) {
            showError(String.format(getString(R.string.errInscriptionEmail),
                            UserAssertions.EMAIL_MAX));
        }
        // Vérification de la validité du mot de passe
        else if (!UserAssertions.isPasswordValid(user.getPassword())) {
            showError(String.format(getString(R.string.errInscriptionPassword),
                            UserAssertions.PASSWORD_MIN, UserAssertions.PASSWORD_MAX));
        }
        // Informations valide, inscription de l'utilisateur
        else {
            // Hash le mot de passe de l'utilisateur
            String hashedPassword = PasswordHasher.hashPassword(user.getPassword());
            user.setPassword(hashedPassword);

            // Enregistre l'utilisateur à l'api
            register(user);
        }
    }

    /** Crée un toast pour afficher l'erreur. */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Exécuté quand l'utilisateur clique sur le bouton retour.
     * @param view
     */
    public void clicRetour(View view) {
        finish(); // Termine l'activité inscription
    }

    /**
     * Envoie une requête d'inscription à l'API et redirige sur l'activité principale
     * une fois l'utilisateur enregistré.
     * @param user utilisateur à enregistrer
     */
    private void register(User user) {
        try {
            JsonObjectRequest request = StrovoApi.getInstance().registerUser(user,
                    onInscriptionSuccess(), onInscriptionError());
            requestQueue.add(request);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Exécuté lorsque l'inscription de l'utilisateur est réussie.
     * @return un objet Response.Listener
     */
    private Response.Listener onInscriptionSuccess() {
        return response -> {
            Toast.makeText(this, "Compte créé avec succès !", Toast.LENGTH_LONG).show();
            finish(); // Termine l'activité inscription
        };
    }

    /**
     * Exécuté lorsque l'inscription de l'utilisateur en echec.
     * @return un objet Response.ErrorListener
     */
    private Response.ErrorListener onInscriptionError() {
        return error -> {
            String messageErreur = getString(R.string.err);

            // Si erreur liée à un problème de conflit
            if (error.networkResponse != null
                && error.networkResponse.statusCode == 409) {
                    messageErreur = getString(R.string.errInscriptionConflict);
            }
            Toast.makeText(this, messageErreur, Toast.LENGTH_LONG).show();
            Log.d("Erreur inscription", error.getMessage());
        };
    }
}