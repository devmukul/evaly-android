package bd.com.evaly.evalyshop.ui.order.orderList.controller;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.order.OrderListItem;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.order.orderList.model.OrderListModel_;

public class OrderListController extends EpoxyController {

    private List<OrderListItem> list = new ArrayList<>();
    private boolean isLoading = true;

    @Override
    protected void buildModels() {
        for (OrderListItem item : list) {
            new OrderListModel_()
                    .id(item.getInvoiceNo())
                    .model(item)
                    .addTo(this);
        }

        new LoadingModel_()
                .id("loadingbar")
                .addIf(isLoading, this);
    }

    public void setList(List<OrderListItem> list) {
        this.list = list;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
