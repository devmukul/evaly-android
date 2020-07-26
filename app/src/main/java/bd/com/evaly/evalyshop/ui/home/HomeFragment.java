package bd.com.evaly.evalyshop.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.pref.ReferPref;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.express.ExpressServiceDao;
import bd.com.evaly.evalyshop.databinding.FragmentAppBarHeaderBinding;
import bd.com.evaly.evalyshop.databinding.FragmentHomeBinding;
import bd.com.evaly.evalyshop.listener.NetworkErrorDialogListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ExpressApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.home.controller.HomeController;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private MainActivity activity;
    private Context context;
    private int currentPage = 1;
    private List<ProductItem> productItemList;
    private boolean isLoading = false;
    private ReferPref referPref;
    private FragmentHomeBinding binding;
    private NavController navController;
    private HomeController homeController;
    private AppDatabase appDatabase;
    private ExpressServiceDao expressServiceDao;
    private HomeViewModel viewModel;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onRefresh() {
        binding.swipeRefresh.setRefreshing(false);
        refreshFragment();
    }

    private void refreshFragment() {
        navController.navigate(HomeFragmentDirections.actionHomeFragmentPop());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        context = getContext();
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        appDatabase = AppDatabase.getInstance(getActivity());

        binding.swipeRefresh.setOnRefreshListener(this);
        return binding.getRoot();
    }

    private void networkCheck() {
        if (!Utils.isNetworkAvailable(context))
            new NetworkErrorDialog(context, new NetworkErrorDialogListener() {
                @Override
                public void onRetry() {
                    refreshFragment();
                }

                @Override
                public void onBackPress() {
                    navController.navigate(R.id.homeFragment);
                }
            });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        networkCheck();
        referPref = new ReferPref(context);

        expressServiceDao = appDatabase.expressServiceDao();

        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        new InitializeActionBar(view.findViewById(R.id.header_logo), getActivity(), "home", mainViewModel);
        FragmentAppBarHeaderBinding headerBinding = binding.header;
        headerBinding.homeSearch.setOnClickListener(view1 -> startActivity(new Intent(getContext(), GlobalSearchActivity.class)));

        productItemList = new ArrayList<>();

        homeController = new HomeController();
        homeController.setActivity((AppCompatActivity) getActivity());
        homeController.setFragment(this);
        homeController.setHomeViewModel(viewModel);

        binding.recyclerView.setAdapter(homeController.getAdapter());

        int spanCount = 2;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        homeController.setSpanCount(spanCount);

        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        binding.recyclerView.setLayoutManager(layoutManager);


        homeController.requestModelBuild();

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
                            getProducts();
                }
            }
        });



        currentPage = 1;

        checkReferral();

      //  getExpressShops();
        getProducts();

    }


    private void getExpressShops() {
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

    private void getProducts() {

        homeController.setLoadingMore(true);

        isLoading = true;
        ProductApiHelper.getCategoryBrandProducts(currentPage, "root", null, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {

                if (binding == null)
                    return;

                homeController.setLoadingMore(false);

                List<ProductItem> list = response.getData();

                long timeInMill = Calendar.getInstance().getTimeInMillis();

                for (ProductItem item : list)
                    item.setUniqueId(item.getSlug() + timeInMill);

                homeController.addData(list);

                isLoading = false;

                if (response.getCount() > 10)
                    currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                homeController.setLoadingMore(false);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


    private void checkReferral() {

        if (!referPref.getRef().equals("")) {

            HashMap<String, String> params = new HashMap<>();
            params.put("token", CredentialManager.getToken().trim());
            params.put("referred_by", referPref.getRef().trim());
            params.put("device_id", Utils.getDeviceID(context).trim());

            GeneralApiHelper.checkReferral(params, new ResponseListenerAuth<JsonObject, String>() {
                @Override
                public void onDataFetched(JsonObject response, int statusCode) {

                    String message = response.get("message").getAsString();

                    if (!message.equals("")) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        referPref.setRef("");
                    }
                }

                @Override
                public void onFailed(String errorBody, int errorCode) {
                    Toast.makeText(context, "Server error occurred, couldn't verify invitation code.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onAuthError(boolean logout) {

                }
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}
