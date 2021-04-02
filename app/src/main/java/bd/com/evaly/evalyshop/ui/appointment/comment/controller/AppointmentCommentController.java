package bd.com.evaly.evalyshop.ui.appointment.comment.controller;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.appointment.comment.AppointmentCommentResponse;
import bd.com.evaly.evalyshop.ui.appointment.comment.model.AppointmentCommentModel_;
import bd.com.evaly.evalyshop.ui.epoxy.EmptySpaceModel_;
import bd.com.evaly.evalyshop.ui.epoxy.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxy.NoItemModel_;

public class AppointmentCommentController extends EpoxyController {

    private List<AppointmentCommentResponse> list = new ArrayList<>();
    private boolean isLoading = true;

    @Override
    protected void buildModels() {
        for (AppointmentCommentResponse item : list) {
            new AppointmentCommentModel_()
                    .id(item.getId())
                    .model(item)
                    .addTo(this);
        }

        new NoItemModel_()
                .id("noitem")
                .text("No comments yet")
                .imageTint("#888888")
                .image(R.drawable.ic_comment_icon_new)
                .width(50)
                .addIf(!isLoading && list.size() == 0, this);

        new EmptySpaceModel_()
                .id("emptymodel")
                .height(100)
                .addIf(isLoading && list.size() == 0, this);

        new LoadingModel_()
                .id("loadingbar")
                .addIf(isLoading, this);
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setList(List<AppointmentCommentResponse> list) {
        this.list = list;
    }
}
