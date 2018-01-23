package andrade.mateus.mytracking.model;

import android.location.Location;

import java.util.Date;


/**
 * Created by mateusandrade on 20/01/2018.
 */

public interface Journey {

    Long getJourneyId();
    void setJourneyId(Long journeyId);

    String getJourneyName();
    void setJourneyName(String journeyName);

    Date getStartTime();
    void setStartTime(Date startTime);

    Date getEndTime();
    void setEndTime(Date endTime);
}