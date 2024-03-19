package fr.gr3.strovo.api;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

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

    public JsonObjectRequest registerUser(User user, Response.Listener responseListener,
                                   Response.ErrorListener errorListener) throws JSONException {
        return new JsonObjectRequest(Request.Method.POST, Endpoints.SIGNUP_URL, user.toJson(),
                        responseListener, errorListener);
    }
}