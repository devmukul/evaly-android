package bd.com.evaly.evalyshop.ui.campaign;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentCampaignShopBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.models.campaign.CampaignShopItem;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;
import bd.com.evaly.evalyshop.ui.campaign.adapter.CampaignShopAdapter;
import bd.com.evaly.evalyshop.util.ImagePreview;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;

public class CampaignShopFragment extends Fragment {

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private CampaignShopAdapter adapter;
    private ArrayList<TabsItem> itemList;
    private int page = 1;
    private boolean isLoading = false;
    private CampaignItem model;
    private FragmentCampaignShopBinding binding;

    public CampaignShopFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey("model"))
                model = (CampaignItem) getArguments().getSerializable("model");

            if (getArguments().containsKey("slug")) {
                model = new CampaignItem();
                model.setSlug(getArguments().getString("slug"));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCampaignShopBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });

        if (model.getName() != null)
            loadCampaignDetails();

        itemList = new ArrayList<>();
        adapter = new CampaignShopAdapter(getContext(), itemList, NavHostFragment.findNavController(this));

        binding.recyclerView.setAdapter(adapter);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        // binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
        binding.recyclerView.setLayoutManager(layoutManager);

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
                            getCampaignShops(page);
                }
            }
        });

        getCampaignShops(page);
    }

    private void loadCampaignDetails() {

        if (getContext() == null || getActivity() == null || getActivity().isFinishing() || getActivity().isDestroyed() || binding == null)
            return;

        binding.toolbar.setTitle(model.getName());

        Date startDate = Utils.getCampaignDate(model.getStartDate());
        Date endDate = Utils.getCampaignDate(model.getEndDate());
        Date currentDate = Calendar.getInstance().getTime();

        if (currentDate.after(startDate) && currentDate.before(endDate)) {
            binding.tvStatus.setText("Live Now");
            binding.tvStatus.setBackground(getActivity().getResources().getDrawable(R.drawable.btn_live_now_red));
        } else if (currentDate.after(endDate)) {
            binding.tvStatus.setText("Expired");
            binding.tvStatus.setBackground(getActivity().getResources().getDrawable(R.drawable.btn_campaign_expired));
        } else {
            binding.tvStatus.setText(String.format("Live on %s", Utils.getFormatedCampaignDate("", "d MMM hh:mm aa", model.getStartDate())));
            binding.tvStatus.setBackground(getActivity().getResources().getDrawable(R.drawable.btn_pending_bg));
        }

        Glide.with(binding.getRoot())
                .load(model.getBannerImage())
                .into(binding.image);

        binding.headerLogo.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ImagePreview.class);
            intent.putExtra("image", model.getBannerImage());
            startActivity(intent);
        });
    }

    public void getCampaignShops(int p) {

        isLoading = true;
        binding.progressBar.setVisibility(View.VISIBLE);

        CampaignApiHelper.getCampaignShops(model.getSlug(), p, new ResponseListenerAuth<CommonDataResponse<List<CampaignShopItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignShopItem>> response, int statusCode) {

                isLoading = false;
                binding.progressBar.setVisibility(View.GONE);

                JsonObject meta = response.getMeta();

                model.setName(meta.get("campaign_title").getAsString());
                model.setBannerImage(meta.get("campaign_banner").getAsString());
                model.setStartDate(meta.get("campaign_start_date").getAsString());
                model.setEndDate(meta.get("campaign_end_date").getAsString());

                loadCampaignDetails();

                List<CampaignShopItem> list = response.getData();

                for (int i = 0; i < list.size(); i++) {
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setType(6);
                    tabsItem.setTitle(list.get(i).getShopName());
                    tabsItem.setImage(list.get(i).getShopImage());
                    tabsItem.setSlug(list.get(i).getSlug());
                    tabsItem.setCategory("root");
                    tabsItem.setCampaignSlug(model.getSlug());
                    itemList.add(tabsItem);
                    adapter.notifyItemInserted(itemList.size());
                }

                if (list.size() > 0)
                    page++;

                if (page == 1 && list.size() == 0)
                    binding.layoutNot.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                isLoading = false;
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

}
