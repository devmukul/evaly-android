package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonSuccessResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;

public class CampaignApiHelper extends ApiHelper{


    public static void getCampaigns(ResponseListener<CommonSuccessResponse<List<CampaignItem>>, String> listener) {

        getiApiClient().getCampaigns().enqueue(getResponseCallBack(listener));
    }



}
