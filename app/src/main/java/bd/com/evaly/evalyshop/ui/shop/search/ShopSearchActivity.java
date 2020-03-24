package bd.com.evaly.evalyshop.ui.shop.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentShopSearchBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ItemsItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.rest.apiHelper.ShopApiHelper;
import bd.com.evaly.evalyshop.ui.buynow.BuyNowFragment;
import bd.com.evaly.evalyshop.ui.shop.ShopViewModel;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;


public class ShopSearchActivity extends AppCompatActivity {

    private FragmentShopSearchBinding binding;
    private ShopSearchAdapter adapter;
    private List<ItemsItem> itemList;
    private int currentPage = 1;
    private int cashbackRate;
    private String campaignSlug = "", shopSlug = "sumash-tech", shopTitle;
    private boolean isLoading = false;
    private int totalCount = 0;
    private String query;
    private ShopViewModel viewModel;
    private boolean firstLoad = true;

    public ShopSearchActivity() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ShopViewModel.class);

        Intent bundle = getIntent();
        shopSlug = bundle.getStringExtra("shop_slug");
        campaignSlug = bundle.getStringExtra("campaign_slug");

        binding = DataBindingUtil.setContentView(this, R.layout.fragment_shop_search);

        itemList = new ArrayList<>();
        adapter = new ShopSearchAdapter(this, itemList, this, null, viewModel);
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

        binding.search.requestFocus();

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

        viewModel.getBuyNowLiveData().observe(this, s -> {

            BuyNowFragment addPhotoBottomDialogFragment =
                    BuyNowFragment.newInstance(shopSlug, s);
            addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                    "BuyNow");
        });


    }

    // https://api-prod.evaly.com.bd/api/categories/?search=phone&page=1


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


        ShopApiHelper.getShopDetailsItem(CredentialManager.getToken(), shopSlug, page, 21, null, campaignSlug, query, new ResponseListenerAuth<ShopDetailsModel, String>() {
            @Override
            public void onDataFetched(ShopDetailsModel response, int statusCode) {


                if (binding.search.getText().toString().length() > 0)
                    binding.searchTitle.setVisibility(View.VISIBLE);

                binding.progressContainer.setVisibility(View.GONE);
                binding.bottomProgressBar.setVisibility(View.GONE);

                isLoading = false;
                totalCount = response.getCount();
                itemList.addAll(response.getData().getItems());
                adapter.notifyItemRangeInserted(itemList.size() - response.getData().getItems().size(), response.getData().getItems().size());

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
                if (!logout)
                    getShopProducts(page);

            }
        });

    }
}
