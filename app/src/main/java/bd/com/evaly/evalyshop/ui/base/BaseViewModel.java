package bd.com.evaly.evalyshop.ui.base;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import io.reactivex.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel {

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();
    protected ApiRepository apiRepository;
    protected PreferenceRepository preferenceRepository;
    protected SavedStateHandle savedStateHandle;

    public BaseViewModel(){

    }

    public BaseViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.savedStateHandle = savedStateHandle;
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
