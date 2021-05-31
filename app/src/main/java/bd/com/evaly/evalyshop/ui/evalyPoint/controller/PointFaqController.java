package bd.com.evaly.evalyshop.ui.evalyPoint.controller;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemPointFaqBinding;
import bd.com.evaly.evalyshop.models.points.FaqItem;
import bd.com.evaly.evalyshop.ui.base.BaseEpoxyController;
import bd.com.evaly.evalyshop.ui.evalyPoint.model.PointFaqModel_;

public class PointFaqController extends BaseEpoxyController {

    private List<FaqItem> list = new ArrayList<>();

    @Override
    protected void buildModels() {
        for (FaqItem item : list) {
            new PointFaqModel_()
                    .id(item.getTitle())
                    .model(item)
                    .isExpanded(item.isExpanded())
                    .onBind((model, view, position) -> {
                        ItemPointFaqBinding binding = (ItemPointFaqBinding) view.getDataBinding();
                        if (model.isExpanded()) {
                            binding.arrow.setImageResource(R.drawable.ic_arrow_up);
                            binding.description.setVisibility(View.VISIBLE);
                        } else {
                            binding.arrow.setImageResource(R.drawable.ic_arrow_down);
                            binding.description.setVisibility(View.GONE);
                        }
                    })
                    .clickListener((model, parentView, clickedView, position) -> {
                        item.setExpanded(!model.model().isExpanded());
                        requestModelBuild();
                    })
                    .addTo(this);
        }
    }

    public void setList(List<FaqItem> list) {
        this.list = list;
    }
}
