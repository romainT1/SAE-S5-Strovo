package fr.gr3.strovo.map;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un parcours.
 */
public class Parcours {


    /** Indique si le parcours est en cours d'enregistrement */
    private boolean running;

    /** Indique si le parcours est en pause */
    private boolean paused;

    /** Liste de toutes les positions enregistrées pour le parcours */
    private List<Location> locations;

    /** Liste de touts les points d'intérêts enregistrés pour le parcours */
    private List<InterestPoint> interestPoints;

    /** Durée du parcours */
    private long time;

    /** Vitesse moyenne */
    private float speed;

    /** Distance parcourue en mètres */
    private float distance;

    /** Dénivelé parcouru */
    private double elevation;

    /**
     * Construit un parcours.
     */
    public Parcours() {
        running = false;
        locations = new ArrayList<>();
        interestPoints = new ArrayList<>();
        time = 0;
        speed = 0.0f;
        distance = 0.0f;
        elevation = 0.0f;
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
     * @return durée du parcours
     */
    public long getTime() {
        return time;
    }

    /**
     * @return vitesse moyenne
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @return distance parcourue
     */
    public float getDistance() {
        return distance;
    }

    /**
     * @return dénivelé parcouru
     */
    public double getElevation() {
        return elevation;
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
}
