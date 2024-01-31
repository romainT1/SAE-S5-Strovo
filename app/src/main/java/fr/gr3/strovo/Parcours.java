package fr.gr3.strovo;

public class Parcours {

    String nom;
    String date_heure;

    public Parcours(String nom, String date_heure) {
        this.nom = nom;
        this.date_heure = date_heure;
    }

    public String getNom() {
        return nom;
    }

    public String getDateHeure() {
        return date_heure;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDateHeure(String date) {
        this.date_heure = date;
    }
}
