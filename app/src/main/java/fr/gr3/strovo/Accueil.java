package fr.gr3.strovo;

import androidx.appcompat.widget.SearchView;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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
                // Gérez le clic sur le bouton de filtre ici
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
                // Gérez le clic sur le bouton d'action flottant ici
            }
        });
    }
}

