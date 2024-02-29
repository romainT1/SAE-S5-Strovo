package fr.gr3.strovo.map;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import fr.gr3.strovo.R;

public class CourseActivity extends AppCompatActivity {
    /** Fournisseur de localisation de l'utilisateur. */
    private static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    /** Carte à afficher à l'écran. */
    private MapView map;

    /** Parcours effectué par l'utilisateur. */
    private Parcours parcours;


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

    /**
     * Méthode appelée lors de la création de l'activité.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        // Demande la permission d'accéder à la localisation
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        map = initMap();
        parcours = new Parcours();
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

        parcours.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Vérifie si l'utilisateur a donné les permissions ou non.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(CourseActivity.this, "Permission Autorisée", Toast.LENGTH_SHORT).show();
            // TODO voir quoi faire ??
        }
        else {
            Toast.makeText(CourseActivity.this, "Permission Refusée", Toast.LENGTH_SHORT).show();
            // TODO voir quoi faire ??
        }
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


    /** Initialise l'écouteur de localisation de l'utilisateur.
     * @return la l'écouteur initilisé
     */
    private LocationListener initLocationListener() {
        LocationListener locationListener = new LocationListener() {
            // Méthode exécutée lors de la détection d'un changement de position de l'utilisateur
            @Override
            public void onLocationChanged(@NonNull Location location) {
                GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
                // Si course lancée
                if (parcours.isRunning()) {
                    // Met à jour le parcours
                    parcours.addLocation(location);
                    polyline.addPoint(point);
                }
                // Centre la map sur la position de l'utilisateur
                map.getController().setCenter(point);

                Toast.makeText(getApplicationContext(), "Changement position", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                LocationListener.super.onProviderDisabled(provider);

                Toast.makeText(getApplicationContext(), "Fournisseur désactivé", Toast.LENGTH_SHORT).show();
                // TODO si on désactive le fournisseur
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                LocationListener.super.onProviderEnabled(provider);
                Toast.makeText(getApplicationContext(), "Fournisseur activé", Toast.LENGTH_SHORT).show();
                // TODO si on active le fournisseur
            }

            @Override
            public void onStatusChanged(String fournisseur, int status, Bundle extras){
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
     * @return la l'écouteur initilisé
     */
    private LocationManager initLocationManager() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO permissions manquantes que faire ?
        }
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, 3000, 2, locationListener);
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

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
        // TODO mettre en pause ce qui peut être mis en pause
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
        // TODO resume ce qui doit être resume
    }

    /**
     * Ajoute un point d'intérêt au parcours et l'affiche sur la carte.
     * @param interestPoint point d'intérêt
     */
    private void addInterestPoint(InterestPoint interestPoint) {
        // Ajoute le point d'intérêt au parcours
        parcours.addInterestPoint(interestPoint);

        // Affiche le point d'intérêt sur la carte
        Marker marker = new Marker(map);
        marker.setPosition(interestPoint.getPoint());
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(interestPoint.getTitle());// TODO voir si on affiche le titre ou la description
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                // TODO ouvrir la popup de information/modification du point ?????
                Toast.makeText(getApplicationContext(), "Ceci est un point d'interet", Toast.LENGTH_SHORT).show();
                // showInterestPointPopup(interestpoint) un truc comme ça
                return true;
            }
        });
        map.getOverlays().add(marker);
    }

    /**
     * Exécuté quand l'utilisateur appuie sur le bouton "ajouter un point d'intérêt".
     * Crée un nouveau parcours et lance le chronomètre de ce parcours.
     */
    private void ClickAddInterestPoint() {
        //TODO la popup doit renvoyer à l'activité le nom et description saisie par l'utilisateur
        // TODO appeler la méthode addInterestPoint(interestPoint);

    }

    /**
     * Exécuté quand l'utilisateur appuie sur le bouton "Arrêter".
     */
    private void ClickStop() {
        //TODO désactiver tout ce qui est désactivable ??
/*
            locationManager.removeUpdates(ecouteurGPS); // TODO utile ?????
            ecouteurGPS = null;*/

        // TODO Sauvegarder parcours
        SaveParcours();
    }


    /**
     * Envoie le parcours à l'API, si erreur, enregistre le parcours en local.
     */
    private void SaveParcours() {
        //TODO Méthode API pour enregistrer le parcours
    }
}