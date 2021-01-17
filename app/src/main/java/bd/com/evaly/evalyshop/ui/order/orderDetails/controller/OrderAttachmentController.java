package bd.com.evaly.evalyshop.ui.order.orderDetails.controller;

import com.airbnb.epoxy.TypedEpoxyController;

import java.util.List;

import bd.com.evaly.evalyshop.ui.order.orderDetails.model.OrderAttachmentModel_;

public class OrderAttachmentController extends TypedEpoxyController<List<String>> {

    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    protected void buildModels(List<String> data) {

        for (String url : data) {
            new OrderAttachmentModel_()
                    .id("image_picker", url)
                    .url(url)
                    .clickListener((model, parentView, clickedView, position) -> {
                        clickListener.onClick(position);
                    })
                    .addTo(this);
        }
    }

    public interface ClickListener {
        void onClick(int position);
    }
}
