package fr.gr3.strovo;

import androidx.appcompat.widget.SearchView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    /** Composant graphique du choix de la date dans le filtre */
    DatePickerDialog picker;


    /** Liste des parcours de l'utilisateur */
    private List<Parcours> parcoursList;

    /** Adaptateur pour la liste des parcours */
    private ParcoursAdapter adapter;

    /** Identifiant de l'utilisateur */
    private int userId;

    /** Nom du parcours */
    private String nameParcours;

    /** Intervalle des dates des parcours */
    private Date[] dateIntervalle;

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

        // Récupération des informations de recherche de parcours
        //TODO : récupérer l'id de l'utilisateur et autres avec les préférences
        userId = 1;
        nameParcours = null;
        dateIntervalle = null;

        // Appelle la méthode pour récupérer les données de l'API
        fetchParcoursFromApi(userId, nameParcours, dateIntervalle);

        // Configuration de l'écouteur de la barre de recherche
        rechercheNom.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String rechercheNomParcours) {
                rechercheNomParcours = rechercheNomParcours.replace(" ", "+");
                fetchParcoursFromApi(userId, rechercheNomParcours, dateIntervalle);
                return true;
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
                EditText inputDateMin = dialog.findViewById(R.id.inputDureeMin);
                EditText inputDateMax = dialog.findViewById(R.id.inputDureeMax);

                inputDateMin.setInputType(InputType.TYPE_NULL);
                inputDateMin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCalendar(inputDateMin);
                    }
                });

                inputDateMax.setInputType(InputType.TYPE_NULL);
                inputDateMax.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCalendar(inputDateMax);
                    }
                });

                Button rechercher = dialog.findViewById(R.id.btnRechercher);
                Button annuler = dialog.findViewById(R.id.btnAnnuler);

                // Gère le clic sur le bouton "rechercher"
                rechercher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date dateMin = simpleDateFormat.parse(inputDateMin.getText().toString());
                            Date dateMax = simpleDateFormat.parse(inputDateMax.getText().toString());
                            Date[] dateFilter = {dateMin, dateMax};

                            parcoursList.clear();
                            adapter.notifyDataSetChanged();
                            fetchParcoursFromApi(userId, nameParcours, dateFilter);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

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
     * @param userId Identifiant de l'utilisateur
     * @param nameParcours Nom du parcours à rechercher
     * @param dateIntervalle Intervalle de date dont on veut les parcours
     */
    private void fetchParcoursFromApi(int userId, String nameParcours, Date[] dateIntervalle) {
        parcoursList.clear();
        String urlModifie = URL_LISTE_PARCOURS;
        String apiUrl = String.format(urlModifie, userId);

        if (nameParcours != null && !nameParcours.equals("")) {
            apiUrl += "?nom=%s";
            apiUrl = String.format(apiUrl, nameParcours);
        }

        if (dateIntervalle != null) {
            apiUrl += "?dateDebut=%s&dateFin=%s";
            apiUrl = String.format(apiUrl, dateIntervalle[0].getTime(), dateIntervalle[1].getTime());
        }

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
        parcoursList.clear();

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject parcoursJson = response.getJSONObject(i);
                Parcours parcours = new Parcours(parcoursJson.getString("name"),
                                                 parcoursJson.getString("date"));
                //parcoursList.add(parcours);
                adapter.add(parcours);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Affiche un calendrier lors du clic sur un editText.
     * @param editText edit text sur lequel on veut afficher le calendrier.
     */
    private void showCalendar(EditText editText) {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(Accueil.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        picker.show();
    }
}

