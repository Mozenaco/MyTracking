package andrade.mateus.mytracking.db.entity;

import android.location.Location;

import com.orm.SugarRecord;

import java.util.Date;

import andrade.mateus.mytracking.db.parse.DateConverter;
import andrade.mateus.mytracking.model.CurrentLocation;
import andrade.mateus.mytracking.model.Journey;

/**
 * Created by mateusandrade on 20/01/2018.
 */

public class JourneyEntity extends SugarRecord<JourneyEntity> implements Journey{

    private Long journeyId;
    private String journeyName;
    private Long journeyStartTime;
    private Long journeyEndTime;

    public JourneyEntity() {
    }

    public Long getJourneyId() {
        return journeyId;
    }

    @Override
    public void setJourneyId(Long journeyId) {
        this.journeyId = journeyId;
    }

    @Override
    public String getJourneyName() {
        return this.journeyName;
    }

    @Override
    public void setJourneyName(String journeyName) {
        this.journeyName = journeyName;
    }

    @Override
    public Date getStartTime() {
        return DateConverter.toDate(this.journeyStartTime);
    }

    @Override
    public void setStartTime(Date startTime) {
        this.journeyStartTime = DateConverter.toTimestamp(startTime);
    }

    @Override
    public Date getEndTime() {
        return DateConverter.toDate(this.journeyEndTime);
    }

    @Override
    public void setEndTime(Date endTime) {
        this.journeyEndTime = DateConverter.toTimestamp(endTime);
    }
}