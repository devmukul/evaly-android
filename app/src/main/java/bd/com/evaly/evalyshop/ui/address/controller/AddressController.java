package bd.com.evaly.evalyshop.ui.address.controller;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemAddressBinding;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.models.user.AddressItem;
import bd.com.evaly.evalyshop.ui.address.model.AddressListModel_;
import bd.com.evaly.evalyshop.ui.address.model.BindAddressModel;
import bd.com.evaly.evalyshop.ui.epoxyModels.EmptySpaceModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoItemModel_;

public class AddressController extends EpoxyController {

    private List<AddressItem> list = new ArrayList<>();
    private boolean isLoading = true;
    private ClickListener clickListener;

    public interface ClickListener {
        void onDelete(AddressItem model);
        void onEdit(AddressItem model);
    }

    @Override
    protected void buildModels() {

        for (AddressItem item : list) {
            new AddressListModel_()
                    .id(item.toString())
                    .model(item)
                    .onBind((model, view, position) -> {
                        ItemAddressBinding binding = (ItemAddressBinding) view.getDataBinding();
                        BindAddressModel.bind(binding, model.model());
                    })
                    .onDeleteClick((model, parentView, clickedView, position) -> clickListener.onDelete(model.model()))
                    .onEditClick((model, parentView, clickedView, position) -> clickListener.onEdit(model.model()))
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
                .width(70)
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

    public void setList(List<AddressItem> list) {
        this.list = list;
    }
}
