package fr.gr3.strovo.map;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fr.gr3.strovo.CourseSynthese;
import fr.gr3.strovo.R;
import fr.gr3.strovo.api.Endpoints;
import fr.gr3.strovo.api.StrovoApi;
import fr.gr3.strovo.api.model.Parcours;
import fr.gr3.strovo.utils.Keys;

public class CourseActivity extends AppCompatActivity {

    /** Fournisseur de localisation de l'utilisateur. */
    private static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    /** Carte à afficher à l'écran. */
    private MapView map;

    /** Bouton d'arrêt d'enregistrement */
    private Button stopButton;

    /** Gestionnaire du parcours de l'utilisateur. */
    private ParcoursManager parcoursManager;

    /** Ecouteur de localisation de l'utilisateur. */
    private LocationListener locationListener;

    /** Gestionnaire de la localisation de l'utilisateur. */
    private LocationManager locationManager;

    /** Element graphique: Tracé du parcours */
    private Polyline polyline;

    /** Element graphique: barre d'échelle */
    private ScaleBarOverlay scaleBarOverlay;

    /** Element graphique: compas */
    private CompassOverlay compassOverlay;

    /** Element graphique: position actuelle de l'utilisateur */
    private MyLocationNewOverlay myLocationNewOverlay;

    /** Indicateur de l'état du GPS */
    private boolean gpsEnabled = true;

    /** Queue pour effectuer la requête HTTP */
    private RequestQueue requestQueue;

    /** Définit le status du clic sur le bouton */
    private boolean longClickDetected;

    /** Handler pour gérer le délai du bouton stop */
    private Handler handler = new Handler();

    /** Token de connexion de l'utilisateur */
    private String token;

    /**
     * Méthode appelée lors de la création de l'activité.
     * Initialise les composants graphiques, configure les écouteurs d'événements
     * et effectue les premières actions nécessaires à l'initialisation de l'activité.
     * @param savedInstanceState Etat de l'activité.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        // Récupère les valeurs de l'intention
        Intent intent = getIntent();
        token = intent.getStringExtra(Keys.TOKEN_KEY);
        String parcoursName = intent.getStringExtra(Keys.PARCOURS_NAME_KEY);
        String parcoursDescription = intent.getStringExtra(Keys.PARCOURS_DESCRIPTION_KEY);

        requestQueue = Volley.newRequestQueue(this);
        map = initMap();

        stopButton = findViewById(R.id.btnArreter);

        parcoursManager = new ParcoursManager(parcoursName, parcoursDescription, new Date());
        locationListener = initLocationListener();
        locationManager = initLocationManager();

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

        parcoursManager.start();

        stopParcoursListener();
    }

    /**
     * Initialise la carte qui sera affichée à l'écran.
     * @return la carte initilisée
     */
    private MapView initMap() {
        MapView map = findViewById(R.id.mapCourse);
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);
        map.getController().setZoom(20.0);
        return map;
    }

    /**
     * Ecouteurs d'événements du clic sur le bouton d'arrêt du parcours.
     */
    private void stopParcoursListener() {
        stopButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        longClickDetected = false;
                        handler.postDelayed(longClickRunnable, 3000); // Déclencher après 3 secondes
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacks(longClickRunnable);
                        if (!longClickDetected) {
                            // Si le clic est court (inférieur à 3 secondes)
                            Toast.makeText(CourseActivity.this, R.string.erreurArretParcours, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * Exécutable permettant d'arrêter le parcours.
     */
    private Runnable longClickRunnable = new Runnable() {
        @Override
        public void run() {
            // Si le bouton est toujours enfoncé après 3 secondes
            longClickDetected = true;
            clicStopParcours(stopButton);
        }
    };


    /**
     * Méthode exécutée lorsque l'utilisateur clique sur le bouton d'arrêt du parcours.
     */
    public void clicStopParcours(View view) {
        parcoursManager.stop();

        locationListener = null;
        locationManager = null;
        try {
            addParcoursToApi();
        } catch (JSONException ignore) {

        }

        // TODO arreter le traceur
    }

    /** Initialise l'écouteur de localisation de l'utilisateur.
     * @return la l'écouteur initilisé
     */
    private LocationListener initLocationListener() {
        // Initialisation du gestionnaire de localisation et du listener

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            // Méthode exécutée lors de la détection d'un changement de position de l'utilisateur
            @Override
            public void onLocationChanged(Location location) {
                GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
                // Si course lancée
                if (parcoursManager.isRunning()) {
                    // Met à jour le parcours
                    parcoursManager.addLocation(location);
                    polyline.addPoint(point);
                    Log.d("LOGG APPLI", "Ajout dun nouveau point");
                }
                // Centre la map sur la position de l'utilisateur
                map.getController().setCenter(point);

                Toast.makeText(getApplicationContext(), "Changement position", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onProviderDisabled(String provider) {
                // Mise en pause du parcours lorsque la localisation est désactivée
                if (parcoursManager.isRunning()) {
                    parcoursManager.pause();
                    Toast.makeText(getApplicationContext(), "Localisation désactivée. Le parcours est mis en pause.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                // Reprendre le parcours lorsque la localisation est réactivée
                if (parcoursManager.isPaused()) {
                    parcoursManager.resume();
                    Toast.makeText(getApplicationContext(), "Localisation réactivée. Le parcours reprend.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStatusChanged(String fournisseur, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        Toast.makeText(getApplicationContext(), fournisseur + " état disponible", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
                        Toast.makeText(getApplicationContext(), fournisseur + " état indisponible", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Toast.makeText(getApplicationContext(), fournisseur + " état temporairement indisponible", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), fournisseur + " état : " + status, Toast.LENGTH_SHORT).show();
                }
            }
        };
        return locationListener;


    }

    /** Initialise le gestionnaire de localisation de l'utilisateur.
     * @return le gestionnaire initilisé
     */
    private LocationManager initLocationManager() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        // Vérification des permissions
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Défini une mise à jour automatique du gestionnaire de localisation
            locationManager.requestLocationUpdates(LOCATION_PROVIDER, 3000, 2, locationListener);
        }
        return locationManager;
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
     * Met en pause le parcours sur la carte.
     */
    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();

        // Met le parcours en pause lorsque l'activité est mise en pause
        if (parcoursManager.isRunning()) {
            parcoursManager.pause();
        }
    }

    /**
     * Reprend le parcours et l'affiche sur la carte.
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();

        // Reprend le parcours lorsque l'activité reprend
        if (parcoursManager.isPaused()) {
            parcoursManager.resume();
        }
    }

    /**
     * Ajoute un point d'intérêt au parcours et l'affiche sur la carte.
     * @param interestPoint point d'intérêt
     */
    private void addInterestPoint(InterestPoint interestPoint) {
        // Ajoute le point d'intérêt au parcours
        parcoursManager.addInterestPoint(interestPoint);

        // Affiche le point d'intérêt sur la carte
        Marker marker = new Marker(map);
        marker.setPosition(interestPoint.getPoint());
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(interestPoint.getName());// TODO voir si on affiche le titre ou la description
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Toast.makeText(getApplicationContext(), interestPoint.getName() + ": " + interestPoint.getDescription(), Toast.LENGTH_SHORT).show();
                // TODO showInterestPointPopup(interestpoint) un truc comme ça
                return true;
            }
        });
        map.getOverlays().add(marker);
    }

    /**
     * Exécuté quand l'utilisateur appuie sur le bouton "ajouter un point d'intérêt".
     * Ouvre une boîte de dialogue dans laquelle seront demandées le nom et la description
     * du point d'intérêt à ajouter.
     */
    public void clickAddInterestPoint(View view) {
        final Dialog dialog = new Dialog(CourseActivity.this);

        // Définis le contenu de la fenêtre contextuelle
        dialog.setContentView(R.layout.popup_point_interet);

        // Récupère les éléments de la fenêtre contextuelle
        EditText inputLibelle = dialog.findViewById(R.id.inputLibelle); // Obligatoire
        EditText inputDescription = dialog.findViewById(R.id.inputDescription); // Optionnelle
        Button confirmer = dialog.findViewById(R.id.btnConfirmer);
        Button annuler = dialog.findViewById(R.id.btnAnnuler);

        // Quand l'utilisateur clique sur "Confirmer"
        confirmer.setOnClickListener(v -> {
            // Vérification les permissions
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO afficher une popup ?
                return;
            }

            if (!inputLibelle.getText().toString().isEmpty()) {
                // Ajoute un point d'intérêt sur la position actuelle de l'utilisateur
                Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                GeoPoint point = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                InterestPoint interestPoint = new InterestPoint(point,
                        inputLibelle.getText().toString(), inputDescription.getText().toString());
                addInterestPoint(interestPoint);

                dialog.dismiss();
            } else {
                showError(getString(R.string.errPointNameEmpty));
            }
        });

        // Quand l'utilisateur clique sur "Annuler"
        annuler.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Envoie une requête POST à l'API pour ajouter un nouveau parcours.
     */

    public void addParcoursToApi() throws JSONException {
        FileOutputStream fichier = null;
        Parcours parcours = parcoursManager.getParcours();
        JsonObjectRequest request = StrovoApi.getInstance().addParcours(token, parcoursManager.getParcours(),
                this::onAddParcoursSuccess,
                error -> saveInFile()
        );
        // Ajoute la requête à la file d'attente des requêtes HTTP
        requestQueue.add(request);
    }

    /**
     * Exécuté lorsque l'ajout du parcours est réussi.
     */
    private void onAddParcoursSuccess(JSONObject  response) {
        try {
            // On récupère l'objet Parcours de la réponse
            String parcoursId = response.getString("id");
            switchToAccueil(parcoursId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Renvoie l'id du parcours créé à l'accueil
     * @param parcoursId id du parcours
     */
    private void switchToAccueil(String parcoursId) {
        // création d'une intention pour demander lancement de l'activité accueil
        Intent intention = new Intent();
        intention.putExtra(Keys.PARCOURS_ID_KEY, parcoursId);
        setResult(Activity.RESULT_OK, intention);
        finish();
    }

    /** Crée un toast pour afficher l'erreur. */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void saveInFile(){
        try {
            OutputStream fichier = null;
            Parcours parcours = parcoursManager.getParcours();
            fichier = openFileOutput("parcoursTemp", Context.MODE_PRIVATE);
            File file = getFileStreamPath("parcoursTemp");
            Log.d("CourseActivity", "Exists : " + file.exists());
            Log.d("CourseActivity", "Sauvegarde du parcours en cours...");

            JSONObject jsonParcours = new JSONObject();
            jsonParcours.put("name", parcours.getName());
            jsonParcours.put("description", parcours.getDescription());
            jsonParcours.put("date", parcours.getDate());
            jsonParcours.put("time", parcours.getTime());
            jsonParcours.put("speed", parcours.getSpeed());
            jsonParcours.put("distance", parcours.getDistance());
            jsonParcours.put("elevation", parcours.getElevation());
            jsonParcours.put("interestPoints", parcours.getInterestPoints());
            jsonParcours.put("coordinates", parcours.getCoordinates());

            fichier.write(jsonParcours.toString().getBytes());
            fichier.close();
            // Log pour confirmer la sauvegarde
            Log.d("CourseActivity", "Parcours sauvegardé avec succès.");
            switchToAccueil(parcours.getId());
        } catch (FileNotFoundException e) {
            Log.e("CourseActivity", "Fichier non trouvé pour la sauvegarde", e);
        } catch (IOException | JSONException e) {
            Log.e("CourseActivity", "Erreur lors de la sauvegarde du parcours", e);
        }
    }
}