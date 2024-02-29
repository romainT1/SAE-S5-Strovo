package fr.gr3.strovo.map;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un parcours.
 */
public class Parcours {

    /** Indique si l'enregistrement du parcours est en cours */
    private boolean running;

    /** Liste de toutes les positions enregistrées pour le parcours */
    private List<Location> locations;

    /** Liste de touts les points d'intérêts enregistrées pour le parcours */
    private List<InterestPoint> interestPoints;

    /** Durée du parcours */
    private float time;

    /** Distance parcourue en mètre */
    private float distance;

    /** vitesse moyenne */
    private float speed;

    /**
     * Construit un parcours.
     */
    public Parcours() {
        running = false;
        locations = new ArrayList<>();
        interestPoints = new ArrayList<>();
        time = 0.0f;
        distance = 0.0f;
        speed = 0.0f;
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
                distance += locations.get(i).distanceTo(locations.get(i+1));
            }

            // Calcul de la vitesse
            speed = distance / time;

            // TODO calculer dénivelé
        }
    }

    /**
     * @return distance parcourue
     */
    public float getDistance() {
        return distance;
    }

    /**
     * @return vitesse moyenne
     */
    public float getSpeed() {
        return speed;
    }
}
