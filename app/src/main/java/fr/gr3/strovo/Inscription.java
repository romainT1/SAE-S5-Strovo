package fr.gr3.strovo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Inscription extends AppCompatActivity {

    private EditText saisiePrenom;
    private EditText saisieNom;
    private EditText saisieEmail;
    private EditText saisieMotDePasse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        saisiePrenom = findViewById(R.id.prenom);
        saisieNom = findViewById(R.id.nom);
        saisieMotDePasse = findViewById(R.id.mot_de_passe_inscription);
        saisieEmail = findViewById(R.id.email_inscription);
    }

    public void clicBoutonInscrire(View view) {
        // création d'une intention pour demander lancement de l'activité accueil
        Intent intention = new Intent(Inscription.this, MainActivity.class);
        // lancement de l'activité accueil via l'intention préalablement créée
        startActivity(intention);

        // Récupération des informations de l'utilisateur
        String prenom = saisiePrenom.getText().toString();
        String nom = saisieNom.getText().toString();
        String motDePasse = saisieMotDePasse.getText().toString();
        String email = saisieEmail.getText().toString();

        // Appel API pour ajouter un utilisateur
        // TODO : Ajouter un utilisateur avec l'API
    }

    public void clicBoutonRetour(View view) {
        // création d'une intention pour demander lancement de l'activité accueil
        Intent intention = new Intent(Inscription.this, MainActivity.class);
        // lancement de l'activité accueil via l'intention préalablement créée
        startActivity(intention);
    }
}
