package andrade.mateus.mytracking.model;

import android.location.Location;

import java.util.Date;


/**
 * Created by mateusandrade on 20/01/2018.
 */

public interface CurrentLocation {
    Location getLocation();
    void setLocation(Location location);
    Date getTimeRecorded();
    void setTimeRecorded(Date timeRecorded);
    Long getJourneyId();
    void setJourneyId(Long journeyId);
}