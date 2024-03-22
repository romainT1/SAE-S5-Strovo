package fr.gr3.strovo.api;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fr.gr3.strovo.api.model.Parcours;
import fr.gr3.strovo.api.model.User;

/**
 * Classe permettant de communiquer avec l'api
 */
public class StrovoApi {

    /** URL de l'api strovo */
    private static final String API_URL = "http://158.178.195.92:8080";

    /** Point de terminaison pour l'enregistrement d'un utilisateur  */
    public static final String SIGNUP_URL = API_URL + "/user/signup";

    /** Point de terminaison pour l'identification d'un utilisateur  */
    public static final String LOGIN_URL = API_URL + "/user/login?email=%s&password=%s";

    /** Point de terminaison pour récupérer la liste des parcours d'un utilisateur */
    public static final String GET_PARCOURS = API_URL + "/parcours";

    /** Point de terminaison pour ajouter un parcours à un utilisateur */
    public static final String ADD_PARCOURS = API_URL + "/parcours";

    /** Point de terminaison pour supprimer un parcours d'un utilisateur */
    public static final String DELETE_PARCOURS = API_URL + "/parcours/%s";
    public static final String GET_PARCOURS_BY_ID = API_URL + "/parcours/%s";

    /** Point de terminaison pour mettre à jour un parcours d'un utilisateur */
    public static final String UPDATE_PARCOURS = API_URL + "/parcours/%s" ;


    /** Singleton */
    private static StrovoApi instance;

    /**
     * Constructeur privé.
     */
    private StrovoApi() { }

    /**
     * Renvoie le singleton de la classe ApiStrovo.
     * @return l'instance de la classe
     */
    public static StrovoApi getInstance() {
        if (instance == null) {
            instance = new StrovoApi();
        }
        return instance;
    }

    /**
     * Construit une requête pour enregistrer un utilisateur.
     * @param user utilisateur à enregistrer
     * @param responseListener Response.Listener exécuté en cas de succès
     * @param errorListener Response.ErrorListener exécuté en cas d'échec
     * @return une requête d'enregistrement de l'utilisateur
     * @throws JSONException si problème lors de la conversion de l'utilisateur en JSONObject
     */
    public JsonObjectRequest registerUser(User user, Response.Listener<JSONObject> responseListener,
                                          Response.ErrorListener errorListener) throws JSONException {

        return new JsonObjectRequest(Request.Method.POST, SIGNUP_URL, user.toJson(),
                        responseListener, errorListener);
    }

    /**
     * Construit une requête pour identifier un utilisateur.
     * @param email adresse mail de l'utilisateur
     * @param password mot de passe de l'utilisateur
     * @param responseListener Response.Listener exécuté en cas de succès
     * @param errorListener Response.ErrorListener exécuté en cas d'échec
     * @return une requête d'identification de l'utilisateur
     */
    public JsonObjectRequest login(String email, String password,
                                   Response.Listener<JSONObject> responseListener,
                                   Response.ErrorListener errorListener) {

        String apiUrl = String.format(LOGIN_URL, email, password);

        return new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                responseListener, errorListener);
    }

    /**
     * Construit une requête pour récupérer la listes des parcours d'un utilisateur.
     * @param token jeton de connexion de l'utilisateur
     * @param responseListener Response.Listener exécuté en cas de succès
     * @param errorListener Response.ErrorListener exécuté en cas d'échec
     * @return une requête de récupération des parcours de l'utilisateur
     */
    public JsonArrayRequest getParcours(String token, Response.Listener<JSONArray> responseListener,
                                        Response.ErrorListener errorListener) {

        return new JsonArrayRequest(Request.Method.GET, GET_PARCOURS, null,
                responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                return createAuthorizationHeader(token);
            }
        };
    }

    /**
     * Construit une requête pour ajouter un parcours à un utilisateur.
     * @param token jeton de connexion de l'utilisateur
     * @param parcours parcours à ajouter
     * @param responseListener Response.Listener exécuté en cas de succès
     * @param errorListener Response.ErrorListener exécuté en cas d'échec
     * @return une requête d'ajout d'un parcours à l'utilisateur
     * @throws JSONException si problème lors de la conversion du parcours en JSONObject
     */
    public JsonObjectRequest addParcours(String token, Parcours parcours,
                                         Response.Listener<JSONObject> responseListener,
                                         Response.ErrorListener errorListener) throws JSONException {

        return new JsonObjectRequest(Request.Method.POST, ADD_PARCOURS, parcours.toJson(),
                                     responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                return createAuthorizationHeader(token);
            }
        };
    }

    /**
     * Construit une requête pour supprimer un parcours de la listes des parcours d'un utilisateur.
     * @param token jeton de connexion de l'utilisateur
     * @param parcoursId identifiant du parcours à supprimer
     * @param responseListener Response.Listener exécuté en cas de succès
     * @param errorListener Response.ErrorListener exécuté en cas d'échec
     * @return une requête de suppression d'un parcours de l'utilisateur
     */
    public StringRequest deleteParcours(String token, String parcoursId,
                                        Response.Listener responseListener,
                                        Response.ErrorListener errorListener) {

        String apiUrl = String.format(DELETE_PARCOURS, parcoursId);

        return new StringRequest(Request.Method.DELETE, apiUrl, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                return createAuthorizationHeader(token);
            }
        };
    }

    /**
     * Construit une requête pour récupérer un parcours d'un utilisateur à partir de l'identifiant
     * du parcours.
     * @param token jeton de connexion de l'utilisateur
     * @param parcoursId identifiant du parcours à récupérer
     * @param responseListener Response.Listener exécuté en cas de succès
     * @param errorListener Response.ErrorListener exécuté en cas d'échec
     * @return une requête de récupération d'un parcours de l'utilisateur
     */
    public JsonObjectRequest getParcoursById(String token, String parcoursId,
                                             Response.Listener<JSONObject> responseListener,
                                             Response.ErrorListener errorListener) {

        String apiUrl = String.format(GET_PARCOURS_BY_ID, parcoursId);

        return  new JsonObjectRequest(Request.Method.GET, apiUrl, null, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                return createAuthorizationHeader(token);
            }
        };
    }

    /**
     * Construit une requête pour mettre à jour un parcours d'un utilisateur.
     * @param token jeton de connexion de l'utilisateur
     * @param parcours parcours à modifier
     * @param responseListener Response.Listener exécuté en cas de succès
     * @param errorListener Response.ErrorListener exécuté en cas d'échec
     * @return une requête de mise à jour d'un parcours de l'utilisateur
     */
    public JsonObjectRequest updateParcours(String token, Parcours parcours,
                                            Response.Listener responseListener,
                                            Response.ErrorListener errorListener) {

        String apiUrl = String.format(UPDATE_PARCOURS, parcours.getId());

        // Crée un objet JSON avec la nouvelle description du parcours
        JSONObject parcoursJson = new JSONObject();
        try {
            parcoursJson.put("description", parcours.getDescription());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JsonObjectRequest(Request.Method.PUT, apiUrl, parcoursJson, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                return createAuthorizationHeader(token);
            }
        };
    }

    /**
     * Crée le header de la requête avec le jeton de connexion api de l'utilisateur.
     * @param token jeton de connexion api
     * @return
     */
    public Map<String, String> createAuthorizationHeader(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", token);
        return headers;
    }
}