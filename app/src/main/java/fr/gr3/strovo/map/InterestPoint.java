package fr.gr3.strovo.map;

import org.osmdroid.util.GeoPoint;

/** Représente un point d'intérêt sur un parcours. */
public class InterestPoint {

    /** Coordonnées du point. */
    private GeoPoint point;

    /** Titre du point d'intérêt. */
    private String title;

    /** Description du point d'intérêt. */
    private String description;


    /**
     * Contruit un point d'itérêt.
     * @param point coordonnées du point
     * @param title titre du point
     * @param description description du point
     */
    public InterestPoint(GeoPoint point, String title, String description) {
        this.point = point;
        this.title = title;
        this.description = description;
    }

    /**
     * @return les coordonnées du point
     */
    public GeoPoint getPoint() {
        return point;
    }

    /**
     * @return le titre du point
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return description du point
     */
    public String getDescription() {
        return description;
    }
}
