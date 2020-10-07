package bd.com.evaly.evalyshop.ui.product.productDetails.controller;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.product.productDetails.AttributesItem;

public class VariantsController extends EpoxyController {

    private List<AttributesItem> list = new ArrayList<>();

    public VariantsController() {
        setFilterDuplicates(true);
    }

    @Override
    protected void buildModels() {



    }

    public void setList(List<AttributesItem> list) {
        this.list = list;
    }


}
