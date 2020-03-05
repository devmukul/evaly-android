package bd.com.evaly.evalyshop.ui.campaign;

import androidx.lifecycle.ViewModel;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;

public class CampaignViewModel extends ViewModel {

    private CampaignNavigator navigator;

    public void setNavigator(CampaignNavigator navigator) {
        this.navigator = navigator;
    }

    public void loadCampaigns(){

        CampaignApiHelper.getCampaigns(new ResponseListenerAuth<CommonDataResponse<List<CampaignItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignItem>> response, int statusCode) {
                navigator.onListLoaded(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                navigator.onListFailed(errorBody, errorCode);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


}
