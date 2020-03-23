package bd.com.evaly.evalyshop.ui.home.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.HomeModelSliderBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.banner.BannerItem;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.ui.home.controller.SliderController;

@EpoxyModelClass(layout = R.layout.home_model_slider)
public abstract class HomeSliderModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    AppCompatActivity activity;

    private SliderController controller;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        HomeModelSliderBinding binding = (HomeModelSliderBinding) holder.getDataBinding();
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);

        controller = new SliderController();
        controller.setActivity(activity);

        binding.sliderPager.setAdapter(controller.getAdapter());


        new TabLayoutMediator(binding.sliderIndicator, binding.sliderPager,
                (tab, position) -> tab.setText("")
        ).attach();

        GeneralApiHelper.getBanners(new ResponseListenerAuth<CommonResultResponse<List<BannerItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<BannerItem>> response, int statusCode) {
                controller.addData(response.getData());
                controller.requestModelBuild();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}
