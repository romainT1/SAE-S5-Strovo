package fr.gr3.strovo.api;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

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
    public static final String GET_PARCOURS = API_URL + "/parcours";
    public static final String ADD_PARCOURS = API_URL + "/parcours";
    public static final String DELETE_PARCOURS = API_URL + "/parcours/%s";
    public static final String GET_PARCOURS_BY_ID = API_URL + "/parcours/%s";
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
    public JsonObjectRequest registerUser(User user, Response.Listener responseListener,
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
    public JsonObjectRequest login(String email, String password, Response.Listener responseListener,
                                   Response.ErrorListener errorListener) {

        String apiUrl = String.format(LOGIN_URL, email, password);

        return new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                responseListener, errorListener);
    }

    /**
     * Construit une requête pour récupérer la listes des parcours d'un utilisateur
     * @param token jeton de connexion de l'utilisateur
     * @param responseListener Response.Listener exécuté en cas de succès
     * @param errorListener Response.ErrorListener exécuté en cas d'échec
     * @return un requête de récupération des parcours de l'utilisateur
     */
    public JsonArrayRequest getParcours(String token, Response.Listener responseListener,
                                 Response.ErrorListener errorListener) {

        return new JsonArrayRequest(Request.Method.GET, GET_PARCOURS, null,
                responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }
        };
    }
}