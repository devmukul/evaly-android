package bd.com.evaly.evalyshop.ui.search.controller;

import com.airbnb.epoxy.EpoxyController;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.search.filter.FilterSubItem;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchViewModel;
import bd.com.evaly.evalyshop.ui.search.model.SearchFilterSubModel_;

public class FilterSubController extends EpoxyController {

    private String type = "";
    private GlobalSearchViewModel viewModel;
    private List<FilterSubItem> list = new ArrayList<>();

    public void setType(String type) {
        this.type = type;
    }

    public void setList(List<FilterSubItem> list) {
        this.list = list;
    }

    public void setViewModel(GlobalSearchViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public String getType() {
        return type;
    }

    @Override
    protected void buildModels() {

        for (FilterSubItem item : list) {
            boolean isSelected = false;
            List<String> selectedList = new ArrayList<>();
            if (type.contains("categor"))
                selectedList = viewModel.selectedFilterCategoriesList;
            else if (type.contains("brand"))
                selectedList = viewModel.selectedFilterBrandsList;
            else if (type.contains("shop"))
                selectedList = viewModel.selectedFilterShopsList;

            isSelected = selectedList.contains(item.getName());

            new SearchFilterSubModel_()
                    .id("root_filter", item.getName())
                    .value(item.getName())
                    .count(item.getCount())
                    .selected(isSelected)
                    .checkedListener((checkBox, isChecked, value) -> {
                        item.setSelected(isChecked);
                    })
                    .addTo(this);
        }
    }

    public List<String> getSelectedValues() {
        List<String> items = new ArrayList<>();
        for (FilterSubItem item : list) {
            if (item.isSelected())
                items.add(item.getName());
        }

        return items;
    }

}
