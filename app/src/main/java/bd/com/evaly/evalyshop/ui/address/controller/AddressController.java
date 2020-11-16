package bd.com.evaly.evalyshop.ui.address.controller;

import android.view.View;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyController;
import com.airbnb.epoxy.OnModelBoundListener;
import com.airbnb.epoxy.OnModelClickListener;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemAddressBinding;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.ui.address.model.AddressListModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.EmptySpaceModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoItemModel_;

public class AddressController extends EpoxyController {

    private List<AddressResponse> list = new ArrayList<>();
    private boolean isLoading = true;
    private ClickListener clickListener;

    public interface ClickListener {
        void onDelete(AddressResponse model);

        void onEdit(AddressResponse model);
    }

    @Override
    protected void buildModels() {

        for (AddressResponse item : list) {
            new AddressListModel_()
                    .id(item.getId())
                    .model(item)
                    .onBind(new OnModelBoundListener<AddressListModel_, DataBindingEpoxyModel.DataBindingHolder>() {
                        @Override
                        public void onModelBound(AddressListModel_ model, DataBindingEpoxyModel.DataBindingHolder view, int position) {
                            ItemAddressBinding binding = (ItemAddressBinding) view.getDataBinding();
                            binding.fullName.setText(model.model().getFullName());
                            binding.addressLine1.setText(model.model().getAddress());
                            binding.addressLine2.setText(model.model().getArea() + ", " + model.model().getCity());
                        }
                    })
                    .onDeleteClick(new OnModelClickListener<AddressListModel_, DataBindingEpoxyModel.DataBindingHolder>() {
                        @Override
                        public void onClick(AddressListModel_ model, DataBindingEpoxyModel.DataBindingHolder parentView, View clickedView, int position) {
                            clickListener.onDelete(model.model());
                        }
                    })
                    .onEditClick(new OnModelClickListener<AddressListModel_, DataBindingEpoxyModel.DataBindingHolder>() {
                        @Override
                        public void onClick(AddressListModel_ model, DataBindingEpoxyModel.DataBindingHolder parentView, View clickedView, int position) {
                            clickListener.onEdit(model.model());
                        }
                    })
                    .addTo(this);
        }

        new EmptySpaceModel_()
                .id("empty_space")
                .height(100)
                .addIf(isLoading, this);

        new LoadingModel_()
                .id("loadingbar")
                .addIf(isLoading, this);

        new NoItemModel_()
                .id("no_item")
                .text("No address added")
                .image(R.drawable.ic_location_track)
                .imageTint("#777777")
                .addIf(!isLoading && list.size() == 0, this);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setList(List<AddressResponse> list) {
        this.list = list;
    }
}
