package bd.com.evaly.evalyshop.ui.search.controller;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.search.filter.FilterRootItem;
import bd.com.evaly.evalyshop.ui.search.model.SearchFilterRootModel_;

public class FilterRootController extends EpoxyController {

    private List<FilterRootItem> list = new ArrayList<>();

    public void setList(List<FilterRootItem> list) {
        this.list = list;
    }

    @Override
    protected void buildModels() {
        for (FilterRootItem item : list) {
            new SearchFilterRootModel_()
                    .id("root_filter", item.getName())
                    .name(item.getName())
                    .selected(item.isSelected())
                    .clickListener((model, parentView, clickedView, position) -> {

                    })
                    .addTo(this);
        }
    }
}
