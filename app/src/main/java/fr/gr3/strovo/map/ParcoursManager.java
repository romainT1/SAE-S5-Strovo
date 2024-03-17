package fr.gr3.strovo.map;

import android.location.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.gr3.strovo.api.model.Parcours;

/**
 * Représente un parcours.
 */
public class ParcoursManager {

    /** Indique si le parcours est en cours d'enregistrement */
    private boolean running;

    /** Indique si le parcours est en pause */
    private boolean paused;

    /** Parcours de l'utilisateur */
   private Parcours parcours;

    /** Liste de toutes les positions enregistrées pour le parcours */
    private List<Location> locations;

    /**
     * Construit un parcours.
     */
    public ParcoursManager(String name, String description, Date date) {
        this.running = false;
        this.parcours = new Parcours(name, description, date);
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
            parcours.getCoordinates().add(new double[] {location.getLatitude(), location.getLongitude()});
        }
    }

    /**
     * Ajoute un point d'intérêt au parcours
     * @param interestPoint point d'intérêt
     */
    public void addInterestPoint(InterestPoint interestPoint) {
        parcours.getInterestPoints().add(interestPoint);
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
     * Calcule les statistiques du parcours effectué par l'utilisateur.
     */
    private void calculateStatistics() {
        // Si il y a plus d'une position enregistrée
        if (locations.size() > 1) {
            long time = 1; // TODO changer
            float distance = calculateDistance();
            double elevation = calculateElevation();

            parcours.setTime(time);
            parcours.setDistance(distance);
            parcours.setSpeed(distance / time);
            parcours.setElevation(elevation);
        }
    }

    /**
     * Calcule la distance parcourue.
     * @return le distance en mètres
     */
    private float calculateDistance() {
        float distance = 0;
        for (int i = 0; i < locations.size()-1; i++) {
            Location actual = locations.get(i);
            Location next = locations.get(i+1);
            distance += actual.distanceTo(next);
        }
        return distance;
    }


    /**
     * Calcule le dénivelé du parcours.
     * @return le dénivelé en mètres
     */
    private double calculateElevation() {
        Location firstLocation = locations.get(0);
        Location lastLocation = locations.get(locations.size()-1);
        return lastLocation.getAltitude() - firstLocation.getAltitude();
    }

    public Parcours getParcours() {
        return parcours;
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
