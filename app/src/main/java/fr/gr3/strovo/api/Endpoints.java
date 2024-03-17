package fr.gr3.strovo.api;

public class Endpoints {

    private static final String API_URL = "http://158.178.195.92:8080";

    public static final String SIGNUP_URL = API_URL + "/user/signup";
    public static final String LOGIN_URL = API_URL + "/user/login?email=%s&password=%s";
    public static final String GET_PARCOURS = API_URL + "/parcours";
    public static final String ADD_PARCOURS = API_URL + "/parcours";
    public static final String DELETE_PARCOURS = API_URL + "/parcours/%s";
    public static final String GET_PARCOURS_BY_ID = API_URL + "/parcours/%s";

}
