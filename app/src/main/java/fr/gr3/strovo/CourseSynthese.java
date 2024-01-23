package fr.gr3.strovo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class CourseSynthese extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_synthese);

        // Initialize the MapView
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // Set the default zoom and location
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(new GeoPoint(48.8583, 2.2944));  // Example: Eiffel Tower

        // Other code here...
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();  // Needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();  // Needed for compass, my location overlays, v6.0.0 and up
    }
}
