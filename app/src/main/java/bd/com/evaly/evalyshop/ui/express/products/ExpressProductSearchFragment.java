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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bd.com.evaly.evalyshop.databinding.ActivityExpressProductSearchBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.rest.apiHelper.ExpressApiHelper;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;

public class ExpressProductSearchFragment extends Fragment {


    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private ActivityExpressProductSearchBinding binding;
    private ExpressProductSearchAdapter adapter;
    private List<ProductItem> itemList;
    private int currentPage = 1;
    private boolean isLoading = false;
    private int totalCount = 0;
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

        expressProductController = new ExpressProductController();

        expressProductController.setActivity((AppCompatActivity) getActivity());
        expressProductController.setFragment(this);

        binding.recyclerView.setAdapter(expressProductController.getAdapter());

        binding.back.setOnClickListener(v -> getActivity().onBackPressed());


        int spanCount = 2;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        expressProductController.setSpanCount(spanCount);

        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        binding.recyclerView.setLayoutManager(layoutManager);

        expressProductController.requestModelBuild();

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    int[] firstVisibleItems = null;
                    firstVisibleItems = layoutManager.findFirstVisibleItemPositions(null);
                    if (firstVisibleItems != null && firstVisibleItems.length > 0)
                        pastVisiblesItems = firstVisibleItems[0];

                    if (!isLoading)
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount)
                            getShopProducts(currentPage);
                }
            }
        });


        binding.progressContainer.setVisibility(View.VISIBLE);
        getShopProducts(1);

        binding.search.setOnEditorActionListener((v, actionId, event) -> {
            if ((actionId == EditorInfo.IME_ACTION_DONE) || (event != null && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) && (event.getAction() == KeyEvent.ACTION_DOWN))) {

                if (!binding.search.getText().toString().trim().equals("")) {

                    ApiClient.getUnsafeOkHttpClient().dispatcher().cancelAll();
                    performSearch(binding.search.getText().toString().trim());
                    query = binding.search.getText().toString().trim();
                } else {

                    // binding.noItem.setVisibility(View.VISIBLE);
                    expressProductController.clear();
                    binding.searchTitle.setVisibility(View.GONE);
                    currentPage = 1;
                    getShopProducts(currentPage);
                }

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.search.getWindowToken(), 0);
                binding.search.clearFocus();
                return true;
            } else {
                return false;
            }

        });

    }

    public String getQuery() {

        return query;

    }


    public void performSearch(String query) {

        binding.searchTitle.setText("Search result for \"" + query + "\"");
        binding.progressContainer.setVisibility(View.VISIBLE);
        expressProductController.clear();
        binding.searchTitle.setVisibility(View.GONE);

        isLoading = true;

        this.query = query;

        currentPage = 1;

        getShopProducts(currentPage);
    }

    public void getShopProducts(int page) {

        isLoading = true;

        if (currentPage > 1)
            expressProductController.setLoadingMore(true);

        ExpressApiHelper.getProductList(null, currentPage, 24, query, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {

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

//                if (response.getCount() == 0) {
//                    binding.noItem.setVisibility(View.VISIBLE);
//                    binding.noText.setText("No products found");
//                } else {
//                    binding.noItem.setVisibility(View.GONE);
//                    binding.noText.setText("Search products here");
//                }

                if (binding.search.getText().toString().length() == 0 && !firstLoad) {
                    firstLoad = false;
                    expressProductController.clear();
                    adapter.notifyDataSetChanged();
                    //   binding.noItem.setVisibility(View.VISIBLE);
                    //  binding.noText.setText("Search products here");
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
