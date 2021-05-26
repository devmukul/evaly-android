package bd.com.evaly.evalyshop.ui.evalyPoint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.points.PointsResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EvalyPointsViewModel extends BaseViewModel {

    public MutableLiveData<PointsResponse> pointsLiveData = new MutableLiveData<>();

    @Inject
    public EvalyPointsViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        super(savedStateHandle, apiRepository, preferenceRepository);
        pointsLiveData.setValue(preferenceRepository.getPoints());
        getEvalyPoints();
    }

    public void getEvalyPoints() {
        apiRepository.getPoints(new ResponseListener<CommonDataResponse<PointsResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<PointsResponse> response, int statusCode) {
                preferenceRepository.setPoints(response.getData());
                pointsLiveData.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }
        });
    }

}
