package andrade.mateus.mytracking.viewmodel;

import android.databinding.BaseObservable;
import android.os.Bundle;
import android.support.annotation.CallSuper;

/**
 * Created by mateusandrade on 23/01/2018.
 */

public abstract class BaseViewModel<V> extends BaseObservable {
    private V view;

    @CallSuper public void attachView(V view, Bundle sis) {
        this.view = view;
        if(sis != null) { onRestoreInstanceState(sis); }
    }

    @CallSuper public void detachView() {
        this.view = null;
    }

    protected void onRestoreInstanceState(Bundle sis) { }
    protected void onSaveInstanceState(Bundle outState) { }

    protected final V view() { return view; }
}