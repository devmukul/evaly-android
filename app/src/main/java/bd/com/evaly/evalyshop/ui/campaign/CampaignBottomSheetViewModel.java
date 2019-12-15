package bd.com.evaly.evalyshop.ui.campaign;

import androidx.lifecycle.ViewModel;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonSuccessResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;

public class CampaignBottomSheetViewModel extends ViewModel {

    private CampaignBottomSheetNavigator navigator;


    public void setNavigator(CampaignBottomSheetNavigator navigator) {
        this.navigator = navigator;
    }


    public void loadCampaigns(){

        CampaignApiHelper.getCampaigns(new ResponseListener<CommonSuccessResponse<List<CampaignItem>>, String>() {
            @Override
            public void onDataFetched(CommonSuccessResponse<List<CampaignItem>> response, int statusCode) {

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
