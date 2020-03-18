package bd.com.evaly.evalyshop.ui.tabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentHomeCategoryBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.ui.search.SearchCategory;
import bd.com.evaly.evalyshop.ui.tabs.adapter.TabsAdapter;

public class SubTabsFragment extends Fragment {

    private TabsAdapter adapter;
    private Context context;
    private ArrayList<TabsItem> itemList;
    private int type = 1;
    private String slug = "root";
    private String category;
    private int brandCounter = 1, shopCounter = 1;
    private String json = "[]";
    private FragmentHomeCategoryBinding binding;

    public SubTabsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeCategoryBinding.inflate(inflater, container, false);

        context = getContext();
        Bundle bundle = getArguments();

        category = bundle.getString("category");
        type = bundle.getInt("type");
        slug = bundle.getString("slug");
        json = bundle.getString("json");

        try {
            binding.shimmer.shimmer.startShimmer();
        } catch (Exception e) {
        }


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

            if (type == 1) {
                Intent intent = new Intent(context, SearchCategory.class);
                intent.putExtra("type", type);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, GlobalSearchActivity.class);
                intent.putExtra("type", type);
                context.startActivity(intent);
            }
        });

        if (json.equals(""))
            loadData();
        else {
            loadJsonToView(json, type);
        }


    }

    public void loadData() {
        if (slug != null) {

            if (!(slug.equals("root") && type == 1)) {
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
            }
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (binding == null)
                        return;

                    if (!(slug.equals("root") && type == 1)) {
                        if (adapter.getItemCount() < 1 || binding.recyclerView.getHeight() < 100) {
                            adapter.notifyDataSetChanged();
                            handler.postDelayed(this, 1000);
                        }
                    }
                }
            }, 1000);
        } else {
            Toast.makeText(getContext(), "Page is not available, go back please.", Toast.LENGTH_SHORT).show();
        }
    }


    public void stopShimmer() {
        if (binding == null)
            return;

        binding.shimmer.shimmer.stopShimmer();
        binding.shimmer.shimmer.setVisibility(View.GONE);

//        binding.shimmer.shimmer.animate().alpha(0.0f)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        binding.shimmer.shimmer.stopShimmer();
//                        binding.shimmer.shimmer.setVisibility(View.GONE);
//                    }
//                });
    }

    public void loadJsonToView(String json, int type) {

        if (binding == null)
            return;

        if (slug != null) {

            if (!(slug.equals("root") && type == 1)) {
                if (type == 1) {
                    binding.searchBtnTabs.setVisibility(View.GONE);
                    binding.showMoreBtnTabs.setVisibility(View.GONE);
                }
            }

            try {
                JsonParser parser = new JsonParser();
                JsonElement tradeElement = parser.parse(json);
                JsonArray response = tradeElement.getAsJsonArray();

                for (int i = 0; i < response.size(); i++) {
                    try {
                        JsonObject ob = response.get(i).getAsJsonObject();
                        TabsItem tabsItem = new TabsItem();

                        if (type == 3) {
                            tabsItem.setTitle(ob.get("shop_name").getAsString());
                            tabsItem.setImage(ob.get("shop_image").isJsonNull() ? null : ob.get("shop_image").getAsString());
                            tabsItem.setSlug(ob.get("shop_slug").getAsString());
                        } else {
                            tabsItem.setTitle(ob.get("name").getAsString());
                            tabsItem.setImage(ob.get("image_url").isJsonNull() ? null : ob.get("image_url").getAsString());
                            tabsItem.setSlug(ob.get("slug").getAsString());
                        }

                        tabsItem.setCategory(category);
                        itemList.add(tabsItem);
                        adapter.notifyItemInserted(itemList.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                stopShimmer();
            } catch (Exception e) {

            }
        } else {
            Toast.makeText(context, "Page is empty!", Toast.LENGTH_SHORT).show();
        }
    }

    public void getSubCategories() {

        ProductApiHelper.getSubCategories(slug, new ResponseListenerAuth<JsonArray, String>() {

            @Override
            public void onDataFetched(JsonArray res, int statusCode) {

                if (binding == null)
                    return;

                binding.progressBar2.setVisibility(View.GONE);

                try {
                    JsonArray response = res;

                    for (int i = 0; i < response.size(); i++) {
                        JsonObject ob = response.get(i).getAsJsonObject();
                        TabsItem tabsItem = new TabsItem();
                        tabsItem.setTitle(ob.get("name").getAsString());
                        tabsItem.setImage(ob.get("image_url").getAsString());
                        tabsItem.setSlug(ob.get("slug").getAsString());
                        tabsItem.setCategory(category);
                        itemList.add(tabsItem);
                        adapter.notifyItemInserted(itemList.size());

                    }


                } catch (Exception e) {
                    e.printStackTrace();
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

    public void getBrandsOfCategory(int counter) {
        ProductApiHelper.getBrandsOfCategories(slug, counter, 12, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject res, int statusCode) {

                if (binding == null)
                    return;

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
                    Toast.makeText(context, "Brand loading error", Toast.LENGTH_SHORT).show();
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

                binding.progressBar2.setVisibility(View.GONE);
                try {

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


                    }


                    stopShimmer();

                } catch (Exception e) {

                    Toast.makeText(context, "Shop Loading Error", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
        binding.recyclerView.setAdapter(null);
        binding = null;
    }
}
