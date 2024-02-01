package fr.gr3.strovo;

import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.app.Dialog;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Accueil extends AppCompatActivity {

    private SearchView searchView;
    private Button filterButton;
    private ListView listView;
    private Button floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        searchView = findViewById(R.id.search_view);
        filterButton = findViewById(R.id.filter_button);
        listView = findViewById(R.id.list_view);
        floatingActionButton = findViewById(R.id.floating_action_button);

        ArrayList<Parcours> listItems = new ArrayList<>();
        listItems.add(new Parcours("parcours1","12/12/2023 18:00"));
        listItems.add(new Parcours("parcours2","12/12/2023 18:00"));
        listItems.add(new Parcours("parcours3","12/12/2023 18:00"));
//        listItems.add("\n1er parcours \n12/12/2023 18:00\n");
//        listItems.add("\n2e parcours \n13/12/2023 18:00\n");
//        listItems.add("\n3e parcours \n14/12/2023 18:00\n");
//        listItems.add("\n5e parcours \n15/12/2023 18:00\n");
//        listItems.add("\n5e parcours \n15/12/2023 18:00\n");
//        listItems.add("\n5e parcours \n15/12/2023 18:00\n");
//        listItems.add("\n5e parcours \n15/12/2023 18:00\n");
//        listItems.add("\n5e parcours \n15/12/2023 18:00\n");
//        listItems.add("\n5e parcours \n15/12/2023 18:00\n");
//        listItems.add("\n5e parcours \n15/12/2023 18:00\n");
//        listItems.add("\n5e parcours \n15/12/2023 18:00\n");
//        listItems.add("\n5e parcours \n15/12/2023 18:00\n");
//        listItems.add("\n5e parcours \n15/12/2023 18:00\n");
//        listItems.add("\n5e parcours \n15/12/2023 18:00\n");
//        listItems.add("\n5e parcours \n15/12/2023 18:00\n");


        ParcoursAdapter adapter = new ParcoursAdapter(this, R.layout.vue_item_liste, listItems);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Gérez la soumission de la requête de recherche ici
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Gérez le changement de texte de recherche ici
                return false;
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crée une instance de Dialog
                final Dialog dialog = new Dialog(Accueil.this);

                // Définis le contenu de la fenêtre contextuelle
                dialog.setContentView(R.layout.popup_filtre);

                // Récupère les éléments de la fenêtre contextuelle
                EditText inputMois = dialog.findViewById(R.id.inputMois);
                EditText inputDureeMin = dialog.findViewById(R.id.inputDureeMin);
                EditText inputDureeMax = dialog.findViewById(R.id.inputDureeMax);

                Button rechercher = dialog.findViewById(R.id.btnRechercher);
                Button annuler = dialog.findViewById(R.id.btnAnnuler);

                // Gère le clic sur le bouton "rechercher"
                rechercher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO faire le lien a l'api
                        dialog.dismiss();
                    }
                });

                // Gérez le clic sur le bouton "Annuler"
                annuler.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Gérez le clic sur le bouton "Annuler" ici
                        dialog.dismiss();
                    }
                });

                // Affichez la fenêtre contextuelle
                dialog.show();
            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Gérez le clic sur un élément de la liste ici
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créez une instance de Dialog
                final Dialog dialog = new Dialog(Accueil.this);

                // Définissez le contenu de la fenêtre contextuelle
                dialog.setContentView(R.layout.popup_lancer_course);

                // Récupérez les éléments de la fenêtre contextuelle
                EditText inputCommentaire = dialog.findViewById(R.id.inputCommentaire);
                Button confirmer = dialog.findViewById(R.id.confirmer);
                Button annuler = dialog.findViewById(R.id.annuler);

                // Gérez le clic sur le bouton "Confirmer"
                confirmer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Gérez le clic sur le bouton "Confirmer" ici
                        Intent intent = new Intent(Accueil.this, MapActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                // Gérez le clic sur le bouton "Annuler"
                annuler.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Gérez le clic sur le bouton "Annuler" ici
                        dialog.dismiss();
                    }
                });

                // Affichez la fenêtre contextuelle
                dialog.show();
            }
        });
    }
}

