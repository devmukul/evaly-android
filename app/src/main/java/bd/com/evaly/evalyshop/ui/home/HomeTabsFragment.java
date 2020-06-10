package bd.com.evaly.evalyshop.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryDao;
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.databinding.FragmentHomeCategoryBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.browseProduct.tabs.TabsViewModel;
import bd.com.evaly.evalyshop.ui.browseProduct.tabs.adapter.TabsAdapter;
import bd.com.evaly.evalyshop.ui.home.adapter.RootCategoriesAdapter;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.ui.search.SearchCategory;
import bd.com.evaly.evalyshop.util.preference.MyPreference;

public class HomeTabsFragment extends Fragment {

    private TabsAdapter adapter;
    private RootCategoriesAdapter adapter2;
    private ArrayList<TabsItem> itemList;
    private int type = 1;
    private String slug = "root";
    private String category;
    private int brandCounter = 1, shopCounter = 1;
    private List<CategoryEntity> categoryItems;
    private FragmentHomeCategoryBinding binding;
    private AppDatabase appDatabase;
    private TabsViewModel viewModel;
    private CategoryDao categoryDao;

    public HomeTabsFragment() {
        // Required empty public constructor
    }

    public static HomeTabsFragment getInstance(int type, String slug, String category) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putString("slug", slug);
        bundle.putString("category", category);
        HomeTabsFragment fragment = new HomeTabsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        category = bundle.getString("category");
        type = bundle.getInt("type");
        slug = bundle.getString("slug");

        viewModel = new ViewModelProvider(this).get("type_" + type, TabsViewModel.class);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeCategoryBinding.inflate(inflater, container, false);
        Bundle bundle = getArguments();
        category = bundle.getString("category");
        type = bundle.getInt("type");
        slug = bundle.getString("slug");

        binding.shimmer.shimmer.startShimmer();

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appDatabase = AppDatabase.getInstance(getActivity());
        categoryDao = appDatabase.categoryDao();

        itemList = new ArrayList<>();
        adapter = new TabsAdapter(getContext(), (MainActivity) getActivity(), itemList, type);

        categoryItems = new ArrayList<>();

        if (slug.equals("root") && type == 1) {

            adapter2 = new RootCategoriesAdapter(getContext(), categoryItems, NavHostFragment.findNavController(this));
            binding.recyclerView.setAdapter(adapter2);
            adapter2.notifyDataSetChanged();

            appDatabase.categoryDao().getAllLiveData().observe(getViewLifecycleOwner(), categoryEntities -> {
                categoryItems.clear();
                categoryItems.addAll(categoryEntities);
                adapter2.notifyDataSetChanged();
                if (categoryEntities.size() > 0) {
                    viewModel.setItemCount(categoryEntities.size());
                    binding.btnHolder.setVisibility(View.VISIBLE);
                    stopShimmer();
                }
            });

            categoryDao.getCountLive().observe(getViewLifecycleOwner(), integer -> {
                if (integer == 0)
                    updateCategoryList();
                else if (getLastUpdated() == 0 || (getLastUpdated() != 0 && Calendar.getInstance().getTimeInMillis() - getLastUpdated() > 20200000))
                    updateCategoryList();
            });

            binding.showMoreBtnTabs.setVisibility(View.GONE);

            binding.shimmer.shimmer.postDelayed(this::stopShimmer, 3000);
        } else {
            binding.recyclerView.setAdapter(adapter);
        }

        binding.showMoreBtnTabs.setOnClickListener(v -> {
            binding.progressBar2.setVisibility(View.VISIBLE);

            if (type == 2) {
                getBrandsOfCategory(++brandCounter);
            } else if (type == 3) {
                getShopsOfCategory(++shopCounter);
            }
        });

        binding.searchBtnTabs.setOnClickListener(v -> {
            if (type == 1) {
                Intent intent = new Intent(getContext(), SearchCategory.class);
                intent.putExtra("type", type);
                getContext().startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), GlobalSearchActivity.class);
                intent.putExtra("type", type);
                getContext().startActivity(intent);
            }
        });

        loadData();
    }


    private void updateCategoryList() {

        binding.shimmer.shimmer.setVisibility(View.VISIBLE);
        binding.shimmer.shimmer.setAlpha(1);

        GeneralApiHelper.getRootCategories(new ResponseListenerAuth<List<CategoryEntity>, String>() {
            @Override
            public void onDataFetched(List<CategoryEntity> response, int statusCode) {
                setLastUpdated();
                Executors.newFixedThreadPool(5).execute(() -> {
                    categoryDao.insertAll(response);
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

    public long getLastUpdated() {
        return MyPreference.with(AppController.getmContext(), "category_db_new13").getLong("last_updated", 0);
    }

    public void setLastUpdated() {
        Calendar calendar = Calendar.getInstance();
        MyPreference.with(AppController.getmContext(), "category_db_new13").addLong("last_updated", calendar.getTimeInMillis()).save();
    }


    public void loadData() {
        if (!(slug.equals("root") && type == 1)) {
            if (type == 2) {
                binding.searchBtnTabs.setHint("Search brands");
                getBrandsOfCategory(1);
                binding.showMoreBtnTabs.setText("Show More");
            } else if (type == 3) {
                binding.searchBtnTabs.setHint("Search shops");
                getShopsOfCategory(1);
                binding.showMoreBtnTabs.setText("Show More");
            }
        }

        if (!(slug.equals("root") && type == 1)) {
            if (adapter.getItemCount() < 1 || binding.recyclerView.getHeight() < 100) {
                adapter.notifyDataSetChanged();

            }
        } else {
            if (adapter2.getItemCount() < 1 || binding.recyclerView.getHeight() < 100) {
                adapter2.notifyDataSetChanged();
            }
        }

    }

    public void stopShimmer() {

        if (binding == null)
            return;

        binding.shimmer.shimmer.stopShimmer();
        binding.shimmer.shimmer.setVisibility(View.GONE);
    }


    public void getBrandsOfCategory(int counter) {

        binding.shimmer.shimmer.setVisibility(View.VISIBLE);

        ProductApiHelper.getBrandsOfCategories(slug, counter, 12, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject res, int statusCode) {

                if (binding == null)
                    return;

                binding.progressBar2.setVisibility(View.GONE);

                JsonArray jsonArray = res.getAsJsonArray("results");

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject ob = jsonArray.get(i).getAsJsonObject();
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setTitle(ob.get("name").getAsString());
                    tabsItem.setImage(ob.get("image_url").isJsonNull() ? null : ob.get("image_url").getAsString());
                    tabsItem.setSlug(ob.get("slug").getAsString());
                    tabsItem.setCategory(category);
                    itemList.add(tabsItem);
                    adapter.notifyItemInserted(itemList.size());

                    viewModel.setItemCount(itemList.size());
                }


                binding.btnHolder.setVisibility(View.VISIBLE);
                stopShimmer();


            }

            @Override
            public void onFailed(String body, int errorCode) {

                if (binding != null)
                    binding.progressBar2.setVisibility(View.GONE);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    public void getShopsOfCategory(int counter) {
        binding.shimmer.shimmer.setVisibility(View.VISIBLE);

        ProductApiHelper.getShopsOfCategories(slug, counter, 12, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject res, int statusCode) {

                if (binding == null)
                    return;

                binding.progressBar2.setVisibility(View.GONE);


                JsonArray jsonArray = res.getAsJsonArray("data");

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject ob = jsonArray.get(i).getAsJsonObject();
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setTitle(ob.get("shop_name").getAsString());
                    tabsItem.setImage(ob.get("shop_image").isJsonNull() ? null : ob.get("shop_image").getAsString());

                    if (slug.equals("root"))
                        tabsItem.setSlug(ob.get("slug").getAsString());
                    else
                        tabsItem.setSlug(ob.get("shop_slug").getAsString());

                    tabsItem.setCategory(category);
                    itemList.add(tabsItem);
                    adapter.notifyItemInserted(itemList.size());

                    viewModel.setItemCount(itemList.size());

                }


                binding.btnHolder.setVisibility(View.VISIBLE);

                stopShimmer();

            }

            @Override
            public void onFailed(String body, int errorCode) {


                if (binding != null)
                    binding.progressBar2.setVisibility(View.GONE);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.setAdapter(null);
        itemList.clear();
        binding = null;
    }

    public class MyDefaultItemAnimator extends DefaultItemAnimator {

        @Override
        public void onAddFinished(RecyclerView.ViewHolder item) {
            super.onAddFinished(item);
        }

    }
}
