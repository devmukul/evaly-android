package bd.com.evaly.evalyshop.ui.home.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.HomeModelSliderBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.banner.BannerItem;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.ui.home.adapter.SliderAdapter;

@EpoxyModelClass(layout = R.layout.home_model_slider)
public abstract class HomeSliderModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    AppCompatActivity activity;

    private SliderAdapter adapter;
    private List<BannerItem> itemList;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        HomeModelSliderBinding binding = (HomeModelSliderBinding) holder.getDataBinding();
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);

        itemList = new ArrayList<>();
        adapter = new SliderAdapter(activity, itemList);

        binding.sliderPager.setAdapter(adapter);
        binding.sliderIndicator.setupWithViewPager(binding.sliderPager, true);

        GeneralApiHelper.getBanners(new ResponseListenerAuth<CommonResultResponse<List<BannerItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<BannerItem>> response, int statusCode) {
                itemList.addAll(response.getData());
                adapter.notifyDataSetChanged();
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
