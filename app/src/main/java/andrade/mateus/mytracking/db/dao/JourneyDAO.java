package andrade.mateus.mytracking.db.dao;

import android.content.Context;
import android.location.Location;

import java.util.Date;

import andrade.mateus.mytracking.db.entity.CurrentLocationEntity;
import andrade.mateus.mytracking.db.entity.JourneyEntity;

/**
 * Created by mateusandrade on 20/01/2018.
 */

public class JourneyDAO {

    Context context;

    public JourneyDAO(Context context) {
        this.context = context;
    }

    public void saveJourney(Long journeyId, String journeyName, Date journeyStartTime, Date journeyEndTime){

        JourneyEntity journeyEntity = new JourneyEntity();
        journeyEntity.setJourneyId(journeyId);
        journeyEntity.setJourneyName(journeyName);
        journeyEntity.setStartTime(journeyStartTime);
        journeyEntity.setEndTime(journeyEndTime);
        journeyEntity.save();
    }
}
