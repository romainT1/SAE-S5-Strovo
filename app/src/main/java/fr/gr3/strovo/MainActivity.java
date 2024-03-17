package fr.gr3.strovo;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;


import java.security.NoSuchAlgorithmException;


import fr.gr3.strovo.api.Endpoints;
import fr.gr3.strovo.utils.Keys;


/**
 * Activité principale, page de connexion.
 */
public class MainActivity extends AppCompatActivity {

    /** gestionnaire de préférences */
    private SharedPreferences preferences;

    /** Champ de saisie de l'adresse mail*/
    private EditText email;


    /** Champ de saisie du mot de passe */
    private EditText motDePasse;


    /** Queue pour effectuer la requête HTTP */
    private RequestQueue requestQueue;


    /**
     * Exécuté lors de la création de l'activité.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        // Charge le token depuis les préférences
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = preferences.getString(Keys.TOKEN_KEY, null);
        // Si un token existe on redirige vers accueil
        if (token != null) {
            switchToAccueil(token);
        }

        email = findViewById(R.id.email);
        motDePasse = findViewById(R.id.mot_de_passe);

        requestQueue = Volley.newRequestQueue(this);
    }


    /**
     * Exécuté quand l'utilisateur clique sur le bouton de connexion.
     * @param view vue
     */
    public void clicConnexion(View view) throws NoSuchAlgorithmException {
        String emailValue = email.getText().toString();
        String passwordValue = motDePasse.getText().toString();
        connexion(emailValue, passwordValue);
    }


    /**
     * Exécuté quand l'utilisateur clique sur le bouton d'inscription.
     * @param view
     */
    public void clicInscription(View view) {
        // création d'une intention pour demander lancement de l'activité inscription
        Intent intention = new Intent(MainActivity.this, Inscription.class);
        // lancement de l'activité accueil via l'intention préalablement créée
        startActivity(intention);
    }


    /**
     * Envoie une requête de connexion à l'API et enregistre le token reçu
     * dans les préférences.
     * @param email
     * @param password
     */
    private void connexion(String email, String password) throws NoSuchAlgorithmException {
        String apiUrl = String.format(Endpoints.LOGIN_URL, email, PasswordHasher.hashPassword(password));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, apiUrl, null,
                response -> {
                    try {
                        String token = response.getString("value");
                        // Enregistre le token dans les préférences
                        preferences.edit().putString(Keys.TOKEN_KEY, token).apply();
                        switchToAccueil(token);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    int messageErreur = R.string.err;

                    /* Si erreur liée à un problème de permission */
                    if (error.networkResponse != null ) {
                        if (error.networkResponse.statusCode == 403) {
                            messageErreur = R.string.errConnexion;
                        }
                    }
                    Toast.makeText(MainActivity.this, messageErreur, Toast.LENGTH_LONG).show();
                    Log.d("Erreur connexion", error.getMessage());
                });
        // Ajoute la requête de suppression à la file d'attente des requêtes HTTP
        requestQueue.add(jsonObjectRequest);
    }


    /**
     * Lance l'intention accueil.
     * @param token valeur du token à transmettre à l'activité
     */
    private void switchToAccueil(String token) {
        // création d'une intention pour demander lancement de l'activité accueil
        Intent intention = new Intent(MainActivity.this, Accueil.class);
        intention.putExtra(Keys.TOKEN_KEY, token);
        // lancement de l'activité accueil via l'intention préalablement créée
        startActivity(intention);
    }
}

