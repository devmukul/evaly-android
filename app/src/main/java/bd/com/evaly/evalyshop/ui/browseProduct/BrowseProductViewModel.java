package bd.com.evaly.evalyshop.ui.browseProduct;

import androidx.lifecycle.ViewModel;

public class BrowseProductViewModel extends ViewModel {

    private int tabPosition = -1;


    public int getTabPosition() {
        return tabPosition;
    }

    public void setTabPosition(int tabPosition) {
        this.tabPosition = tabPosition;
    }
}