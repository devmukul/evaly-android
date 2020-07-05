package bd.com.evaly.evalyshop.ui.payment.bottomsheet.controller;

import android.view.View;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyController;
import com.airbnb.epoxy.OnModelBoundListener;
import com.airbnb.epoxy.OnModelClickListener;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.databinding.ItemPaymentMethodBinding;
import bd.com.evaly.evalyshop.models.payment.PaymentMethodModel;
import bd.com.evaly.evalyshop.ui.payment.bottomsheet.model.PaymentMothodItemModel_;

public class PaymentMethodController extends EpoxyController {

    private List<PaymentMethodModel> list = new ArrayList<>();

    @Override
    protected void buildModels() {

        for (PaymentMethodModel model : list) {
            new PaymentMothodItemModel_()
                    .title(model.getName())
                    .description(model.getDescription())
                    .image(model.getImage())
                    .onBind(new OnModelBoundListener<PaymentMothodItemModel_, DataBindingEpoxyModel.DataBindingHolder>() {
                        @Override
                        public void onModelBound(PaymentMothodItemModel_ model, DataBindingEpoxyModel.DataBindingHolder view, int position) {
                            ItemPaymentMethodBinding binding = (ItemPaymentMethodBinding) view.getDataBinding();
                            binding.radioButton.setChecked(model.isSelected());
                        }
                    })
                    .clickListener(new OnModelClickListener<PaymentMothodItemModel_, DataBindingEpoxyModel.DataBindingHolder>() {
                        @Override
                        public void onClick(PaymentMothodItemModel_ model, DataBindingEpoxyModel.DataBindingHolder parentView, View clickedView, int position) {
                            String title = model.title();
                            for (PaymentMethodModel item : list)
                                item.setSelected(item.getName().equalsIgnoreCase(title));
                            requestModelBuild();
                        }
                    })
                    .addTo(this);
        }
    }

    public void loadData(List<PaymentMethodModel> newList, boolean build) {
        this.list = newList;
        if (build)
            requestModelBuild();
    }
}
