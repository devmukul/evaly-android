package bd.com.evaly.evalyshop.ui.order.orderRequest;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.orderRequest.OrderRequestResponse;
import bd.com.evaly.evalyshop.ui.epoxy.NoItemModel_;
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
            new OrderRequestListModel_()
                    .id(item.toString())
                    .model(item)
                    .clickListener((model, parentView, clickedView, position) -> clickListener.onClick(model.model().getInvoiceNo()))
                    .addTo(this);
        }

        new OrderListSkeletonModel_()
                .id("skeletonss")
                .addIf(isLoading, this);

        new NoItemModel_()
                .id("empty item id")
                .width(70)
                .text("No order requests")
                .imageTint("#777777")
                .image(R.drawable.ic_order_thin)
                .addIf(list.size() == 0 && !isLoading, this);
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
