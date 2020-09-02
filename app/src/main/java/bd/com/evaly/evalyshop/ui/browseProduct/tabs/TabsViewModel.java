package bd.com.evaly.evalyshop.ui.browseProduct.tabs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TabsViewModel extends ViewModel {


    private int tabPosition = -1;

    private MutableLiveData<Integer> itemCount = new MutableLiveData<>();

    public LiveData<Integer> getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount.setValue(itemCount);
    }




    public int getIntCount() {
        if (itemCount.getValue() == null)
            return 0;
        else
            return itemCount.getValue();
    }

    public int getTabPosition() {
        return tabPosition;
    }

    public void setTabPosition(int tabPosition) {
        this.tabPosition = tabPosition;
    }
}
