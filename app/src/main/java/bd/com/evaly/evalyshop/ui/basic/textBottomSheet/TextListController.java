package bd.com.evaly.evalyshop.ui.basic.textBottomSheet;

import com.airbnb.epoxy.EpoxyController;

import java.util.Calendar;
import java.util.List;

public class TextListController extends EpoxyController {

    private List<String> list;

    @Override
    protected void buildModels() {
        for (String text: list){
            new TextListModel_()
                    .id(text + Calendar.getInstance().getTimeInMillis())
                    .text(text)
                    .addTo(this);
        }
    }

    public void setList(List<String> list) {
        this.list = list;
        requestModelBuild();
    }
}
