package fr.gr3.strovo.api.model;

import java.util.Date;

public class Route {

    /** Nom du parcours. */
    private String name;

    /** Description du parcours. */
    private String description;

    /** Date d'enregistrement du parcours. */
    private final Date date;

    /** Durée du parcours en millisecondes. */
    private final long time;

    /** Vitesse moyenne. */
    private final float averageSpeed;

    /** Distance parcourue en mètre. */
    private final float distance;

    /** Dénivelé du parcours. */
    private final double elevation;

    /** Liste des identifiants des points d'intêrets associés au parcours. */
    private String[] interestPointsIds;

    /** Listes des coordonnées des points enregistrés formant le parcours. */
    private final double[][] coordinates;

    public Route(String name, String description, Date date, long time, float averageSpeed, float distance, double elevation, String[] interestPointsIds, double[][] coordinates) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.averageSpeed = averageSpeed;
        this.distance = distance;
        this.elevation = elevation;
        this.interestPointsIds = interestPointsIds;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public long getTime() {
        return time;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public float getDistance() {
        return distance;
    }


    public double getElevation() {
        return elevation;
    }


    public String[] getInterestPointsIds() {
        return interestPointsIds;
    }

    public void setInterestPointsIds(String[] interestPointsIds) {
        this.interestPointsIds = interestPointsIds;
    }

    public double[][] getCoordinates() {
        return coordinates;
    }
}