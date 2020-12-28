package bd.com.evaly.evalyshop.ui.express.products;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.roomdb.express.ExpressServiceDao;
import bd.com.evaly.evalyshop.databinding.ActivityExpressProductSearchBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.rest.apiHelper.ExpressApiHelper;
import bd.com.evaly.evalyshop.ui.express.products.controller.ExpressProductController;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ExpressProductSearchFragment extends Fragment {

    @Inject
    ExpressServiceDao expressServiceDao;
    private ActivityExpressProductSearchBinding binding;
    private List<ProductItem> itemList;
    private int currentPage = 1, totalCount = 0;
    private boolean isLoading = false;
    private String query;
    private boolean firstLoad = true;
    private ExpressProductController expressProductController;

    public ExpressProductSearchFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityExpressProductSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentPage = 1;
        itemList = new ArrayList<>();
        if (expressProductController == null)
            expressProductController = new ExpressProductController();
        expressProductController.setActivity((AppCompatActivity) getActivity());
        expressProductController.setFragment(this);
        expressProductController.setExpressServiceDao(expressServiceDao);
        expressProductController.setFilterDuplicates(true);
        binding.recyclerView.setAdapter(expressProductController.getAdapter());
        binding.back.setOnClickListener(v -> getActivity().onBackPressed());

        int spanCount = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        expressProductController.setSpanCount(spanCount);
        binding.recyclerView.setLayoutManager(layoutManager);

        expressProductController.requestModelBuild();

        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    getShopProducts();
                    isLoading = true;
                }
            }
        });

        binding.progressContainer.setVisibility(View.VISIBLE);
        // getShopProducts();

        binding.search.setOnEditorActionListener((v, actionId, event) -> {
            if ((actionId == EditorInfo.IME_ACTION_DONE) || (event != null && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                if (!binding.search.getText().toString().trim().equals("")) {
                    ApiClient.getUnsafeOkHttpClient().dispatcher().cancelAll();
                    performSearch(binding.search.getText().toString().trim());
                    query = binding.search.getText().toString().trim();
                    binding.searchClear.setImageDrawable(AppController.getmContext().getDrawable(R.drawable.ic_close));
                } else {
                    // binding.noItem.setVisibility(View.VISIBLE);
                    expressProductController.clear();
                    binding.searchTitle.setVisibility(View.GONE);
                    currentPage = 1;
                    getShopProducts();
                }

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.search.getWindowToken(), 0);
                binding.search.clearFocus();
                return true;
            } else {
                return false;
            }

        });

        binding.searchClear.setOnClickListener(v -> {
            if (!binding.search.getText().toString().trim().equals("")) {
                binding.searchClear.setImageDrawable(AppController.getmContext().getDrawable(R.drawable.ic_search));
                binding.search.setText("");
                query = null;
                currentPage = 1;
                expressProductController.clear();
                expressProductController.setTitle("Products");
                binding.progressContainer.setVisibility(View.VISIBLE);
                getShopProducts();
            }
        });

        expressServiceDao.getAll().observe(getViewLifecycleOwner(), expressServiceModels -> {
            expressProductController.setItemsExpress(expressServiceModels);
        });
        getExpressServices();
    }

    private void getExpressServices() {
        ExpressApiHelper.getServicesList(new ResponseListenerAuth<List<ExpressServiceModel>, String>() {
            @Override
            public void onDataFetched(List<ExpressServiceModel> response, int statusCode) {
                Executors.newFixedThreadPool(4).execute(() -> {
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

    public String getQuery() {
        return query;
    }

    public void performSearch(String query) {
        expressProductController.setTitle("Search result for \"" + query + "\"");
        binding.progressContainer.setVisibility(View.VISIBLE);
        expressProductController.clear();
        binding.searchTitle.setVisibility(View.GONE);
        isLoading = true;
        this.query = query;
        currentPage = 1;
        getShopProducts();
    }

    public void getShopProducts() {

        isLoading = true;
        expressProductController.showEmptyPage(false, false);

        if (currentPage > 1)
            expressProductController.setLoadingMore(true);

        ExpressApiHelper.getProductList(null, currentPage, 24, query, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {


                if (!binding.search.getText().toString().equals(""))
                    binding.searchClear.setImageDrawable(AppController.getmContext().getDrawable(R.drawable.ic_close));

                expressProductController.setLoadingMore(false);

                if (binding.search.getText().toString().length() > 0)
                    binding.searchTitle.setVisibility(View.VISIBLE);

                binding.progressContainer.setVisibility(View.GONE);

                isLoading = false;
                totalCount = response.getCount();

                List<ProductItem> list = response.getData();

                long timeInMill = Calendar.getInstance().getTimeInMillis();

                for (ProductItem item : list)
                    item.setUniqueId(item.getSlug() + timeInMill);

                expressProductController.addData(list);

                if (response.getCount() > 0)
                    currentPage++;

                if (response.getCount() == 0)
                    expressProductController.showEmptyPage(true, true);

                if (binding.search.getText().toString().length() == 0 && !firstLoad) {
                    firstLoad = false;
                    expressProductController.clear();
                }
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
