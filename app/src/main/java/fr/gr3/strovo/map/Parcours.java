package fr.gr3.strovo.map;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Représente un parcours.
 */
public class Parcours {

    /** Indique si le parcours est en cours d'enregistrement */
    private boolean running;

    /** Indique si le parcours est en pause */
    private boolean paused;

    /** Nom du parcours */
    private String nom;

    /** Description du parcours. */
    private String description;
    
    /** Date de création du parcours */
    private Date date;

    /** Durée du parcours */
    private long time;

    /** Vitesse moyenne */
    private float speed;

    /** Distance parcourue en mètres */
    private float distance;

    /** Dénivelé parcouru */
    private double elevation;

    /** Liste de touts les points d'intérêts enregistrés pour le parcours */
    private List<InterestPoint> interestPoints;

    /** Liste de toutes les positions enregistrées pour le parcours */
    private List<Location> locations;

    /**
     * Construit un parcours.
     */
    public Parcours(String nom, String description, Date date) {
        this.running = false;
        this.nom = nom;
        this.description = description;
        this.date = date;
        this.time = 0;
        this.speed = 0.0f;
        this.distance = 0.0f;
        this.elevation = 0.0f;
        this.interestPoints = new ArrayList<>();
        this.locations = new ArrayList<>();
    }

    /**
     * @return true si enregistrement en cours, false sinon
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Ajoute une nouvelle position au parcours.
     * Compare la nouvelle position avec la dernière enregistrée dans le parcours.
     * Si les deux positions ont au moins 2 mètres d'écart, alors la nouvelle position est enregistrée.
     * Sinon l'enregistrement est annulé.
     * @param location nouvelle position
     */
    public void addLocation(Location location) {
        // Si c'est la première position du parcours ou si l'utilisateur s'est déplacé d'au moins 2 mètres
        if (locations.size() == 0 || location.distanceTo(locations.get(locations.size() - 1)) >= 2) {
            locations.add(location);
        }
    }

    /**
     * Ajoute un point d'intérêt au parcours
     * @param interestPoint point d'intérêt
     */
    public void addInterestPoint(InterestPoint interestPoint) {
        interestPoints.add(interestPoint);
    }

    public void start() {
        running = true;
        // TODO intégrer le timer ICI
    }

    public void stop() {
        // TODO arreter le timer ICI A LA PLACE DU TODO ATTENTION à bien l'arreter avant de calculer les stats
        running = false;
        calculateStatistics();
    }


    /**
     * Calcule les statistiques du parcours.
     */
    private void calculateStatistics() {
        // Si il y a plus d'une position enregistrée
        if (locations.size() > 1) {
            // Calcul de la distance
            for (int i = 0; i < locations.size()-1; i++) {
                Location actual = locations.get(i);
                Location next = locations.get(i+1);
                distance += actual.distanceTo(next);
            }

            // Calcul de la vitesse
            speed = distance / time;

            // Calcul du dénivelé
            Location firstLocation = locations.get(0);
            Location lastLocation = locations.get(locations.size()-1);
            elevation = lastLocation.getAltitude() - firstLocation.getAltitude();
        }
    }

    /**
     * Met le parcours en pause.
     */
    public void pause() {
        paused = true;
        // Ajoutez ici tout ce qui doit être mis en pause, comme un timer si vous en avez un
    }

    /**
     * Reprend le parcours.
     */
    public void resume() {
        paused = false;
        // Ajoutez ici tout ce qui doit être repris, comme un timer si vous en avez un
    }

    /**
     * Vérifie si le parcours est en pause.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Convertit le parcours en objet json.
     * @return un objet Json
     * @throws JSONException si une erreur se produit durant la conversion
     */
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", nom);
        jsonObject.put("description", description);
        jsonObject.put("date", date);
        jsonObject.put("time", time);
        jsonObject.put("averageSpeed", speed);
        jsonObject.put("distance", distance);
        jsonObject.put("elevation", elevation);
        jsonObject.put("interestPoints", new JSONArray(interestPoints).toString());

        List<Double[]> coordinates = new ArrayList<>();
        for (Location location : locations) {
            coordinates.add(new Double[]{location.getLatitude(), location.getLongitude()});
        }
        jsonObject.put("coordinates", new JSONArray(coordinates).toString());
        
        return jsonObject;
    }
}
