package fr.gr3.strovo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.gr3.strovo.api.Endpoints;
import fr.gr3.strovo.api.StrovoApi;
import fr.gr3.strovo.api.model.Parcours;
import fr.gr3.strovo.map.InterestPoint;
import fr.gr3.strovo.utils.Keys;

/**
 * Activité pour afficher un résumé d'un parcours.
 */
public class CourseSynthese extends AppCompatActivity {

    /**
     * Clé de l'identifiant du parcours.
     */
    public static final String PARCOURS_ID = "PARCOURS_ID";

    /**
     * Vue qui affiche la carte.
     */
    private MapView map;

    /**
     * Élément graphique représentant le tracé du parcours sur la carte.
     */
    private Polyline polyline;

    /** Element graphique: barre d'échelle */
    private ScaleBarOverlay scaleBarOverlay;

    /** Element graphique: compas */
    private CompassOverlay compassOverlay;

    /**
     * Bouton de retour à l'activité précédente.
     */
    private Button btnRetour;

    /**
     * Identifiant du parcours en cours de visualisation.
     */
    private String parcoursId;

    /**
     * Champ de texte affichant le nom du parcours.
     */
    private TextView parcoursName;

    /**
     * Champ de texte affichant la description du parcours.
     */
    private TextView parcoursDescription;

    /**
     * Champ de texte affichant la date du parcours.
     */
    private TextView parcoursDate;

    /**
     * Champ de texte affichant la distance parcourue.
     */
    private TextView parcoursDistance;

    /**
     * Champ de texte affichant le dénivelé du parcours.
     */
    private TextView parcoursElevation;

    /**
     * Champ de texte affichant le temps écoulé durant le parcours.
     */
    private TextView parcoursTime;

    /**
     * Champ de texte affichant la vitesse moyenne durant le parcours.
     */
    private TextView parcoursSpeed;

    /**
     * File d'attente pour effectuer les requêtes HTTP.
     */
    private RequestQueue requestQueue;

    /** Token de connexion de l'utilisateur */
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_synthese);
        btnRetour = findViewById(R.id.floating_home_button);

        map = initMap();

        Intent intent = getIntent();
        token = intent.getStringExtra(Keys.TOKEN_KEY);
        parcoursId = intent.getStringExtra(Keys.PARCOURS_ID_KEY);

        // Initialisation des éléments graphiques
        polyline = initPolyline();
        scaleBarOverlay = new ScaleBarOverlay(map);
        compassOverlay = new CompassOverlay(getApplicationContext(), map);
        compassOverlay.enableCompass();

        map.getOverlays().add(polyline);
        map.getOverlays().add(scaleBarOverlay);
        map.getOverlays().add(compassOverlay);

        initializeViews();
        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action à effectuer lorsque le bouton est cliqué
                finish();
            }
        });

        getParcoursByIdFromApi(parcoursId);
    }

    /**
     * Méthode exécutée lorsque l'utilisateur clique sur le bouton retour.
     */
    private void clicRetourAccueil(View v) {
        finish();
    }

    /**
     * Ajoute un point d'intérêt au parcours et l'affiche sur la carte.
     * @param interestPoint point d'intérêt
     */
    private void addInterestPoint(InterestPoint interestPoint) {

        // Affiche le point d'intérêt sur la carte
        Marker marker = new Marker(map);
        marker.setPosition(interestPoint.getPoint());
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(interestPoint.getName());// TODO voir si on affiche le titre ou la description
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {

                Toast.makeText(getApplicationContext(), interestPoint.getName() + ": " + interestPoint.getDescription(), Toast.LENGTH_SHORT).show();
                // showInterestPointPopup(interestpoint) un truc comme ça
                return true;
            }
        });
        map.getOverlays().add(marker);
    }

    /**
     * Initialise les vues de l'activité et configure les écouteurs d'événements.
     */
    private void initializeViews() {
        // Initialisation des composants graphiques
        parcoursDate = findViewById(R.id.dateTextView);
        parcoursName = findViewById(R.id.nameTextView);
        parcoursDistance = findViewById(R.id.distanceTextView);
        parcoursDescription = findViewById(R.id.descriptionTextView);
        parcoursElevation = findViewById(R.id.elevationTextView);
        parcoursTime = findViewById(R.id.timeTextView);
        parcoursSpeed = findViewById(R.id.speedTextView);

        // Initialise la file d'attente des requêtes HTTP avec Volley
        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * Initialise la carte qui sera affichée à l'écran.
     * @return la carte initilisée
     */
    private MapView initMap() {
        MapView map = findViewById(R.id.mapSynthese);
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);
        map.getController().setZoom(20.0);
        return map;
    }

    /** Initialise le tracé du parcours.
     * @return le tracé
     */
    private Polyline initPolyline() {
        Polyline polyline = new Polyline();
        polyline.getOutlinePaint().setColor(Color.RED);
        polyline.getOutlinePaint().setStrokeWidth(5);
        return polyline;
    }

    /**
     * Effectue une requête à l'API pour récupérer les détails d'un parcours spécifié par son identifiant.
     * Les détails du parcours récupérés sont utilisés pour afficher les informations sur l'interface graphique.
     * @param parcoursId l'identifiant du parcours à récupérer
     */
    private void getParcoursByIdFromApi(String parcoursId) {
        JsonObjectRequest request = StrovoApi.getInstance().getParcoursById(token, parcoursId,
                this::onGetParcoursByIdSuccess,
                error -> showError(getString(R.string.errRecupInfosParcours))
        );

        // Ajouter la demande à la file d'attente de Volley pour l'exécuter
        requestQueue.add(request);
    }

    /**
     * Exécuté lorsque la récupération du parcours est réussie.
     */
    private void onGetParcoursByIdSuccess(JSONObject response) {
        try {
            // Créé un nouvel objet Parcours avec les données extraites
            Parcours parcours = fetchParcours(response);

            // Charge les éléments graphiques de la synthèse
            chargerParcours(parcours);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupère les données du parcours renvoyé par l'API.
     * @param response les données du parcours retourné par l'API
     * @return l'objet parcours.
     */
    private Parcours fetchParcours(JSONObject response) throws JSONException, ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        // Récupère les différentes propriétés de l'objet JSON
        String name = response.isNull("name") ? null : response.getString("name");
        String description = response.isNull("description") ? null : response.getString("description");
        Date date = response.isNull("date") ? null : formatter.parse(response.getString("date"));
        long time = response.isNull("time") ? 0 : response.getLong("time");
        float averageSpeed = response.isNull("speed") ? 0 : (float) response.getDouble("speed");
        float distance = response.isNull("distance") ? 0 : (float) response.getDouble("distance");
        double elevation = response.isNull("elevation") ? 0 : response.getDouble("elevation");

        // Conversion des points d'intérêt (si présents)
        List<InterestPoint> interestPoints = new ArrayList<>();
        JSONArray interestPointsJson = response.isNull("interestPoints") ? null : response.getJSONArray("interestPoints");
        if (interestPointsJson != null) {
            for (int i = 0; i < interestPointsJson.length(); i++) {
                JSONObject interestPointJson = interestPointsJson.getJSONObject(i);
                interestPoints.add(InterestPoint.fromJson(interestPointJson));
            }
        }

        List<double[]> coordinates = new ArrayList<>();
        JSONArray coordinatesJson = response.isNull("coordinates") ? null : response.getJSONArray("coordinates");
        if (coordinatesJson != null) {
            for (int i = 0; i < coordinatesJson.length(); i++) {
                JSONArray coordinate = coordinatesJson.getJSONArray(i);
                coordinates.add(new double[] {coordinate.getDouble(0), coordinate.getDouble(1)});
            }
        }

        return new Parcours(name, description, date, time, averageSpeed, distance, elevation, interestPoints, coordinates);
    }

    /**
     * Affiche les informations associées au parcours sur l'interface graphique.
     * @param parcours le parcours à afficher
     */
    private void chargerParcours(Parcours parcours) {
        List<double[]> coordinates = parcours.getCoordinates();
        // Affiche le parcours sur la map
        for (double[] coordinate: coordinates) {
            GeoPoint point = new GeoPoint(coordinate[0], coordinate[1]);
            polyline.addPoint(point);
        }
        map.invalidate();
        map.getController().setCenter(new GeoPoint(coordinates.get(0)[0], coordinates.get(0)[1]));

        // Affiche les points d'intérêts
        for (InterestPoint interestPoint : parcours.getInterestPoints()) {
            addInterestPoint(interestPoint);
        }

        // Affiche les informations associés au parcours
        parcoursName.setText(parcours.getName());
        parcoursDescription.setText(parcours.getDescription());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(parcours.getDate());
        parcoursDate.setText(formattedDate);
        int distance = parcours.getDistance();
        String resDistance;
        if (distance > 1000) {
            // Convertie la distance en kilomètres
            int kilometre = distance/1000;
            int metre = distance - kilometre*1000;
            resDistance = String.format(getString(R.string.distanceKm), String.valueOf(kilometre), String.valueOf(metre));
        } else {
            resDistance = String.format(getString(R.string.distance), String.valueOf(distance));
        }

        String time = formatTime(parcours.getTime());
        parcoursDistance.setText(resDistance);
        parcoursSpeed.setText(String.format(getString(R.string.vitesse), String.valueOf(parcours.getSpeed())));
        parcoursTime.setText(String.format(getString(R.string.temps), time));
        parcoursElevation.setText(String.format(getString(R.string.denivele), String.valueOf(parcours.getElevation())));
    }

    /**
     * Formate le temps à partir du nombre de millisecondes.
     * @param milliseconds le nombre de millisecondes
     * @return le temps formaté (HH h MM min SS s)
     */
    public String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        return String.format("%02d h %02d min %02d s", hours, minutes, remainingSeconds);
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    /** Crée un toast pour afficher l'erreur. */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
