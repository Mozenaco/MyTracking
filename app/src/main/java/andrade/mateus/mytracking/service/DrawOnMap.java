package andrade.mateus.mytracking.service;

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by mateusandrade on 21/01/2018.
 */

public class DrawOnMap {

    GoogleMap map;

    public DrawOnMap(GoogleMap map) {
        this.map = map;
    }

    public void drawLine(LatLng start, LatLng end) {
        Polyline line = map.addPolyline(new PolylineOptions()
                .add(start, end)
                .width(5)
                .color(Color.BLUE));
    }
}
