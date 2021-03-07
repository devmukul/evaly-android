package bd.com.evaly.evalyshop.ui.browseProduct.tabs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentBrowseProductTabBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.browseProduct.tabs.adapter.TabsAdapter;
import bd.com.evaly.evalyshop.ui.main.MainActivity;

public class SubTabsFragment extends Fragment {

    private TabsAdapter adapter;
    private Context context;
    private ArrayList<TabsItem> itemList;
    private int type = 1;
    private String slug = "root";
    private String category;
    private int brandCounter = 1, shopCounter = 1;
    private final String json = "[]";
    private FragmentBrowseProductTabBinding binding;
    private TabsViewModel viewModel;

    public SubTabsFragment() {
        // Required empty public constructor
    }

    public static SubTabsFragment getInstance(int type, String slug, String category) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putString("slug", slug);
        bundle.putString("category", category);
        SubTabsFragment fragment = new SubTabsFragment();
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
        binding = FragmentBrowseProductTabBinding.inflate(inflater, container, false);

        context = getContext();
        binding.shimmer.shimmer.startShimmer();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemList = new ArrayList<>();
        adapter = new TabsAdapter(context, (MainActivity) getActivity(), itemList, type);
        binding.recyclerView.setAdapter(adapter);


        binding.showMoreBtnTabs.setOnClickListener(v -> {

            binding.progressBar2.setVisibility(View.VISIBLE);

            if (type == 2) {
                getBrandsOfCategory(++brandCounter);
            } else if (type == 3) {
                getShopsOfCategory(++shopCounter);
            }
        });

        binding.searchBtnTabs.setOnClickListener(v -> {

        });


        loadData();


    }

    public void loadData() {
        if (slug != null) {

            if (type == 1) {
                binding.searchBtnTabs.setHint(R.string.search_categories);
                binding.showMoreBtnTabs.setVisibility(View.GONE);
                getSubCategories();
            } else if (type == 2) {
                binding.searchBtnTabs.setHint(R.string.search_brands);
                getBrandsOfCategory(1);
                binding.showMoreBtnTabs.setText(R.string.show_more);
            } else if (type == 3) {
                binding.searchBtnTabs.setHint(R.string.search_shops);
                getShopsOfCategory(1);
                binding.showMoreBtnTabs.setText(R.string.show_more);
            }

        } else {
            Toast.makeText(getContext(), "Page is not available, go back please.", Toast.LENGTH_SHORT).show();
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


    public void getSubCategories() {


        ProductApiHelper.getSubCategories(slug, new ResponseListenerAuth<JsonArray, String>() {

            @Override
            public void onDataFetched(JsonArray res, int statusCode) {

                if (binding == null)
                    return;

                for (int i = 0; i < res.size(); i++) {

                    JsonObject ob = res.get(i).getAsJsonObject();
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setTitle(ob.get("name").getAsString());
                    tabsItem.setImage(ob.get("image_url").isJsonNull() ? null : ob.get("image_url").getAsString());
                    tabsItem.setSlug(ob.get("slug").getAsString());
                    tabsItem.setCategory(category);
                    itemList.add(tabsItem);
                    adapter.notifyItemInserted(itemList.size());

                }

                viewModel.setItemCount(itemList.size());

                if (res.size() == 0)
                    binding.noCat.setVisibility(View.VISIBLE);
                else
                    binding.noCat.setVisibility(View.GONE);

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

    public void getBrandsOfCategory(int counter) {
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
        ProductApiHelper.getShopsOfCategories(slug, counter, 12, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject res, int statusCode) {

                if (binding == null)
                    return;

                binding.progressBar2.setVisibility(View.GONE);


                JsonArray jsonArray = res.get("data").getAsJsonArray();

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

                stopShimmer();

                binding.btnHolder.setVisibility(View.VISIBLE);
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
        adapter = null;
        binding.recyclerView.setAdapter(null);
        binding = null;
    }
}
