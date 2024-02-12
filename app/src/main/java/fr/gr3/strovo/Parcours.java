package fr.gr3.strovo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Parcours {

    String id;
    String nom;
    String date_heure;
    String description;

    public Parcours(String nom, String date_heure, String decription, String id) {
        this.id = id;
        this.nom = nom;
        this.date_heure = date_heure;
        this.description = decription;
    }

    public Parcours(String nom, String date_heure, String decription) {
        this.nom = nom;
        this.date_heure = date_heure;
        this.description = decription;
    }
    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDateHeure() {
        // Créer un objet SimpleDateFormat pour le format d'entrée
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        // Créer un objet SimpleDateFormat pour le format de sortie
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            // Convertir la chaîne en objet Date
            Date date = inputFormat.parse(date_heure);

            // Formater la date
            String formattedDate = outputFormat.format(date);

            return formattedDate;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDateHeure(String date) {
        this.date_heure = date;
    }
}
