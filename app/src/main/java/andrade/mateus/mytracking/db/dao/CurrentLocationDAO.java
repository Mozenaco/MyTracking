package andrade.mateus.mytracking.db.dao;

import android.content.Context;
import android.location.Location;

import java.util.Date;

import andrade.mateus.mytracking.db.entity.CurrentLocationEntity;
import andrade.mateus.mytracking.model.CurrentLocation;

/**
 * Created by mateusandrade on 20/01/2018.
 */

public class CurrentLocationDAO {

    Context context;

    public CurrentLocationDAO(Context context) {
        this.context = context;
    }

    public void saveLocation(Location location, Date now){

        CurrentLocationEntity currentLocationEntity = new CurrentLocationEntity();
        currentLocationEntity.setLocation(location);
        currentLocationEntity.setTimeRecorded(now);
        currentLocationEntity.save();
    }
}
