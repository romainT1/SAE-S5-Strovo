package fr.gr3.strovo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText motDePasse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        email = findViewById(R.id.email);
        motDePasse = findViewById(R.id.mot_de_passe);
    }

    public void clicBoutonConnexion(View view) {
        // Vérifier l'authentification
        VerificationConnexion();

        // création d'une intention pour demander lancement de l'activité accueil
        Intent intention = new Intent(MainActivity.this, Accueil.class);
        // lancement de l'activité accueil via l'intention préalablement créée
        startActivity(intention);
    }

    public void clicBoutonInscription(View view) {
        // création d'une intention pour demander lancement de l'activité inscription
        Intent intention = new Intent(MainActivity.this, Inscription.class);
        // lancement de l'activité accueil via l'intention préalablement créée
        startActivity(intention);
    }

    /*
     * Vérifie les informations d'authentification saisie par l'utilisateur
     */
    private void VerificationConnexion() {
        String identifiant = email.getText().toString();
        String mdp = motDePasse.getText().toString();
        // Appel à l'API pour vérifier les informations d'authentification
        // TODO : faire appel à l'API
    }

}