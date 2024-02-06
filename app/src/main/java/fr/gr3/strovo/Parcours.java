package fr.gr3.strovo;

import java.util.Date;

public class Parcours {

    String nom;
    String date_heure;
    String description;

    public Parcours(String nom, String date_heure, String decription) {
        this.nom = nom;
        this.date_heure = date_heure;
        this.description = decription;
    }

    public String getNom() {
        return nom;
    }

    public String getDateHeure() {
        return date_heure;
    }

    public String getDescription() {
        return description;
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
