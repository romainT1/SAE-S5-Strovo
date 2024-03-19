package fr.gr3.strovo;




import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SearchView;


import fr.gr3.strovo.api.model.Parcours;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.app.Dialog;
import android.widget.TextView;
import android.widget.Toast;




import androidx.appcompat.app.AppCompatActivity;




import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;




import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




import fr.gr3.strovo.api.Endpoints;
import fr.gr3.strovo.map.CourseActivity;
import fr.gr3.strovo.map.InterestPoint;
import fr.gr3.strovo.utils.Keys;




/**
 * Classe représentant l'activité principale de l'application, correspondant à l'écran d'accueil.
 * Cette activité affiche une liste de parcours et permet aux utilisateurs de rechercher, filtrer,
 * et interagir avec les parcours affichés.
 */
public class Accueil extends AppCompatActivity {


    /** Composant graphique de la recherche */
    private SearchView rechercheNom;


    /** Copie de parcoursList */
    private List<Parcours> parcoursListOrigine;


    /** Composant graphique du bouton de filtre */
    private Button filterButton;




    /** Composant graphique de la liste des parcours */
    private ListView listViewParcours;




    /** Composant graphique du bouton qui lance un enregistrement de parcours */
    private Button lancerParcoursButton;




    /** Composant graphique du choix de la date dans le filtre */
    private DatePickerDialog picker;




    /** Composant graphique du TextView quand aucun parcours est retourné. */
    private TextView emptyParcoursText;




    /** Liste des parcours de l'utilisateur */
    private List<Parcours> parcoursList;




    /** Adaptateur pour la liste des parcours */
    private ParcoursAdapter adapter;




    /** Nom du parcours */
    private String nameParcours;




    /** Intervalle des dates des parcours */
    private Date[] dateIntervalle;




    /** Queue pour effectuer la requête HTTP */
    private RequestQueue requestQueue;




    /** Handler pour gérer le délai d'appel à l'API */
    private Handler handler = new Handler();




    /** Pour gérer le délai de l'appel à l'API */
    private Runnable runnable;




    /** Handler pour gérer le délai sur le clic du bouton */
    private Handler handlerButton;


    /** Lanceur de l'activité course */
    private ActivityResultLauncher<Intent> courseActivityLauncher;




    /** Définit le status du clic sur le bouton */
    private boolean longClickDetected;


    /** Token de connexion de l'utilisateur */
    private String token;




    /**
     * Méthode appelée lors de la création de l'activité.
     * Initialise les composants graphiques, configure les écouteurs d'événements
     * et effectue les premières actions nécessaires à l'initialisation de l'activité.
     * @param savedInstanceState L'état de l'activité si elle est recréée après une rotation, par exemple.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);


        // Récupère le token
        token = getIntent().getStringExtra(Keys.TOKEN_KEY);


        initializeViews();
        setupEventListeners();


        courseActivityLauncher= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::couseActivityDone);


        try {
            unsentFiles();
        } catch (FileNotFoundException ignored) {
        } catch (Exception ignored) {


        }
    }




    /**
     * Récupère les données des parcours depuis l'API en utilisant la bibliothèque Volley.
     * @param nameParcours Nom du parcours à rechercher
     * @param dateIntervalle Intervalle de date dont on veut les parcours
     */
    private void fetchParcoursFromApi(String nameParcours, Date[] dateIntervalle) {
        //parcoursList.clear();
        String apiUrl = Endpoints.GET_PARCOURS;




        if (nameParcours != null && !nameParcours.equals("")) {
            apiUrl += "?nom=%s";
            apiUrl = String.format(apiUrl, nameParcours);
        }




        if (dateIntervalle != null) {
            apiUrl += "?dateDebut=%s&dateFin=%s";
            apiUrl = String.format(apiUrl, dateIntervalle[0].getTime(), dateIntervalle[1].getTime());
        }




        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                apiUrl,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseJsonResponse(response);




                        // Vérifie si la liste des parcours est vide
                        if (parcoursList.isEmpty()) {
                            emptyParcoursText.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 403) {
                            finish();
                        } else {
                            emptyParcoursText.setVisibility(View.VISIBLE);
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }
        };


        // Ajoute la requête à la file d'attente
        requestQueue.add(jsonArrayRequest);
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
                Parcours parcours = new Parcours(
                        parcoursJson.getString("id"),
                        parcoursJson.getString("name"),
                        parcoursJson.getString("description"),
                        new Date(String.valueOf(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(parcoursJson.getString("date"))))
                );


                adapter.add(parcours);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }


        parcoursListOrigine = new ArrayList<>(parcoursList);
        adapter.notifyDataSetChanged();
    }






    /**
     * Envoie une requête DELETE à l'API pour supprimer un parcours.
     * @param parcours Le parcours à supprimer.
     */
    public void deleteParcoursFromApi(Parcours parcours) {




        // Construit l'URL spécifique pour le parcours à supprimer
        String apiUrl = String.format(Endpoints.DELETE_PARCOURS, parcours.getId());




        // Crée une requête DELETE pour supprimer le parcours de l'API
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, apiUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // Traitement de la réponse en cas de succès
                        Log.d("DELETE Response", response);
                        adapter.remove(parcours);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Traitement de l'erreur
                        Log.e("DELETE Error", error.toString());
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }
        };


        // Ajoute la requête de suppression à la file d'attente des requêtes HTTP
        requestQueue.add(deleteRequest);
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




    /**
     * Initialise les vues de l'activité et configure les écouteurs d'événements.
     */
    private void initializeViews() {
        // Initialisation des composants graphiques
        rechercheNom = findViewById(R.id.search_view);
        filterButton = findViewById(R.id.filter_button);
        listViewParcours = findViewById(R.id.list_view);
        lancerParcoursButton = findViewById(R.id.floating_action_button);
        emptyParcoursText = findViewById(R.id.text_empty_parcours);
        emptyParcoursText.setVisibility(View.INVISIBLE);




        // Initialisation de la liste des parcours
        parcoursList = new ArrayList<>();




        // Création d'un adaptateur personnalisé pour la liste des parcours
        adapter = new ParcoursAdapter(this, R.layout.vue_item_liste, parcoursList);
        listViewParcours.setAdapter(adapter);




        // Initialise la file d'attente des requêtes HTTP avec Volley
        requestQueue = Volley.newRequestQueue(this);
    }




    /**
     * Configure les écouteurs d'événements.
     */
    private void setupEventListeners() {
        // Configuration de l'écouteur de la barre de recherche
        searchBarListener();




        // Configuration de l'écouteur du clic sur un élément de la liste
        itemListListener();
    }


    private Runnable longClickRunnable = new Runnable() {
        @Override
        public void run() {
            // Si le bouton est toujours enfoncé après 3 secondes
            longClickDetected = true;
            clickSaveParcours(lancerParcoursButton);
        }
    };




    @Override
    protected void onResume() {
        super.onResume();




        // Appelle la méthode pour récupérer les données de l'API
        fetchParcoursFromApi(null, null);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            // Annuler toutes les requêtes en cours associées à cette activité
            requestQueue.cancelAll(this);
        }
    }




    /**
     * Configure l'écouteur de la barre de recherche.
     */
    private void searchBarListener() {
        rechercheNom.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String rechercheNomParcours) {
                // Ne rien faire lors de la soumission du texte
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                // Filtrer la liste des parcours en fonction du texte saisi
                List<Parcours> filteredList = new ArrayList<>();
                if (newText.isEmpty()) {
                    // Si la chaîne de recherche est vide, afficher la liste complète des parcours
                    filteredList.addAll(parcoursListOrigine);
                } else {
                    for (Parcours parcours : parcoursListOrigine) { // Utiliser parcoursListOrigine ici
                        if (parcours.getName().toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(parcours);
                        }
                    }
                }
                // Mettre à jour l'adaptateur avec la liste filtrée
                adapter.clear();
                adapter.addAll(filteredList);
                adapter.notifyDataSetChanged();


                return true;
            }
        });
    }








    /**
     * Méthode exécutée lorsque l'utilisateur clique sur le bouton des filtres.
     */
    public void clickFilter(View v) {
        /* Lorsque l'utilisateur clique sur le bouton de filtre, ouvre une boîte de dialogue
         * pour définir les filtres
         */
        final Dialog dialog = new Dialog(Accueil.this);




        // Définis le contenu de la fenêtre contextuelle
        dialog.setContentView(R.layout.popup_filtre);




        // Récupère les éléments de la fenêtre contextuelle
        EditText inputDateMin = dialog.findViewById(R.id.inputDureeMin);
        EditText inputDateMax = dialog.findViewById(R.id.inputDureeMax);




        // Gestion du clic sur les champs de date pour afficher le calendrier
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
                /* Lorsque l'utilisateur clique sur "Rechercher", récupère les dates
                 * sélectionnées et lance la recherche
                 */




                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date dateMin = simpleDateFormat.parse(inputDateMin.getText().toString());
                    Date dateMax = simpleDateFormat.parse(inputDateMax.getText().toString());
                    Date[] dateFilter = {dateMin, dateMax};




                    parcoursList.clear();
                    adapter.notifyDataSetChanged();
                    fetchParcoursFromApi(nameParcours, dateFilter);
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




    /**
     * Configure l'écouteur du clic sur un élément de la liste.
     */
    private void itemListListener() {
        listViewParcours.setOnItemLongClickListener((parent, view, position, id) -> {
            /* Lorsque l'utilisateur maintient un élément de la liste enfoncé, ouvre une boîte
             * de dialogue pour supprimer le parcours
             */
            final Dialog dialog = new Dialog(Accueil.this);
            Parcours parcours = parcoursList.get(position);
            // Définis le contenu de la fenêtre contextuelle
            dialog.setContentView(R.layout.popup_modif_parcours);




            // Récupère les éléments de la fenêtre contextuelle
            EditText newDescriptionInput = dialog.findViewById(R.id.nouvelleDescription);
            Button deleteButton = dialog.findViewById(R.id.btnSupprimer);
            Button cancelButton = dialog.findViewById(R.id.btnAnnuler);
            Button editButton = dialog.findViewById(R.id.btnModifier);


            newDescriptionInput.setText(parcours.getDescription());


            // Gestion du clic sur le bouton "Supprimer"
            deleteButton.setOnClickListener(v -> {
                /* Lorsque l'utilisateur clique sur "Supprimer",
                 * supprime le parcours de l'API et de la liste
                 */
                deleteParcoursFromApi(parcours);
                dialog.dismiss();
            });


            // Lorsque l'utilisateur clique sur "Annuler", ferme la boîte de dialogue
            cancelButton.setOnClickListener(v -> dialog.dismiss());




            editButton.setOnClickListener(v -> {
                /* Lorsque l'utilisateur clique sur "Modifier la description",
                 * modifie la description du parcours
                 */
                String newDescriptionValue = newDescriptionInput.getText().toString();


                // Si la description est différente de l'ancienne
                if(!newDescriptionValue.equals(parcours.getDescription())) {
                    parcours.setDescription(newDescriptionValue);
                    updateParcoursFromApi(parcours);
                }
                dialog.dismiss();
            });


            // Affiche la fenêtre contextuelle
            dialog.show();
            return true;
        });




        listViewParcours.setOnItemClickListener((parent, view, position, id) -> {
            /* Lorsque l'utilisateur clique sur un élément de la liste */
            Parcours parcours = parcoursList.get(position);
            switchToSynthese(token, parcours.getId());
        });
    }


    private void updateParcoursFromApi(Parcours parcours) {


        String apiUrl = String.format(Endpoints.UPDATE_PARCOURS, parcours.getId());


        // Crée un objet JSON avec la nouvelle description du parcours
        JSONObject parcoursJson = new JSONObject();
        try {
            parcoursJson.put("description", parcours.getDescription());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Crée une requête PUT pour mettre à jour le parcours sur l'API
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, apiUrl, parcoursJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Traitement de la réponse en cas de succès
                        Log.d("PUT Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Traitement de l'erreur
                        Log.e("PUT Error", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }
        };


        // Ajoute la requête de mise à jour à la file d'attente des requêtes HTTP
        requestQueue.add(putRequest);
    }






    /**
     * Méthode exécutée lorsque l'utilisateur clique sur le bouton d'enregistrement de parcours.
     */
    public void clickSaveParcours(View view) {
        // Vérifie les permissions de l'utilisateur
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showPermissionPopup();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            showSaveParcoursPopup();
        }
    }




    /**
     * Affiche la popup des permissions manquantes.
     */
    private void showPermissionPopup() {
        // Créé une instance de Dialog
        final Dialog dialog = new Dialog(Accueil.this);


        // Définis le contenu de la fenêtre contextuelle
        dialog.setContentView(R.layout.popup_permission);


        Button closeButton = dialog.findViewById(R.id.fermer);


        // Gère le clic sur le bouton "Fermer"
        closeButton.setOnClickListener(view -> {
            dialog.dismiss();
        });


        dialog.show();
    }




    /**
     * Affiche la popup d'enregistrement d'un parcours à l'écran.
     */
    private void showSaveParcoursPopup() {
        // Créé une instance de Dialog
        final Dialog dialog = new Dialog(Accueil.this);




        // Définis le contenu de la fenêtre contextuelle
        dialog.setContentView(R.layout.popup_lancer_course);




        // Récupére les éléments de la fenêtre contextuelle
        EditText inputCommentaire = dialog.findViewById(R.id.inputDescriptionParcours);
        EditText inputName = dialog.findViewById(R.id.inputNom);




        Button confirmer = dialog.findViewById(R.id.confirmer);
        Button annuler = dialog.findViewById(R.id.annuler);




        // Gère le clic sur le bouton "Confirmer"
        confirmer.setOnClickListener(view -> {
            switchToCourse(token, inputName.getText().toString(), inputCommentaire.getText().toString());
            dialog.dismiss();
        });




        // Gère le clic sur le bouton "Annuler"
        annuler.setOnClickListener(view -> dialog.dismiss());




        dialog.show();
    }


    /**
     * Appelé quand l'activité course renvoie le parcours effectué au format json
     */
    private void parcoursDone() {


    }


    /**
     * Exécuté au retour de l'activité course
     */
    private void couseActivityDone(ActivityResult result) {
        Intent intent = result.getData();


        if (result.getResultCode() == Activity.RESULT_OK) {
            switchToSynthese(token, intent.getStringExtra(Keys.PARCOURS_ID_KEY));
        }
    }


    /**
     * Lance l'intention course.
     * @param token valeur du token à transmettre à l'activité
     */
    private void switchToCourse(String token, String name, String description) {
        // création d'une intention pour demander lancement de l'activité accueil
        Intent intention = new Intent(Accueil.this, CourseActivity.class);
        intention.putExtra(Keys.TOKEN_KEY, token);
        intention.putExtra(Keys.PARCOURS_NAME_KEY, name);
        intention.putExtra(Keys.PARCOURS_DESCRIPTION_KEY, description);
        // lancement de l'activité accueil via l'intention préalablement créée
        courseActivityLauncher.launch(intention);
    }


    /**
     * Lance l'intention synthèse.
     * @param token valeur du token à transmettre à l'activité
     * @param parcoursId id du parcours
     */
    private void switchToSynthese(String token, String parcoursId) {
        // création d'une intention pour demander lancement de l'activité accueil
        Intent intention = new Intent(Accueil.this, CourseSynthese.class);
        intention.putExtra(Keys.TOKEN_KEY, token);
        intention.putExtra(Keys.PARCOURS_ID_KEY, parcoursId);
        // lancement de l'activité accueil via l'intention préalablement créée
        startActivity(intention);
    }


    private void unsentFiles() throws FileNotFoundException {


        InputStreamReader fichier =
                new InputStreamReader(openFileInput("parcoursTemp"));
        BufferedReader fichierTexte = new BufferedReader(fichier);
        String ligne;
        try {
            while ((ligne = fichierTexte.readLine()) != null) {
                // Créer un JSONObject à partir de la ligne
                JSONObject jsonParcours = new JSONObject(ligne);
                sendToApi(jsonParcours);
            }
            deleteFile("parcoursTemp");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    private void sendToApi(JSONObject jsonParcours) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Endpoints.ADD_PARCOURS, jsonParcours,
                response -> {


                }, error -> {
            // En cas d'erreur de l'API, cette méthode est appelée
        });
    }




}

