package bd.com.evaly.evalyshop.ui.order.orderList.controller;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.order.OrderListItem;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoItemModel_;
import bd.com.evaly.evalyshop.ui.order.orderList.model.OrderListModel_;
import bd.com.evaly.evalyshop.ui.order.orderList.model.OrderListSkeletonModel_;

public class OrderListController extends EpoxyController {

    private List<OrderListItem> list = new ArrayList<>();
    private boolean isLoading = true;
    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    protected void buildModels() {

        for (OrderListItem item : list) {
            new OrderListModel_()
                    .id(item.getInvoiceNo())
                    .model(item)
                    .clickListener((model, parentView, clickedView, position) -> clickListener.onClick(model.model().getInvoiceNo()))
                    .addTo(this);
        }

        new OrderListSkeletonModel_()
                .id("skeletonss")
                .addIf(isLoading, this);

        new NoItemModel_()
                .id("noordersfound")
                .image(R.drawable.ic_order_thin)
                .width(80)
                .imageTint("#888888")
                .text("No orders found")
                .addIf(!isLoading && list.size() == 0, this);

    }

    public void setList(List<OrderListItem> list) {
        this.list = list;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public interface ClickListener {
        void onClick(String invoice);
    }
}
