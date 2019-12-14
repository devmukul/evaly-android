package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonSuccessResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.models.campaign.CampaignShopItem;

public class CampaignApiHelper extends ApiHelper{


    public static void getCampaigns(ResponseListener<CommonSuccessResponse<List<CampaignItem>>, String> listener) {

        getiApiClient().getCampaigns().enqueue(getResponseCallBack(listener));
    }


    public static void getCampaignShops(String group, int page, ResponseListener<CommonSuccessResponse<List<CampaignShopItem>>, String> listener) {

        getiApiClient().getCampaignShops(group, page).enqueue(getResponseCallBack(listener));
    }


}
