package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonSuccessResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.models.campaign.CampaignShopItem;

public class CampaignApiHelper extends ApiHelper{


    public static void getCampaigns(ResponseListenerAuth<CommonSuccessResponse<List<CampaignItem>>, String> listener) {

        getiApiClient().getCampaigns().enqueue(getResponseCallBack(listener));
    }


    public static void getCampaignShops(String group, int page, ResponseListenerAuth<CommonSuccessResponse<List<CampaignShopItem>>, String> listener) {

        getiApiClient().getCampaignShops(group, page, 21).enqueue(getResponseCallBack(listener));
    }


}
