package fr.gr3.strovo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import fr.gr3.strovo.api.Endpoints;
import fr.gr3.strovo.api.model.Route;

public class CourseSynthese extends AppCompatActivity {

    /** Clé de l'id du parcours */
    public static final String PARCOURS_ID = "PARCOURS_ID";

    private MapView mapView;

    /** Element graphique: Tracé du parcours */
    private Polyline polyline;
    private Button btnRetour;
    private String parcoursId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_synthese);
        btnRetour = findViewById(R.id.floating_home_button);

        mapView = initMap();

        polyline = initPolyline();
        mapView.getOverlays().add(polyline);


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
        MapView map = findViewById(R.id.mapView);
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
                            float averageSpeed = response.isNull("averageSpeed") ? 0 : (float) response.getDouble("averageSpeed");
                            float distance = response.isNull("distance") ? 0 : (float) response.getDouble("distance");
                            double elevation = response.isNull("elevation") ? 0 : response.getDouble("elevation");

                            String[] interestPointsIds = null;
                            JSONArray interestPointsIdsJson = response.isNull("interestPointsIds") ? null : response.getJSONArray("interestPointsIds");
                            if (interestPointsIdsJson != null) {
                                interestPointsIds = new String[interestPointsIdsJson.length()];
                                for (int i = 0; i < interestPointsIdsJson.length(); i++) {
                                    interestPointsIds[i] = interestPointsIdsJson.getString(i);
                                }
                            }

                            double[][] coordinates = null;
                            JSONArray coordinatesJson = response.isNull("coordinates") ? null : response.getJSONArray("coordinates");
                            if (coordinatesJson != null) {
                                coordinates = new double[coordinatesJson.length()][2];
                                for (int i = 0; i < coordinatesJson.length(); i++) {
                                    JSONArray coordinate = coordinatesJson.getJSONArray(i);
                                    coordinates[i][0] = coordinate.getDouble(0);
                                    coordinates[i][1] = coordinate.getDouble(1);
                                }
                            }

                            // Créez un nouvel objet Route avec les données extraites
                            Route route = new Route(name, description, date, time, averageSpeed, distance, elevation, interestPointsIds, coordinates);

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
        double[][] coordinates = route.getCoordinates();
        for (double[] coordinate: coordinates) {
            GeoPoint point = new GeoPoint(coordinate[0], coordinate[1]);
            polyline.addPoint(point);
        }
        mapView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();  // Needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();  // Needed for compass, my location overlays, v6.0.0 and up
    }
}
