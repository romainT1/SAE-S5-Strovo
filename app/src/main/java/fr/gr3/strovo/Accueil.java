package fr.gr3.strovo;

import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.app.Dialog;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant l'activité principale de l'application, correspondant à l'écran d'accueil.
 * Cette activité affiche une liste de parcours et permet aux utilisateurs de rechercher, filtrer,
 * et interagir avec les parcours affichés.
 */
public class Accueil extends AppCompatActivity {

    /** URL de l'API pour récupérer la liste des parcours de l'utilisateur */
    private final String URL_LISTE_PARCOURS = "http://172.20.10.14:8080/parcours/utilisateur/%d";

    /** Composant graphique de la recherche */
    private SearchView rechercheNom;

    /** Composant graphique du bouton de filtre */
    private Button filterButton;

    /** Composant graphique de la liste des parcours */
    private ListView listViewParcours;

    /** Composant graphique du bouton qui lance un enregistrement de parcours */
    private Button lancerParcoursButton;


    /** Liste des parcours de l'utilisateur */
    private List<Parcours> parcoursList;

    /** Adaptateur pour la liste des parcours */
    private ParcoursAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        // Affectation des composants graphiques
        rechercheNom = findViewById(R.id.search_view);
        filterButton = findViewById(R.id.filter_button);
        listViewParcours = findViewById(R.id.list_view);
        lancerParcoursButton = findViewById(R.id.floating_action_button);

        parcoursList = new ArrayList<>();

        // Creation d'un adaptateur personnalisé
        adapter = new ParcoursAdapter(this, R.layout.vue_item_liste, parcoursList);
        listViewParcours.setAdapter(adapter);

        // Appelle la méthode pour récupérer les données de l'API
        fetchParcoursFromApi();

        // Configuration de l'écouteur de la barre de recherche
        rechercheNom.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String nameParcours) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Gérez le changement de texte de recherche ici
                return false;
            }
        });

        // Configuration de l'écouteur du bouton de filtre
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crée une instance de Dialog
                final Dialog dialog = new Dialog(Accueil.this);

                // Définis le contenu de la fenêtre contextuelle
                dialog.setContentView(R.layout.popup_filtre);

                // Récupère les éléments de la fenêtre contextuelle
                EditText inputMois = dialog.findViewById(R.id.inputMois);
                EditText inputDureeMin = dialog.findViewById(R.id.inputDureeMin);
                EditText inputDureeMax = dialog.findViewById(R.id.inputDureeMax);

                Button rechercher = dialog.findViewById(R.id.btnRechercher);
                Button annuler = dialog.findViewById(R.id.btnAnnuler);

                // Gère le clic sur le bouton "rechercher"
                rechercher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO faire le lien a l'api
                        dialog.dismiss();
                    }
                });

                // Gère le clic sur le bouton "Annuler"
                annuler.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // Affiche la fenêtre contextuelle
                dialog.show();
            }

        });

        // Configuration de l'écouteur du clic sur un élément de la liste
        listViewParcours.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Gère le clic sur un élément de la liste
            }
        });

        // Configuration de l'écouteur du clic sur le bouton d'action flottant
        lancerParcoursButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créé une instance de Dialog
                final Dialog dialog = new Dialog(Accueil.this);

                // Définis le contenu de la fenêtre contextuelle
                dialog.setContentView(R.layout.popup_lancer_course);

                // Récupére les éléments de la fenêtre contextuelle
                EditText inputCommentaire = dialog.findViewById(R.id.inputCommentaire);
                Button confirmer = dialog.findViewById(R.id.confirmer);
                Button annuler = dialog.findViewById(R.id.annuler);

                // Gère le clic sur le bouton "Confirmer"
                confirmer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Accueil.this, MapActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                // Gère le clic sur le bouton "Annuler"
                annuler.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // Affiche la fenêtre contextuelle
                dialog.show();
            }
        });
    }

    /**
     * Récupère les données des parcours depuis l'API en utilisant la bibliothèque Volley.
     */
    private void fetchParcoursFromApi() {
        // TODO Récupérer l'identifiant de l'utilisateur
        int userId = 1;
        String.format(URL_LISTE_PARCOURS, userId);
        String apiUrl = "http://172.20.10.14:8080/parcours/utilisateur/1";

        // Utilisation de la bibliothèque Volley pour effectuer la requête HTTP
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                apiUrl,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseJsonResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("ERREUR : " + error.getMessage());
                    }
                });

        // Ajoute la requête à la file d'attente
        queue.add(jsonArrayRequest);
    }

    /**
     * Analyse la réponse JSON de l'API et peuple la liste des parcours.
     * @param response Un objet de type JSONArray
     */
    private void parseJsonResponse(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject parcoursJson = response.getJSONObject(i);
                Parcours parcours = new Parcours(parcoursJson.getString("name"),
                                                 parcoursJson.getString("date"));
                parcoursList.add(parcours);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

