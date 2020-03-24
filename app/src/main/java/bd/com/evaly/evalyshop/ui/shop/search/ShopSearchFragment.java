package bd.com.evaly.evalyshop.ui.shop.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import bd.com.evaly.evalyshop.databinding.FragmentShopSearchBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ItemsItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.rest.apiHelper.ShopApiHelper;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;


public class ShopSearchFragment extends Fragment {

    private FragmentShopSearchBinding binding;
    private ShopSearchAdapter adapter;
    private List<ItemsItem> itemList;
    private int currentPage = 1;
    private int cashbackRate;
    private String campaignSlug="", shopSlug = "sumash-tech";
    private boolean isLoading = false;
    private int totalCount = 0;


    public ShopSearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShopSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemList = new ArrayList<>();
        adapter = new ShopSearchAdapter(getContext(), itemList, (AppCompatActivity) getActivity(), this,  NavHostFragment.findNavController(this), null, null);
        binding.recyclerView.setAdapter(adapter);

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
        int spacing = (int) Utils.convertDpToPixel(10, Objects.requireNonNull(getContext())); // 50px
        boolean includeEdge = true;
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        getShopProducts(1);

    }

    public void getShopProducts(int page) {

        isLoading = true;
        binding.progressContainer.setVisibility(View.VISIBLE);


        ShopApiHelper.getShopDetailsItem(CredentialManager.getToken(), shopSlug, page, 21, null, campaignSlug, null, new ResponseListenerAuth<ShopDetailsModel, String>() {
            @Override
            public void onDataFetched(ShopDetailsModel response, int statusCode) {

                isLoading = false;
                totalCount = response.getCount();

                itemList.addAll(response.getData().getItems());
                adapter.notifyItemRangeInserted(itemList.size() - response.getData().getItems().size(), response.getData().getItems().size());

                if (response.getCount() > 0)
                    currentPage++;
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
