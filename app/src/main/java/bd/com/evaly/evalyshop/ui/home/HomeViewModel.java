package bd.com.evaly.evalyshop.ui.home;

import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private int tabPosition = -1;


    public int getTabPosition() {
        return tabPosition;
    }

    public void setTabPosition(int tabPosition) {
        this.tabPosition = tabPosition;
    }
}
