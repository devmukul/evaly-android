package bd.com.evaly.evalyshop.ui.order.orderList.controller;

import com.airbnb.epoxy.EpoxyController;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.orderRequest.OrderRequestResponse;
import bd.com.evaly.evalyshop.ui.order.orderList.model.OrderListSkeletonModel_;
import bd.com.evaly.evalyshop.ui.order.orderList.model.OrderRequestListModel_;

public class OrderRequestListController extends EpoxyController {

    private List<OrderRequestResponse> list = new ArrayList<>();
    private boolean isLoading = true;
    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    protected void buildModels() {
        for (OrderRequestResponse item : list) {
            Logger.d(item);
            new OrderRequestListModel_()
                    .id(item.toString())
                    .model(item)
                    // .clickListener((model, parentView, clickedView, position) -> clickListener.onClick(model.model().getInvoiceNo()))
                    .addTo(this);
        }

        new OrderListSkeletonModel_()
                .id("skeletonss")
                .addIf(isLoading, this);
    }

    public void setList(List<OrderRequestResponse> list) {
        this.list = list;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public interface ClickListener {
        void onClick(String invoice);
    }
}
