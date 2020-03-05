package bd.com.evaly.evalyshop.ui.campaign;

import java.util.List;

import bd.com.evaly.evalyshop.models.campaign.CampaignItem;

public interface CampaignNavigator {
    void onListLoaded(List<CampaignItem> list);
    void onListFailed(String errorBody, int errorCode);
}
