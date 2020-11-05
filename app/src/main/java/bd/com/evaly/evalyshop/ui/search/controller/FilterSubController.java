package bd.com.evaly.evalyshop.ui.search.controller;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.search.filter.FilterSubItem;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchViewModel;
import bd.com.evaly.evalyshop.ui.search.model.SearchFilterSubModel_;

public class FilterSubController extends EpoxyController {

    private GlobalSearchViewModel viewModel;
    private List<FilterSubItem> list = new ArrayList<>();

    public void setList(List<FilterSubItem> list) {
        this.list = list;
    }

    public void setViewModel(GlobalSearchViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    protected void buildModels() {

        for (FilterSubItem item : list) {
            new SearchFilterSubModel_()
                    .id("root_filter", item.getName())
                    .value(item.getName())
                    .count(item.getCount())
                    .selected(item.isSelected())
                    .clickListener((model, parentView, clickedView, position) -> {

                    })
                    .addIf(viewModel.getSelectedFilterRoot().equals(item.getRoot()), this);
        }
    }

}
