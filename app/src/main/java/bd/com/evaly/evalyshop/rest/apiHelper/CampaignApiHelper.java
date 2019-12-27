package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.models.campaign.CampaignShopItem;

public class CampaignApiHelper extends BaseApiHelper {


    public static void getCampaigns(ResponseListenerAuth<CommonDataResponse<List<CampaignItem>>, String> listener) {

        getiApiClient().getCampaigns().enqueue(getResponseCallBackDefault(listener));
    }


    public static void getCampaignShops(String group, int page, ResponseListenerAuth<CommonDataResponse<List<CampaignShopItem>>, String> listener) {

        getiApiClient().getCampaignShops(group, page, 21).enqueue(getResponseCallBackDefault(listener));
    }


}
