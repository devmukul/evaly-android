package bd.com.evaly.evalyshop.ui.appointment.controller;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;
import bd.com.evaly.evalyshop.ui.appointment.model.AppointmentModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.EmptySpaceModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoItemModel_;

public class AppointmentController extends EpoxyController {

    private List<AppointmentResponse> list = new ArrayList<>();
    private boolean isLoading = true;
    private ClickListener clickListener;

    public interface ClickListener {
        void onCancelClick(AppointmentResponse model);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    protected void buildModels() {
        for (AppointmentResponse item : list) {
            new AppointmentModel_()
                    .id(item.getAppointmentId())
                    .model(item)
                    .clickListenerCancel((model, parentView, clickedView, position) -> clickListener.onCancelClick(model.model()))
                    .addTo(this);
        }

        new NoItemModel_()
                .id("emptymodel")
                .image(R.drawable.ic_appointment_scheduling)
                .text("You have no appointments")
                .imageTint("#999999")
                .width(80)
                .addIf(!isLoading && list.size() == 0, this);

        new EmptySpaceModel_()
                .id("emptyspace")
                .height(60)
                .addIf(isLoading && list.size() == 0, this);

        new LoadingModel_()
                .id("loadingbar")
                .addIf(isLoading, this);
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setList(List<AppointmentResponse> list) {
        this.list = list;
    }
}
