package bd.com.evaly.evalyshop.epoxy;

import bd.com.evaly.evalyshop.epoxy.models.GridItemModel;
import bd.com.evaly.evalyshop.epoxy.models.GridItemModel_;
import bd.com.evaly.evalyshop.epoxy.models.HeaderModel;
import bd.com.evaly.evalyshop.epoxy.models.HeaderModel_;

public class EpoxyAdapter extends com.airbnb.epoxy.EpoxyAdapter {

    private final HeaderModel headerModel = new HeaderModel_().title("Header 1");

    @Override
    protected void enableDiffing() {
        super.enableDiffing();
    }

    public void setData() {
        models.add(headerModel);

        String item = "This is title";

        for (int i = 0; i < 10; i++) {
            item = item + item;
            GridItemModel gridItemModel = new GridItemModel_()
                    .id(i)
                    .text(item)
                    .spanSizeOverride((totalSpanCount, position, itemCount) -> totalSpanCount / 2);
            models.add(gridItemModel);
        }

        notifyDataSetChanged();

    }



}
