package bd.com.evaly.evalyshop.ui.home.model;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.HomeModelSliderBinding;
import bd.com.evaly.evalyshop.models.banner.BannerItem;
import bd.com.evaly.evalyshop.ui.epoxy.BaseDataBindingEpoxyModel;
import bd.com.evaly.evalyshop.ui.home.controller.SliderController;

@EpoxyModelClass(layout = R.layout.home_model_slider)
public abstract class HomeSliderModel extends BaseDataBindingEpoxyModel {

    @EpoxyAttribute
    AppCompatActivity activity;

    @EpoxyAttribute
    List<BannerItem> list;

    @EpoxyAttribute
    SliderController controller;

    @Override
    public void preBind(ViewDataBinding baseBinding) {
        super.preBind(baseBinding);

        HomeModelSliderBinding binding = (HomeModelSliderBinding) baseBinding;
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);

        if (controller == null)
            controller = new SliderController();
        controller.setFilterDuplicates(true);
        controller.setActivity(activity);
        binding.sliderPager.setAdapter(controller.getAdapter());

        new TabLayoutMediator(binding.sliderIndicator, binding.sliderPager,
                (tab, position) -> tab.setText("")
        ).attach();

        if (list.size() > 0)
            controller.setData(list);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }

}
