package bd.com.evaly.evalyshop.ui.express.products;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ActivityExpressProductSearchBinding;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.express.products.controller.ExpressProductController;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ExpressServiceFragment extends BaseFragment<ActivityExpressProductSearchBinding, ExpressServiceViewModel> {

    @Inject
    ApiRepository apiRepository;
    private ExpressProductController expressProductController;

    public ExpressServiceFragment() {
        super(ExpressServiceViewModel.class, R.layout.activity_express_product_search);
    }

    @Override
    protected void initViews() {
        binding.progressContainer.setVisibility(View.VISIBLE);
    }


    @Override
    protected void setupRecycler() {
        if (expressProductController == null)
            expressProductController = new ExpressProductController();
        expressProductController.setActivity((AppCompatActivity) getActivity());
        expressProductController.setFragment(this);
        expressProductController.setExpressServiceDao(viewModel.expressServiceDao);
        expressProductController.setFilterDuplicates(true);
        binding.recyclerView.setAdapter(expressProductController.getAdapter());
        int spanCount = 3;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);
        expressProductController.setSpanCount(spanCount);
        binding.recyclerView.setLayoutManager(layoutManager);
        expressProductController.requestModelBuild();
    }

    @Override
    protected void clickListeners() {
        binding.back.setOnClickListener(v -> getActivity().onBackPressed());
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.expressServiceDao.getAll().observe(getViewLifecycleOwner(), expressServiceModels -> {
            expressProductController.setItemsExpress(expressServiceModels);
        });
    }

}
