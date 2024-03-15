package fr.gr3.strovo.map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

/** Représente un point d'intérêt sur un parcours. */
public class InterestPoint {

    /** Coordonnées du point. */
    private GeoPoint point;

    /** Nom du point d'intérêt. */
    private String name;

    /** Description du point d'intérêt. */
    private String description;


    /**
     * Contruit un point d'itérêt.
     * @param point coordonnées du point
     * @param name titre du point
     * @param description description du point
     */
    public InterestPoint(GeoPoint point, String name, String description) {
        this.point = point;
        this.name = name;
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
    public String getName() {
        return name;
    }

    /**
     * @return description du point
     */
    public String getDescription() {
        return description;
    }

    /**
     * Convertit un point d'intêret en objet json.
     * @return un objet Json
     * @throws JSONException si une erreur se produit durant la conversion
     */
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("description", description);
        double[] coordinates = new double[]{point.getLatitude(), point.getLongitude()};
        jsonObject.put("coordinates", new JSONArray(coordinates));

        return jsonObject;
    }
}
