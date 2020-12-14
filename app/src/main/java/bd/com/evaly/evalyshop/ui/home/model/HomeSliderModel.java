package bd.com.evaly.evalyshop.ui.home.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.banner.BannerDao;
import bd.com.evaly.evalyshop.databinding.HomeModelSliderBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.banner.BannerItem;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.ui.home.controller.SliderController;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@EpoxyModelClass(layout = R.layout.home_model_slider)
public abstract class HomeSliderModel extends EpoxyModelWithHolder<HomeSliderModel.HomeSliderHolder> {

    @EpoxyAttribute
    AppCompatActivity activity;
    @EpoxyAttribute
    AppDatabase appDatabase;
    @EpoxyAttribute
    Fragment fragment;

    BannerDao bannerDao;
    SliderController controller;
    CompositeDisposable compositeDisposable;


    @Override
    public void bind(@NonNull HomeSliderHolder holder) {
        super.bind(holder);
    }

    @Override
    public void unbind(@NonNull HomeSliderHolder holder) {
        super.unbind(holder);
        if (compositeDisposable != null)
            compositeDisposable.clear();
    }

    class HomeSliderHolder extends EpoxyHolder {

        View itemView;

        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams();
            params.setFullSpan(true);

            HomeModelSliderBinding binding = HomeModelSliderBinding.bind(itemView);

            compositeDisposable = new CompositeDisposable();

            if (controller == null)
                controller = new SliderController();
            controller.setFilterDuplicates(true);
            controller.setActivity(activity);
            binding.sliderPager.setAdapter(controller.getAdapter());

            new TabLayoutMediator(binding.sliderIndicator, binding.sliderPager,
                    (tab, position) -> tab.setText("")
            ).attach();

            bannerDao = appDatabase.bannerDao();

            bannerDao.getAll().observe(fragment.getViewLifecycleOwner(), bannerItems -> {
                controller.reAddData(bannerItems);
            });

            GeneralApiHelper.getBanners(new ResponseListenerAuth<CommonResultResponse<List<BannerItem>>, String>() {
                @Override
                public void onDataFetched(CommonResultResponse<List<BannerItem>> response, int statusCode) {

                    bannerDao.insertListRx(response.getData())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new CompletableObserver() {
                                @Override
                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                                    compositeDisposable.add(d);
                                }

                                @Override
                                public void onComplete() {
                                    List<String> slugs = new ArrayList<>();
                                    for (BannerItem item : response.getData())
                                        slugs.add(item.slug);

                                    if (slugs.size() > 0)
                                        deleteOldBanners(slugs);
                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                                }
                            });
                }

                @Override
                public void onFailed(String errorBody, int errorCode) {

                }

                @Override
                public void onAuthError(boolean logout) {

                }
            });

        }

        private void deleteOldBanners(List<String> slugs) {
            bannerDao.deleteOldRx(slugs)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                        }
                    });
        }
    }


}
