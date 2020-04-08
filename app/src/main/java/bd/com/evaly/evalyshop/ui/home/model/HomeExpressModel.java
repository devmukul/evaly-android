package bd.com.evaly.evalyshop.ui.home.model;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.express.ExpressServiceDao;
import bd.com.evaly.evalyshop.databinding.HomeModelExpressBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.rest.apiHelper.ExpressApiHelper;

@EpoxyModelClass(layout = R.layout.home_model_express)
public abstract class HomeExpressModel extends EpoxyModelWithHolder<HomeExpressModel.HomeExpressHolder> {

    @EpoxyAttribute
    public AppCompatActivity activity;
    @EpoxyAttribute
    public Fragment fragment;
    @EpoxyAttribute
    AppDatabase appDatabase;

    @Override
    public void bind(@NonNull HomeExpressHolder holder) {
        super.bind(holder);
    }

    @Override
    public void unbind(@NonNull HomeExpressHolder holder) {
        super.unbind(holder);
        holder.itemView = null;
    }


    class HomeExpressHolder extends EpoxyHolder {

        View itemView;

        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;

            HomeModelExpressBinding binding = HomeModelExpressBinding.bind(itemView);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(true);

            NavController navController = NavHostFragment.findNavController(fragment);

            ExpressServiceDao expressServiceDao = appDatabase.expressServiceDao();

            expressServiceDao.getAll().observe(fragment.getViewLifecycleOwner(), expressServiceModels -> {
                for (ExpressServiceModel serviceModel : expressServiceModels) {
                    String name = serviceModel.getName().toLowerCase();
                    if (name.contains("grocery")) {
                        binding.btnGrocery.setOnClickListener(v -> {
                            Bundle bundle = new Bundle();
                            bundle.putString("slug", serviceModel.getSlug());
                            navController.navigate(R.id.evalyExpressFragment, bundle);
                        });
                    } else if (name.contains("pharmacy")) {
                        binding.btnPharmacy.setOnClickListener(v -> {
                            Bundle bundle = new Bundle();
                            bundle.putString("slug", serviceModel.getSlug());
                            navController.navigate(R.id.evalyExpressFragment, bundle);
                        });
                    } else if (name.contains("food")) {
                        binding.btnFoods.setOnClickListener(v -> {
                            Bundle bundle = new Bundle();
                            bundle.putString("slug", serviceModel.getSlug());
                            navController.navigate(R.id.evalyExpressFragment, bundle);
                        });
                    }
                }
            });

            ExpressApiHelper.getServicesList(new ResponseListenerAuth<List<ExpressServiceModel>, String>() {
                @Override
                public void onDataFetched(List<ExpressServiceModel> response, int statusCode) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        expressServiceDao.insertList(response);
                        List<String> slugs = new ArrayList<>();
                        for (ExpressServiceModel item : response)
                            slugs.add(item.getSlug());

                        if (slugs.size() > 0)
                            expressServiceDao.deleteOld(slugs);
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
    }

}
