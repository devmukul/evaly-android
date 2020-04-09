package bd.com.evaly.evalyshop.ui.express.products;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ActivityExpressProductSearchBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ExpressApiHelper;
import bd.com.evaly.evalyshop.ui.shop.ShopViewModel;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;

public class ExpressProductSearchActivity extends AppCompatActivity {


    private ActivityExpressProductSearchBinding binding;
    private ExpressProductSearchAdapter adapter;
    private List<ProductItem> itemList;
    private int currentPage = 1;
    private boolean isLoading = false;
    private int totalCount = 0;
    private String query;
    private ShopViewModel viewModel;
    private boolean firstLoad = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_express_product_search);

        itemList = new ArrayList<>();
        adapter = new ExpressProductSearchAdapter(this, itemList, this, null, viewModel);
        binding.recyclerView.setAdapter(adapter);

        binding.back.setOnClickListener(v -> finish());

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {

                    if (!isLoading && totalCount > itemList.size()) {
                        if (currentPage > 1)
                            binding.progressContainer.setVisibility(View.VISIBLE);
                        getShopProducts(currentPage);
                    }
                }
            }
        });

        int spanCount = 2; // 3 columns
        int spacing = (int) Utils.convertDpToPixel(10, Objects.requireNonNull(this));
        boolean includeEdge = true;
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        getShopProducts(1);

        binding.search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {


                if (!binding.search.getText().toString().trim().equals("")) {

                    performSearch(binding.search.getText().toString().trim());
                    query = binding.search.getText().toString().trim();
                } else {

                    binding.noItem.setVisibility(View.VISIBLE);
                    itemList.clear();
                    adapter.notifyDataSetChanged();
                    binding.searchTitle.setVisibility(View.GONE);
                    binding.noText.setText("Search products here");
                }

            }
        });

    }

    public String getQuery() {

        return query;

    }


    public void performSearch(String query) {

        binding.searchTitle.setText("Search result for \"" + query + "\"");
        binding.progressContainer.setVisibility(View.VISIBLE);
        binding.noItem.setVisibility(View.GONE);

        itemList.clear();
        adapter.notifyDataSetChanged();
        binding.searchTitle.setVisibility(View.GONE);

        binding.noItem.setVisibility(View.GONE);

        isLoading = true;

        this.query = query;

        currentPage = 1;

        getShopProducts(currentPage);
    }

    public void getShopProducts(int page) {


        binding.noItem.setVisibility(View.GONE);

        isLoading = true;
        binding.progressContainer.setVisibility(View.VISIBLE);

        if (currentPage > 1)
            binding.bottomProgressBar.setVisibility(View.VISIBLE);


        ExpressApiHelper.getProductList(null, currentPage, 24, query, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {

                if (binding.search.getText().toString().length() > 0)
                    binding.searchTitle.setVisibility(View.VISIBLE);

                binding.progressContainer.setVisibility(View.GONE);
                binding.bottomProgressBar.setVisibility(View.GONE);

                isLoading = false;
                totalCount = response.getCount();
                itemList.addAll(response.getData());
                adapter.notifyItemRangeInserted(itemList.size() - response.getData().size(), response.getData().size());

                if (response.getCount() > 0)
                    currentPage++;

                if (response.getCount() == 0) {
                    binding.noItem.setVisibility(View.VISIBLE);
                    binding.noText.setText("No products found");
                } else {
                    binding.noItem.setVisibility(View.GONE);
                    binding.noText.setText("Search products here");
                }

                if (binding.search.getText().toString().length() == 0 && !firstLoad) {
                    firstLoad = false;
                    itemList.clear();
                    adapter.notifyDataSetChanged();
                    binding.noItem.setVisibility(View.VISIBLE);
                    binding.noText.setText("Search products here");
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
