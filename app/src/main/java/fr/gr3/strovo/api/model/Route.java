package fr.gr3.strovo.api.model;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import fr.gr3.strovo.map.InterestPoint;

public class Route {

    /** Identifiant de l'utilisateur. */
    private int userId;

    /** Nom du parcours. */
    private String name;

    /** Description du parcours. */
    private String description;

    /** Date d'enregistrement du parcours. */
    private final Date date;

    /** Durée du parcours en millisecondes. */
    private final long time;

    /** Vitesse moyenne. */
    private final float speed;

    /** Distance parcourue en mètre. */
    private final float distance;

    /** Dénivelé du parcours. */
    private final double elevation;

    /** Liste des identifiants des points d'intêrets associés au parcours. */
    private InterestPoint[] interestPoints;

    /** Listes des coordonnées des points enregistrés formant le parcours. */
    private final double[][] coordinates;

    public Route(String name, String description, Date date, long time, float speed, float distance, double elevation, InterestPoint[] interestPoints, double[][] coordinates) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.speed = speed;
        this.distance = distance;
        this.elevation = elevation;
        this.interestPoints = interestPoints;
        this.coordinates = coordinates;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public float getSpeed() {
        return speed;
    }

    public float getDistance() {
        return distance;
    }

    public double getElevation() {
        return elevation;
    }

    public InterestPoint[] getInterestPoints() {
        return interestPoints;
    }

    public void setInterestPoints(InterestPoint[] interestPoints) {
        this.interestPoints = interestPoints;
    }

    public double[][] getCoordinates() {
        return coordinates;
    }
    /**
     * Convertit le parcours en objet json.
     * @return un objet Json
     * @throws JSONException si une erreur se produit durant la conversion
     */
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        jsonObject.put("name", name);
        jsonObject.put("description", description);
        jsonObject.put("date", new SimpleDateFormat("yyyy-MM-dd").format(date));
        jsonObject.put("time", time);
        jsonObject.put("speed", speed);
        jsonObject.put("distance", distance);
        jsonObject.put("elevation", elevation);

        // Conversion des points d'intêrets
        JSONArray jsonInterestPoints = new JSONArray();
        for (InterestPoint interestPoint : interestPoints) {
            jsonInterestPoints.put(interestPoint.toJson());
        }
        jsonObject.put("interestPoints", jsonInterestPoints);

        // Convertion des coordonnées
        JSONArray jsonCoordinates = new JSONArray();
        for (double[] coordinatePoint : coordinates) {
            jsonCoordinates.put(new JSONArray()
                    .put(coordinatePoint[0])
                    .put(coordinatePoint[1]));
        }
        jsonObject.put("coordinates", jsonCoordinates);

        return jsonObject;
    }
}