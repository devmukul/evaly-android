package bd.com.evaly.evalyshop.ui.payment.bottomsheet.controller;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemPaymentMethodBinding;
import bd.com.evaly.evalyshop.listener.RecyclerViewOnItemClickListener;
import bd.com.evaly.evalyshop.models.payment.PaymentMethodModel;
import bd.com.evaly.evalyshop.ui.payment.bottomsheet.model.PaymentMethodItemModel_;


public class PaymentMethodController extends EpoxyController {

    private List<PaymentMethodModel> list = new ArrayList<>();
    private AppCompatActivity activity;
    private RecyclerViewOnItemClickListener<Void> focusListener;

    public AppCompatActivity getActivity() {
        return activity;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void buildModels() {

        for (PaymentMethodModel model : list) {
            new PaymentMethodItemModel_()
                    .id(model.getName())
                    .title(model.getName())
                    .description(model.getDescription())
                    .image(model.getImage())
                    .isSelected(model.isSelected())
                    .isEnabled(model.isEnabled())
                    .badgeText(model.getBadgeText())
                    .redBadge(model.getRedText())
                    .onBind((model12, view, position) -> {
                        ItemPaymentMethodBinding binding = (ItemPaymentMethodBinding) view.getDataBinding();
                        binding.radioButton.setChecked(model12.isSelected());
                        if (model12.isSelected())
                            binding.container.setBackgroundColor(binding.container.getContext().getResources().getColor(R.color.f9f9f9));
                        else
                            binding.container.setBackgroundColor(binding.container.getContext().getResources().getColor(R.color.fff));
                    })
                    .clickListener((model1, parentView, clickedView, position) -> {
                        String title = model1.title();
                        for (PaymentMethodModel item : list)
                            item.setSelected(item.getName().equals(title));
                        requestModelBuild();
                        focusListener.onRecyclerViewItemClicked(null);
                    })
                    .addTo(this);
        }
    }

    public void loadData(List<PaymentMethodModel> newList, boolean build) {
        this.list = newList;
        if (build)
            requestModelBuild();
    }

    public PaymentMethodModel getSelectedMethod() {
        for (PaymentMethodModel item : list) {
            if (item.isSelected())
                return item;
        }
        return null;
    }

    public void setFocusListener(RecyclerViewOnItemClickListener<Void> focusListener) {
        this.focusListener = focusListener;
    }
}
