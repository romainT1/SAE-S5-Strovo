package fr.gr3.strovo;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapActivity extends AppCompatActivity {
    private MapView mapView;
    private IMapController mapController;
    private Button btnAjouter;
    private Button btnArreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapCourse);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        btnAjouter = findViewById(R.id.btnAjouter);
        btnArreter = findViewById(R.id.btnArreter);
        mapController = mapView.getController();
        mapController.setZoom(18.0);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Test verif permission", Toast.LENGTH_LONG).show();
            initializeMapLocation();
        } else {
            Toast.makeText(this, "Test verif pas ok", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        final Dialog dialog = new Dialog(MapActivity.this);

        btnAjouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créez une instance de Dialog
                final Dialog dialog = new Dialog(MapActivity.this);

                // Définissez le contenu de la fenêtre contextuelle
                dialog.setContentView(R.layout.popup_point_interet);
                EditText inputLibelle = dialog.findViewById(R.id.inputLibelle);
                EditText inputDescription = dialog.findViewById(R.id.inputDescription);

                // Récupérez les éléments de la fenêtre contextuelle
                Button confirmer = dialog.findViewById(R.id.confirmer);
                Button annuler = dialog.findViewById(R.id.annuler);

                // Gérez le clic sur le bouton "Confirmer"
                confirmer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Gérez le clic sur le bouton "Confirmer" ici
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

        btnArreter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, CourseSynthese.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
    }

    private void initializeMapLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            Toast.makeText(this, "Test OK", Toast.LENGTH_LONG).show();
            GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            mapController.setCenter(startPoint);

            Marker startMarker = new Marker(mapView);
            startMarker.setPosition(startPoint);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(startMarker);

            mapView.invalidate(); // Rafraîchir la carte
        } else {
            Toast.makeText(this, "Test PAs OK", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeMapLocation();
        }
    }
}
