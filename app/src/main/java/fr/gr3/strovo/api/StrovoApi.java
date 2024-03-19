package fr.gr3.strovo.api;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import fr.gr3.strovo.api.model.User;

public class StrovoApi {

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
     * Construit un requête pour enregistrer un utilisateur.
     * @param user utilisateur à enregistrer
     * @param responseListener Response.Listener exécuté en cas de succès
     * @param errorListener Response.ErrorListener exécuté en cas d'échec
     * @return une requête d'enregistrement de l'utilisateur
     * @throws JSONException si problème lors de la conversion de l'utilisateur en JSONObject
     */
    public JsonObjectRequest registerUser(User user, Response.Listener responseListener,
                                          Response.ErrorListener errorListener) throws JSONException {

        return new JsonObjectRequest(Request.Method.POST, Endpoints.SIGNUP_URL, user.toJson(),
                        responseListener, errorListener);
    }

    /**
     * Construit un requête pour identifier un utilisateur.
     * @param email adresse mail de l'utilisateur
     * @param password mot de passe de l'utilisateur
     * @param responseListener Response.Listener exécuté en cas de succès
     * @param errorListener Response.ErrorListener exécuté en cas d'échec
     * @return une requête d'identification de l'utilisateur
     */
    public JsonObjectRequest login(String email, String password, Response.Listener responseListener,
                                   Response.ErrorListener errorListener) {

        String apiUrl = String.format(Endpoints.LOGIN_URL, email, password);

        return new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                responseListener, errorListener);
    }
}