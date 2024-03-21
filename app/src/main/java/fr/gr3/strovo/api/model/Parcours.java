package fr.gr3.strovo.api.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.gr3.strovo.map.InterestPoint;

public class Parcours {

    /** Identifiant du parcours */
    private String id;

    /** Nom du parcours. */
    private String name;

    /** Description du parcours. */
    private String description;

    /** Date d'enregistrement du parcours. */
    private final Date date;

    /** Durée du parcours en millisecondes. */
    private long time;

    /** Vitesse moyenne. */
    private float speed;

    /** Distance parcourue en mètres. */
    private double distance;

    /** Dénivelé du parcours. */
    private double elevation;

    /** Liste des identifiants des points d'intêrets associés au parcours. */
    private List<InterestPoint> interestPoints;

    /** Listes des coordonnées des points enregistrés formant le parcours. */
    private List<double[]> coordinates;

    /**
     * Construit un parcours.
     *
     * @param name nom du parcours
     * @param description description du parcours
     * @param date date du parcours
     * @param time durée du parcours
     * @param speed vitesse du parcours
     * @param distance distance parcourue
     * @param elevation dénivelé du parcours
     * @param interestPoints liste des points d'intêrets associés au parcours
     * @param coordinates liste de coordonnées de points formant le parcours
     */
    public Parcours(String name, String description, Date date,
                    long time, float speed, double distance, double elevation,
                    List<InterestPoint> interestPoints, List<double[]> coordinates) {
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

    /**
     * Construit un parcours.
     *
     * @param id identifiant du parcours
     * @param name nom du parcours
     * @param description description du parcours
     * @param date date du parcours
     */
    public Parcours(String id, String name, String description, Date date) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = 0;
        this.speed = 0;
        this.distance = 0;
        this.elevation = 0;
        this.interestPoints = new ArrayList<>();
        this.coordinates = new ArrayList<>();
    }

    /**
     * Construit un parcours.
     *
     * @param name nom du parcours
     * @param description description du parcours
     * @param date date du parcours
     */
    public Parcours(String name, String description, Date date) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = 0;
        this.speed = 0;
        this.distance = 0;
        this.elevation = 0;
        this.interestPoints = new ArrayList<>();
        this.coordinates = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setTime(long time) {
        this.time = time;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return (int) distance;
    }

    public int getElevation() {
        return (int) elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public List<InterestPoint> getInterestPoints() {
        return interestPoints;
    }

    public void setInterestPoints(List<InterestPoint> interestPoints) {
        this.interestPoints = interestPoints;
    }

    public List<double[]> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<double[]> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Convertit le parcours en objet json.
     * @return un objet Json
     * @throws JSONException si une erreur se produit durant la conversion
     */
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("description", description);
        jsonObject.put("date", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(date));
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
        for (double[] coordinatesPoint : coordinates) {
            jsonCoordinates.put(new JSONArray()
                    .put(coordinatesPoint[0])
                    .put(coordinatesPoint[1]));
        }
        jsonObject.put("coordinates", jsonCoordinates);

        return jsonObject;
    }
}