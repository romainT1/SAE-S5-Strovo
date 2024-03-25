package fr.gr3.strovo.utils.parcours;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fr.gr3.strovo.R;
import fr.gr3.strovo.api.model.Parcours;

public class ParcoursAdapter extends ArrayAdapter<Parcours> {

    /** Identifiant de la vue permettant d'afficher chaque item de la liste */
    private int identifiantVueItem;
    /**
     * Objet utilitaire permettant de désérialiser une vue
     */
    private LayoutInflater inflater;
    /** Regroupe les 2 TextView présents sur la vue d'un item de la liste */
    static class SauvegardeTextView {
        TextView nomParcours;
        TextView dateHeure;
    }
    /**
     * Constructeur de l'adaptateur
     * @param contexte contexte de création de l'adaptateur
     * @param vueItem identifiant de la vue permettant d'afficher chaque
     * item de la liste
     * @param lesItems Liste de items à afficher
     */
    public ParcoursAdapter(Context contexte, int vueItem,
                                 List<Parcours> lesItems) {
        super(contexte, vueItem, lesItems);
        this.identifiantVueItem = vueItem;
        inflater = (LayoutInflater)getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    /**
     * Permet d'affecter à chaque item de la liste, les valeurs qui
     * doivent être affichées
     * @param position position de l'élément qui doit être affiché
     * (position au sein de la liste associée à l'adaptateur)
     * @param uneVue contient soit la valeur null, soit une ancienne vue
     * pour l'élément à afficher. La méthode pourra alors se
     * contenter de réactualiser cette vue
     * @param parent vue parente à laquelle la vue à renvoyer peut être rattachée
     * @return une vue qui affichera les informations adéquates dans l'item de la liste
     * situé à la position p
     */
    @Override
    public View getView(int position, View uneVue, ViewGroup parent) {
        // on récupère la valeur de l'item à afficher, via sa position
        Parcours parcours = getItem(position);
        LinearLayout vueItemListe; // layout décrivant un item de la liste
        SauvegardeTextView sauve; // regroupe les 2 TextView présent sur la vue
        // destinée à afficher l'item
        if (uneVue == null) {
            /*
             * la vue décrivant chaque item de la liste n'est pas encore créée
             * Il faut désérialiser le layout correspondant à cette vue.
             */
            uneVue = inflater.inflate(identifiantVueItem, parent, false);
            // on récupère un accès sur les 2 TextView qu'il faudra renseigner
            sauve = new SauvegardeTextView();
            sauve.nomParcours = uneVue.findViewById(R.id.nom_parcours);
            sauve.dateHeure = uneVue.findViewById(R.id.date_heure);
            // on stocke les identifiants de 2 TextView dans la vue elle-même
            uneVue.setTag(sauve);
        } else {
            // on récupère les identifiants des 2 TextView stockés dans la vue
            sauve = (SauvegardeTextView) uneVue.getTag();
        }
        // on place dans les 2 TextView les valeurs de l'item à afficher
        sauve.nomParcours.setText(parcours.getName());


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(parcours.getDate());
        sauve.dateHeure.setText(formattedDate);
        return uneVue;
    }
}

