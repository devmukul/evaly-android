package bd.com.evaly.evalyshop.ui.giftcard.giftCardList;


import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.FragmentGiftcardListBinding;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.models.remoteConfig.RemoteConfigBaseUrls;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.giftcard.adapter.GiftCardListAdapter;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

();


@AndroidEntryPoint
public class GiftCardListFragment extends BaseFragment<FragmentGiftcardListBinding, GiftCardListViewModel> implements SwipeRefreshLayout.OnRefreshListener {

    static GiftCardListFragment instance;
    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    ApiRepository apiRepository;
    @Inject
    FirebaseRemoteConfig firebaseRemoteConfig;
    private ArrayList<GiftCardListItem> itemList;
    private GiftCardListAdapter adapter;
    private ViewDialog dialog;
    private int currentPage;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String baseUrl = BuildConfig.BASE_URL + "cpn/";

    public GiftCardListFragment() {
        super(GiftCardListViewModel.class, R.layout.fragment_giftcard_list);
    }

    public static GiftCardListFragment getInstance() {
        return instance;
    }

    @Override
    public void onRefresh() {
        itemList.clear();
        adapter.notifyDataSetChanged();
        currentPage = 1;
        binding.swipeContainer.setRefreshing(false);
        getGiftCardList();
    }

    @Override
    protected void initViews() {
        RemoteConfigBaseUrls baseUrls = new Gson().fromJson(firebaseRemoteConfig.getValue("temp_urls").asString(), RemoteConfigBaseUrls.class);

        String url = null;
        if (baseUrls != null) {
            if (BuildConfig.DEBUG)
                url = baseUrls.getDevGiftCardBaseUrl();
            else
                url = baseUrls.getProdGiftCardBaseUrl();
        }

        if (url != null)
            baseUrl = url;


        binding.swipeContainer.setOnRefreshListener(this);

        itemList = new ArrayList<>();
        dialog = new ViewDialog(getActivity());
        currentPage = 1;
        getGiftCardList();
    }

    @Override
    protected void liveEventsObservers() {

    }

    @Override
    protected void clickListeners() {

    }

    @Override
    protected void setupRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(manager);

        instance = this;
        adapter = new GiftCardListAdapter(getContext(), itemList);
        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();
                    pastVisiblesItems = manager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            getGiftCardList();
                        }
                    }
                }
            }
        });
    }



    public void getGiftCardList() {

        loading = false;

        if (currentPage == 1) {
            binding.progressContainer.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        } else {
            binding.progressContainer.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }

        apiRepository.getGiftCard(currentPage, baseUrl, new ResponseListener<CommonDataResponse<List<GiftCardListItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<GiftCardListItem>> response, int statusCode) {

                loading = true;
                binding.progressBar.setVisibility(View.GONE);

                List<GiftCardListItem> list = response.getData();

                itemList.addAll(list);
                adapter.notifyItemRangeChanged(itemList.size() - list.size(), list.size());

                if (currentPage == 1)
                    binding.progressContainer.setVisibility(View.GONE);

                if (list.size() == 0 && currentPage == 1)
                    binding.noItem.setVisibility(View.VISIBLE);

                currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

}
