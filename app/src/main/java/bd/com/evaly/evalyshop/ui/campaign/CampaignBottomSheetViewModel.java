package bd.com.evaly.evalyshop.ui.campaign;

import androidx.lifecycle.ViewModel;

public class CampaignBottomSheetViewModel extends ViewModel {

    private CampaignBottomSheetNavigator navigator;


    public void setNavigator(CampaignBottomSheetNavigator navigator) {
        this.navigator = navigator;
    }
}
