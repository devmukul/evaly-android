package bd.com.evaly.evalyshop.ui.base;

import com.airbnb.epoxy.EpoxyController;

public abstract class BaseEpoxyController extends EpoxyController {

    public BaseEpoxyController() {
        setFilterDuplicates(true);
    }

}
