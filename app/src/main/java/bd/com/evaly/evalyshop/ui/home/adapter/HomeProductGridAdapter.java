package bd.com.evaly.evalyshop.ui.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.OnDoneListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.banner.BannerItem;
import bd.com.evaly.evalyshop.models.notification.NotificationCount;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.campaign.CampaignBottomSheetFragment;
import bd.com.evaly.evalyshop.ui.giftcard.GiftCardActivity;
import bd.com.evaly.evalyshop.ui.home.HomeTabsFragment;
import bd.com.evaly.evalyshop.ui.newsfeed.NewsfeedActivity;
import bd.com.evaly.evalyshop.ui.order.orderList.OrderListActivity;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import retrofit2.Response;

public class HomeProductGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Context context;
    private List<ProductItem> productsList;
    private Fragment fragmentInstance;
    private AppCompatActivity activityInstance;

    public HomeProductGridAdapter(Context context, List<ProductItem> a, AppCompatActivity activityInstance, Fragment fragmentInstance) {
        this.context = context;
        productsList = a;
        this.fragmentInstance = fragmentInstance;
        this.activityInstance = activityInstance;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View v = inflater.inflate(R.layout.recycler_header_home, parent, false);
            return  new VHHeader(v);
        } else {
            View v = inflater.inflate(R.layout.home_product_grid_item, parent, false);
            return new VHItem(v);
        }
    }

    View.OnClickListener itemViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag(); // get item for position

            Intent intent=new Intent(context, ViewProductActivity.class);
            intent.putExtra("product_slug",productsList.get(position).getSlug());
            intent.putExtra("product_name",productsList.get(position).getName());
            intent.putExtra("product_price",productsList.get(position).getMaxPrice());
            if (productsList.get(position).getImageUrls().size()>0)
                intent.putExtra("product_image", productsList.get(position).getImageUrls().get(0));
            context.startActivity(intent);
        }
    };


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderz, int position) {
        if (holderz instanceof VHHeader) {
//            VHHeader holder = (VHHeader) holderz;
//            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
//            layoutParams.setFullSpan(true);

        } else if (holderz instanceof VHItem){

            VHItem holder = (VHItem) holderz;

            ProductItem model = productsList.get(position);

            if(model.getName().contains("-")){
                String str = model.getName();
                str = str.trim();
                String regex = "-[a-zA-Z0-9]+$";

                final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                final Matcher matcher = pattern.matcher(str);

                if(matcher.find()){
                    String output = str.replaceAll(regex, "");
                    holder.textViewAndroid.setText(Html.fromHtml(output));
                }
                else
                    holder.textViewAndroid.setText(Html.fromHtml(model.getName()));
            }else
                holder.textViewAndroid.setText(Html.fromHtml(model.getName()));

            if (context != null)
                Glide.with(context)
                        .asBitmap()
                        .skipMemoryCache(true)
                        .apply(new RequestOptions().override(260, 260))
                        .load(model.getImageUrls().get(0))
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.ic_placeholder_small)
                        .into(holder.imageViewAndroid);


            if((model.getMinPriceD()==0) || (model.getMaxPriceD()==0))
                holder.price.setText("Call For Price");

            else if(model.getMinDiscountedPriceD() != 0){

                if (model.getMinDiscountedPriceD() < model.getMinPriceD()){
                    holder.priceDiscount.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinPriceD()));
                    holder.priceDiscount.setVisibility(View.VISIBLE);
                    holder.priceDiscount.setPaintFlags(holder.priceDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinDiscountedPriceD()));
                } else {
                    holder.priceDiscount.setVisibility(View.GONE);
                    holder.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinPriceD()));
                }

            } else
                holder.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinPriceD()));

            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(itemViewListener);

            holder.buyNow.setVisibility(View.GONE);

            if ((model.getMinPriceD()==0) || (model.getMaxPriceD()==0)){
                holder.buyNow.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }



    class VHHeader extends RecyclerView.ViewHolder{

        View view;

        private VHHeader(View itemView) {
            super(itemView);
            view = itemView;
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.setFullSpan(true);
            initHeader(view);
        }
    }

    class VHItem extends RecyclerView.ViewHolder{
        TextView textViewAndroid,price,priceDiscount,buyNow,tvCashback;
        ImageView imageViewAndroid;
        View itemView;

        public VHItem(final View itemView) {
            super(itemView);
            textViewAndroid=itemView.findViewById(R.id.title);
            price=itemView.findViewById(R.id.price);
            imageViewAndroid=itemView.findViewById(R.id.image);
            priceDiscount = itemView.findViewById(R.id.priceDiscount);
            buyNow = itemView.findViewById(R.id.buy_now);
            tvCashback = itemView.findViewById(R.id.tvCashback);
            this.itemView = itemView;
        }
    }

    private boolean isPositionHeader(int position) {
        return productsList.get(position) instanceof HomeHeaderItem;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    public void setFilter(ArrayList<ProductItem> ar){
        productsList=new ArrayList<>();
        productsList.addAll(ar);
        notifyDataSetChanged();
    }



    private void initHeader(View view){

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setSmoothScrollingEnabled(true);

        LinearLayout voucher = view.findViewById(R.id.voucher);
        
        final ViewPager viewPager = view.findViewById(R.id.pager);
        HomeTabPagerAdapter pager = new HomeTabPagerAdapter(fragmentInstance.getChildFragmentManager());

        ShimmerFrameLayout shimmer = view.findViewById(R.id.shimmer);

        try {
            shimmer.setVisibility(View.VISIBLE);
            shimmer.startShimmer();
        } catch (Exception ignored) { }

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(pager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(1);


        LinearLayout evalyStore = view.findViewById(R.id.evaly_store);
        evalyStore.setOnClickListener(v -> {
            Intent ni = new Intent(context, GiftCardActivity.class);
            context.startActivity(ni);
        });

        voucher.setOnClickListener(v -> {
            CampaignBottomSheetFragment campaignBottomSheetFragment = CampaignBottomSheetFragment.newInstance();
            if (fragmentInstance.getFragmentManager() != null)
                campaignBottomSheetFragment.show(fragmentInstance.getFragmentManager(), "Campaign BottomSheet");
        });


        LinearLayout wholesale = view.findViewById(R.id.evaly_wholesale);
        wholesale.setOnClickListener(v -> context.startActivity(new Intent(context, NewsfeedActivity.class)));


        LinearLayout orders = view.findViewById(R.id.orders);
        orders.setOnClickListener(v -> {

            if (CredentialManager.getToken().equals("")) {
                context.startActivity(new Intent(context, SignInActivity.class));
            } else {
                context.startActivity(new Intent(context, OrderListActivity.class));
            }
        });


        // slider
        ViewPager sliderPager = view.findViewById(R.id.sliderPager);
        TabLayout sliderIndicator = view.findViewById(R.id.sliderIndicator);


        HomeTabsFragment categoryFragment = new HomeTabsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("slug", "root");
        bundle.putString("category", "root");
        categoryFragment.setArguments(bundle);


        HomeTabsFragment brandFragment = new HomeTabsFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("type", 2);
        bundle2.putString("slug", "root");
        bundle2.putString("category", "root");
        brandFragment.setArguments(bundle2);

        OnDoneListener onDoneListener = () -> shimmer.setVisibility(View.GONE);

        brandFragment.setOnDoneListener(onDoneListener);
        categoryFragment.setOnDoneListener(onDoneListener);

        HomeTabsFragment shopFragment = new HomeTabsFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putInt("type", 3);
        bundle3.putString("slug", "root");
        bundle3.putString("category", "root");
        shopFragment.setArguments(bundle3);

        pager.addFragment(categoryFragment, "Categories");
        pager.addFragment(brandFragment, "Brands");
        pager.addFragment(shopFragment, "Shops");
        pager.notifyDataSetChanged();


        Handler handler = new Handler();
        handler.postDelayed(() -> {
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);
        }, 1500);

        AuthApiHelper.getBanners(new DataFetchingListener<Response<JsonObject>>() {
            @Override
            public void onDataFetched(Response<JsonObject> response) {
                if (response.code() == 200 || response.code() == 201) {
                    ArrayList<BannerItem> sliderImages = new Gson().fromJson(response.body().get("results"), new TypeToken<List<BannerItem>>() {
                    }.getType());
                    sliderPager.setAdapter(new SliderAdapter(context, activityInstance, sliderImages));
                    sliderIndicator.setupWithViewPager(sliderPager, true);
                }
            }

            @Override
            public void onFailed(int status) {

            }
        });




        if (!CredentialManager.getToken().equals("")){
            GeneralApiHelper.getNotificationCount(CredentialManager.getToken(), "newsfeed", new ResponseListenerAuth<NotificationCount, String>() {
                @Override
                public void onDataFetched(NotificationCount response, int statusCode) {
                    if (response.getCount() > 0)
                        view.findViewById(R.id.newsfeedIndicator).setVisibility(View.VISIBLE);
                    else
                        view.findViewById(R.id.newsfeedIndicator).setVisibility(View.GONE);
                }

                @Override
                public void onFailed(String errorBody, int errorCode) {

                }

                @Override
                public void onAuthError(boolean logout) {


                }
            });
        }


    }
}

