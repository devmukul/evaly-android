package bd.com.evaly.evalyshop.ui.address.controller;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.ui.address.model.AddressListModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.EmptySpaceModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoItemModel_;

public class AddressController extends EpoxyController {

    private List<AddressResponse> list = new ArrayList<>();
    private boolean isLoading = true;

    @Override
    protected void buildModels() {

        for (AddressResponse item : list) {
            new AddressListModel_()
                    .id(item.getId())
                    .model(item)
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

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setList(List<AddressResponse> list) {
        this.list = list;
    }
}
