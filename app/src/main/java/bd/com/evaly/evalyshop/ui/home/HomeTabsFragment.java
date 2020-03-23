package bd.com.evaly.evalyshop.ui.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.databinding.FragmentHomeCategoryBinding;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.OnDoneListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.home.adapter.RootCategoriesAdapter;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.ui.search.SearchCategory;
import bd.com.evaly.evalyshop.ui.tabs.adapter.TabsAdapter;
import bd.com.evaly.evalyshop.util.CategoryUtils;

public class HomeTabsFragment extends Fragment {

    private TabsAdapter adapter;
    private RootCategoriesAdapter adapter2;
    private ArrayList<TabsItem> itemList;
    private int type = 1;
    private String slug = "root";
    private String category;
    private boolean isEmpty = false;
    private int brandCounter = 1, shopCounter = 1;
    private List<CategoryEntity> categoryItems;
    private OnDoneListener onDoneListener;
    private FragmentHomeCategoryBinding binding;

    public HomeTabsFragment() {
        // Required empty public constructor
    }

    public void setOnDoneListener(OnDoneListener onDoneListener) {
        this.onDoneListener = onDoneListener;
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


        itemList = new ArrayList<>();
        adapter = new TabsAdapter(getContext(), (MainActivity) getActivity(), itemList, type);

        // check if showing offline homepage vector categories

        categoryItems = new ArrayList<>();

        if (slug.equals("root") && type == 1) {

            CategoryUtils categoryUtils = new CategoryUtils(getContext());

            adapter2 = new RootCategoriesAdapter(getContext(), categoryItems, NavHostFragment.findNavController(this));
            binding.recyclerView.setAdapter(adapter2);
            binding.recyclerView.setItemAnimator(new MyDefaultItemAnimator());
            adapter2.notifyDataSetChanged();

            Calendar calendar = Calendar.getInstance();

            if (categoryUtils.getLastUpdated() == 0 || (categoryUtils.getLastUpdated() != 0 && calendar.getTimeInMillis() - categoryUtils.getLastUpdated() > 20200000)) {
                categoryUtils.updateFromApi(new DataFetchingListener<List<CategoryEntity>>() {
                    @Override
                    public void onDataFetched(List<CategoryEntity> response) {

                        if (binding != null && getActivity() != null && adapter2 != null) {
                            getActivity().runOnUiThread(() -> {
                                categoryItems.addAll(response);
                                adapter2.notifyDataSetChanged();
                                stopShimmer();
                            });
                        }
                    }

                    @Override
                    public void onFailed(int status) {

                    }
                });
            } else {

                categoryUtils.getLocalCategoryList(new DataFetchingListener<List<CategoryEntity>>() {
                    @Override
                    public void onDataFetched(List<CategoryEntity> response) {

                        if (binding != null && getActivity() != null && adapter2 != null) {
                            getActivity().runOnUiThread(() -> {
                                categoryItems.addAll(response);
                                adapter2.notifyDataSetChanged();
                                stopShimmer();
                            });
                        }
                    }

                    @Override
                    public void onFailed(int status) {

                    }
                });
                //skeletonScreen.hide();
            }

            binding.searchBtnTabs.setHint("Search categories");
            binding.showMoreBtnTabs.setVisibility(View.GONE);
            binding.searchBtnTabs.setVisibility(View.VISIBLE);
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

                if (onDoneListener != null)
                    onDoneListener.onDone();
                adapter2.notifyDataSetChanged();

            }
        }

    }

    public void stopShimmer() {

        if (binding == null)
            return;

        binding.shimmer.shimmer.animate().alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (binding == null)
                            return;
                        binding.shimmer.shimmer.stopShimmer();
                        binding.shimmer.shimmer.setVisibility(View.GONE);
                    }
                });
    }



    public void getBrandsOfCategory(int counter) {
        ProductApiHelper.getBrandsOfCategories(slug, counter, 12, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject res, int statusCode) {

                if (binding == null)
                    return;

                if (onDoneListener != null)
                    onDoneListener.onDone();

                binding.progressBar2.setVisibility(View.GONE);
                try {

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
                    }

                    stopShimmer();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Brand loading error", Toast.LENGTH_SHORT).show();
                }

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
        ProductApiHelper.getShopsOfCategories(slug, counter, 12, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject res, int statusCode) {

                if (binding == null)
                    return;

                if (onDoneListener != null)
                    onDoneListener.onDone();

                binding.progressBar2.setVisibility(View.GONE);
                try {

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

                    }

                    stopShimmer();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "ShopDetails error", Toast.LENGTH_SHORT).show();
                }
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


    public class MyDefaultItemAnimator extends DefaultItemAnimator {

        @Override
        public void onAddFinished(RecyclerView.ViewHolder item) {
            super.onAddFinished(item);

            if (onDoneListener != null)
                onDoneListener.onDone();
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.setAdapter(null);
        itemList.clear();
        binding = null;
    }
}
