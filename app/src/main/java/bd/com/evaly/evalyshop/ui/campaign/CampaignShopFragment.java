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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentCampaignShopBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignShopItem;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;
import bd.com.evaly.evalyshop.ui.campaign.adapter.CampaignShopAdapter;
import bd.com.evaly.evalyshop.util.ImagePreview;

public class CampaignShopFragment extends Fragment {

    private CampaignShopAdapter adapter;
    private ArrayList<TabsItem> itemList;
    private int page = 1;
    private boolean isLoading = false;
    private String title = "";
    private String slug = "";
    private String image = "";
    private FragmentCampaignShopBinding binding;

    public CampaignShopFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            slug = getArguments().getString("slug");
            image = getArguments().getString("image");

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

        binding.toolbar.setTitle(title);

        Glide.with(view)
                .load(image)
                .into(binding.image);

        binding.headerLogo.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ImagePreview.class);
            intent.putExtra("image", image);
            startActivity(intent);
        });

        itemList = new ArrayList<>();
        adapter = new CampaignShopAdapter(getContext(), itemList,  NavHostFragment.findNavController(this));

        binding.recyclerView.setAdapter(adapter);

        getCampaignShops(1);

    }

    public void getCampaignShops(int p) {

        binding.progressBar.setVisibility(View.VISIBLE);

        CampaignApiHelper.getCampaignShops(slug, p, new ResponseListenerAuth<CommonDataResponse<List<CampaignShopItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignShopItem>> response, int statusCode) {

                binding.progressBar.setVisibility(View.GONE);


                List<CampaignShopItem> list = response.getData();

                for (int i = 0; i < list.size(); i++) {
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setType(6);
                    tabsItem.setTitle(list.get(i).getShopName());
                    tabsItem.setImage(list.get(i).getShopImage());
                    tabsItem.setSlug(list.get(i).getSlug());
                    tabsItem.setCategory("root");
                    tabsItem.setCampaignSlug(slug);
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

                binding.progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

}
