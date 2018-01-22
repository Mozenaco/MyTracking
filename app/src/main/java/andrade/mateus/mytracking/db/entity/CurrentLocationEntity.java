package andrade.mateus.mytracking.db.entity;

import android.location.Location;
import com.orm.SugarRecord;

import java.util.Date;

import andrade.mateus.mytracking.db.parse.DateConverter;
import andrade.mateus.mytracking.model.CurrentLocation;

/**
 * Created by mateusandrade on 20/01/2018.
 */

public class CurrentLocationEntity extends SugarRecord<CurrentLocationEntity> implements CurrentLocation{

    Double latitude;
    Double longitude;
    Long timeRecorded;
    Long journeyId;


    public CurrentLocationEntity(){
    }

    public CurrentLocationEntity(Double latitude, Double longitude, Long timeRecorded, Long journeyId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeRecorded = timeRecorded;
        this.journeyId = journeyId;
    }

    @Override
    public Location getLocation() {
        Location targetLocation = new Location("");
        targetLocation.setLatitude(Double.valueOf(this.latitude));
        targetLocation.setLongitude(Double.valueOf(this.longitude));
        return targetLocation;
    }

    @Override
    public Date getTimeRecorded() {
        return DateConverter.toDate(this.timeRecorded);
    }

    @Override
    public void setLocation(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    @Override
    public void setTimeRecorded(Date date) {
        this.timeRecorded = DateConverter.toTimestamp(date);
    }

    @Override
    public Long getJourneyId() {
        return this.journeyId;
    }

    @Override
    public void setJourneyId(Long journeyId) {
        this.journeyId = journeyId;
    }
}