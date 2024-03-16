package fr.gr3.strovo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.gr3.strovo.api.Endpoints;
import fr.gr3.strovo.api.model.Route;
import fr.gr3.strovo.map.InterestPoint;

public class CourseSynthese extends AppCompatActivity {

    /** Clé de l'id du parcours */
    public static final String PARCOURS_ID = "PARCOURS_ID";

    private MapView map;

    /** Element graphique: Tracé du parcours */
    private Polyline polyline;

    /** Element graphique: barre d'échelle */
    private ScaleBarOverlay scaleBarOverlay;

    /** Element graphique: compas */
    private CompassOverlay compassOverlay;

    /** Element graphique: position actuelle de l'utilisateur */
    private MyLocationNewOverlay myLocationNewOverlay;

    private Button btnRetour;
    private String parcoursId;
    private TextView parcoursName;
    private TextView parcoursDescription;
    private TextView parcoursDate;
    private TextView parcoursDistance;
    private TextView parcoursElevation;
    private TextView parcoursTime;
    private TextView parcoursSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_synthese);
        btnRetour = findViewById(R.id.floating_home_button);

        map = initMap();

        // Initialisation des éléments graphiques
        polyline = initPolyline();
        scaleBarOverlay = new ScaleBarOverlay(map);
        compassOverlay = new CompassOverlay(getApplicationContext(), map);
        compassOverlay.enableCompass();
        myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), map);
        myLocationNewOverlay.enableMyLocation();

        map.getOverlays().add(polyline);
        map.getOverlays().add(scaleBarOverlay);
        map.getOverlays().add(compassOverlay);
        map.getOverlays().add(myLocationNewOverlay);

        parcoursDate = findViewById(R.id.dateTextView);
        parcoursName = findViewById(R.id.nameTextView);
        parcoursDistance = findViewById(R.id.distanceTextView);
        parcoursDescription = findViewById(R.id.descriptionTextView);
        parcoursElevation = findViewById(R.id.elevationTextView);
        parcoursTime = findViewById(R.id.timeTextView);
        parcoursSpeed = findViewById(R.id.speedTextView);

        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseSynthese.this, Accueil.class);
                startActivity(intent);
            }
        });

        // Récupère l'intention qui a démarré cette activité
        Intent intent = getIntent();

        // Vérifie si l'intention contient le token
        if (intent != null && intent.hasExtra(PARCOURS_ID)) {
            parcoursId = intent.getStringExtra(PARCOURS_ID);
            findParcoursFromApi(parcoursId);
        }
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

    private void findParcoursFromApi(String parcoursId) {
        String apiUrl = String.format(Endpoints.GET_PARCOURS_BY_ID, parcoursId);

        // Créer une nouvelle demande GET à l'aide de Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                            // Extrayez les différentes propriétés de l'objet JSON
                            String name = response.isNull("name") ? null : response.getString("name");
                            String description = response.isNull("description") ? null : response.getString("description");
                            Date date = response.isNull("date") ? null : formatter.parse(response.getString("date"));
                            long time = response.isNull("time") ? 0 : response.getLong("time");
                            float speed = response.isNull("speed") ? 0 : (float) response.getDouble("speed");
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

                            // Créez un nouvel objet Route avec les données extraites
                            Route route = new Route(1, name, description, date, time, speed, distance, elevation, interestPoints, coordinates);

                            chargerParcours(route);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Gérer les erreurs de parsing JSON ici
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("AAAAAAA","erreur");
                    }
                });

        // Ajouter la demande à la file d'attente de Volley pour l'exécuter
        Volley.newRequestQueue(CourseSynthese.this).add(jsonObjectRequest);
    }

    private void chargerParcours(Route route) {
        List<double[]> coordinates = route.getCoordinates();
        // Affiche le parcours sur la map
        for (double[] coordinate: coordinates) {
            GeoPoint point = new GeoPoint(coordinate[0], coordinate[1]);
            polyline.addPoint(point);
        }
        map.invalidate();
        map.getController().setCenter(new GeoPoint(coordinates.get(0)[0], coordinates.get(0)[1]));

        // Affiche les informations associés au parcours
        parcoursName.setText(route.getName());
        parcoursDescription.setText(route.getDescription());
        parcoursDate.setText(route.getDate().toString());
        parcoursDistance.setText(String.valueOf(route.getDistance()));
        parcoursSpeed.setText(String.valueOf(route.getSpeed()));
        parcoursTime.setText(String.valueOf(route.getTime()));
        parcoursElevation.setText(String.valueOf(route.getElevation()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();  // Needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();  // Needed for compass, my location overlays, v6.0.0 and up
    }
}
