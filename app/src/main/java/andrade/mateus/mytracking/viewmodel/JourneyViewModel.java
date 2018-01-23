package andrade.mateus.mytracking.viewmodel;

import android.databinding.BaseObservable;
import android.view.View;
import andrade.mateus.mytracking.db.entity.JourneyEntity;

/**
 * Created by mateusandrade on 23/01/2018.
 */

public class JourneyViewModel extends BaseObservable {

    private JourneyEntity model = new JourneyEntity();

    public void setModel(JourneyEntity model) {
        this.model = model;
        notifyChange();
    }
}

