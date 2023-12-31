package bd.com.evaly.evalyshop.ui.buynow;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.FragmentBuyNowBinding;
import bd.com.evaly.evalyshop.models.product.productDetails.AvailableShopResponse;
import bd.com.evaly.evalyshop.models.shop.shopItem.AttributesItem;
import bd.com.evaly.evalyshop.models.shop.shopItem.ShopItem;
import bd.com.evaly.evalyshop.ui.auth.login.SignInActivity;
import bd.com.evaly.evalyshop.ui.base.BaseBottomSheetFragment;
import bd.com.evaly.evalyshop.ui.buynow.adapter.VariationAdapter;
import bd.com.evaly.evalyshop.ui.cart.CartViewModel;
import bd.com.evaly.evalyshop.ui.checkout.CheckoutFragment;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BuyNowFragment extends BaseBottomSheetFragment<FragmentBuyNowBinding, BuyNowViewModel> implements VariationAdapter.ClickListenerVariation {

    @Inject
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    @Inject
    PreferenceRepository preferenceRepository;

    private CartViewModel cartViewModel;
    private SkeletonScreen skeleton;
    private ArrayList<ShopItem> itemsList;
    private String shop_item_slug = "tvs-apache-rtr-160cc-single-disc";
    private int quantityCount = 1;
    private double productPriceInt = 0;
    private double productActualPriceInt = 0;
    private VariationAdapter adapterVariation;
    private ViewDialog dialog;
    private CartEntity cartItem;
    private AvailableShopResponse shopItem;
    private NavController navController;

    public static BuyNowFragment newInstance(String shopSlug, String productSlug) {
        BuyNowFragment f = new BuyNowFragment();
        Bundle args = new Bundle();
        args.putString("shopSlug", shopSlug);
        args.putString("productSlug", productSlug);
        f.setArguments(args);
        return f;
    }

    public static BuyNowFragment createInstance(CartEntity cartItem, AvailableShopResponse shopItemModel) {
        BuyNowFragment f = new BuyNowFragment();
        Bundle args = new Bundle();
        args.putSerializable("cartItem", cartItem);
        args.putSerializable("shopItem", shopItemModel);
        f.setArguments(args);
        return f;
    }

    public BuyNowFragment() {
        super(BuyNowViewModel.class, R.layout.fragment_buy_now);
    }

    @Override
    public void selectVariation(int position) {

        for (int i = 0; i < itemsList.size(); i++) {
            if (i == position) {
                itemsList.get(i).setSelected(true);
                if (itemsList.get(i).getAttributes().size() > 0) {
                    AttributesItem attributesItem = itemsList.get(i).getAttributes().get(0);
                    String varName = attributesItem.getName();
                    String varValue = attributesItem.getValue();
                    //variationTitle.setText(varName + ": " + varValue);
                    loadProductById(position);
                }
            } else
                itemsList.get(i).setSelected(false);
        }
        adapterVariation.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentInputBottomSheetDialog);

        dialog = new ViewDialog(getActivity());

        Bundle args = getArguments();
        if (args.containsKey("productSlug"))
            shop_item_slug = args.getString("productSlug");

        if (args.containsKey("shopItem"))
            shopItem = (AvailableShopResponse) args.getSerializable("shopItem");
        if (args.containsKey("cartItem"))
            cartItem = (CartEntity) args.getSerializable("cartItem");

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }


    public void inflateFromModel() {
        skeleton.hide();

        shop_item_slug = cartItem.getSlug();

        productPriceInt = shopItem.getDiscountedPrice();
        productActualPriceInt = shopItem.getPrice();
        inflateQuantity();

        binding.productName.setText(cartItem.getName());
        binding.shopName.setText(String.format("Seller: %s", shopItem.getShopName()));

        Glide.with(this)
                .load(cartItem.getImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(250, 250))
                .into(binding.productImage);

        binding.variationHolder.setVisibility(View.GONE);

        binding.addCart.setOnClickListener(v -> {
            cartViewModel.insert(getCartItem());
            ToastUtils.show("Added to cart");
            dismiss();
        });

        binding.buyNow.setOnClickListener(view1 -> {
            if (preferenceRepository.getToken().equals("")) {
                startActivity(new Intent(getContext(), SignInActivity.class));
                return;
            }
            if (isVisible())
                dismissAllowingStateLoss();

            CartEntity cartEntity = getCartItem();
            Bundle bundle = new Bundle();
            bundle.putSerializable("model", cartEntity);
            // navController.navigate(R.id.checkoutFragment, bundle);
            CheckoutFragment checkoutFragment = new CheckoutFragment();
            checkoutFragment.setArguments(bundle);
            checkoutFragment.show(getParentFragmentManager(), "Checkout");
        });
    }


    private CartEntity getCartItem() {
        int quantity = 1;
        try {
            quantity = Integer.parseInt(binding.quantity.getText().toString());
        } catch (Exception ignore) {
        }

        Calendar calendar = Calendar.getInstance();

        CartEntity cartEntity = new CartEntity();
        cartEntity.setName(cartItem.getName());
        cartEntity.setImage(cartItem.getImage());
        cartEntity.setPriceRound(String.valueOf(shopItem.getPrice()));
        cartEntity.setDiscountedPrice(shopItem.getDiscountedPrice());
        cartEntity.setExpressShop(shopItem.isExpressShop());
        cartEntity.setQuantity(quantity);
        cartEntity.setShopSlug(shopItem.getShopSlug());
        cartEntity.setShopName(shopItem.getShopName());
        cartEntity.setSlug(cartItem.getSlug());
        cartEntity.setVariantDetails(cartItem.getVariantDetails());
        cartEntity.setProductID(String.valueOf(shopItem.getShopItemId()));
        cartEntity.setTime(Calendar.getInstance().getTimeInMillis());

        return cartEntity;
    }

    @SuppressLint("DefaultLocale")
    private void inflateQuantity() {
        binding.quantity.setText(String.format("%d", quantityCount));
        binding.price.setText(String.format("%s x %d", Utils.formatPriceSymbol(productPriceInt), quantityCount));
        binding.priceTotal.setText(Utils.formatPriceSymbol(productPriceInt * quantityCount));
        binding.priceTotalDiscounted.setText(Utils.formatPriceSymbol(productActualPriceInt * quantityCount));

        if (productPriceInt > 0 && productActualPriceInt > productPriceInt) {
            binding.priceTotalDiscounted.setVisibility(View.VISIBLE);
            binding.priceTotalDiscounted.setPaintFlags(binding.priceTotalDiscounted.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else
            binding.priceTotalDiscounted.setVisibility(View.GONE);
    }


    @Override
    protected void initViews() {
        if (getActivity() instanceof MainActivity)
            navController = NavHostFragment.findNavController(this);

        skeleton = Skeleton.bind(binding.linearLayout)
                .load(R.layout.skeleton_buy_now_modal)
                .color(R.color.ddd)
                .shimmer(true).show();

        setupRecycler();
        if (shopItem == null) {
            skeleton.show();
        } else
            inflateFromModel();
    }

    @Override
    protected void clickListeners() {
        binding.minus.setOnClickListener(view1 -> {
            if (quantityCount > 1) {
                quantityCount--;
                inflateQuantity();
            }
        });

        binding.plus.setOnClickListener(view1 -> {
            quantityCount++;
            inflateQuantity();
        });
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.liveList.observe(getViewLifecycleOwner(), shopItems -> {
            skeleton.hide();
            itemsList.clear();
            itemsList.addAll(shopItems);
            adapterVariation.notifyDataSetChanged();
            if (itemsList.size() > 0) {
                itemsList.get(0).setSelected(true);
                loadProductById(0);
            }
        });
    }

    private void setupRecycler() {
        itemsList = new ArrayList<>();
        adapterVariation = new VariationAdapter(itemsList, getContext(), this);

        LinearLayoutManager managerVariation = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerViewVariation.setLayoutManager(managerVariation);
        binding.recyclerViewVariation.setAdapter(adapterVariation);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog dialogz = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = dialogz.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
                BottomSheetBehavior.from(bottomSheet).setHideable(true);
            }
        });
        return bottomSheetDialog;
    }


    private void loadProductById(int position) {

        ShopItem firstItem = itemsList.get(position);

        productPriceInt = firstItem.getShopItemDiscountedPriceD();
        productActualPriceInt = firstItem.getShopItemPriceD();

        binding.productName.setText(firstItem.getShopItemName());
        binding.shopName.setText(String.format("Seller: %s", firstItem.getShopName()));

        inflateQuantity();

        if (getContext() != null && this.isVisible() && !requireActivity().isDestroyed())
            Glide.with(getContext())
                    .load(firstItem.getFirstImage())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(new RequestOptions().override(250, 250))
                    .into(binding.productImage);

        if (firstItem.getAttributes().size() > 0) {
            binding.variationHolder.setVisibility(View.VISIBLE);
            AttributesItem attributesItem = firstItem.getAttributes().get(0);
            String varName = attributesItem.getName();
            String varValue = attributesItem.getValue();
            // variationTitle.setText(varName + ": " + varValue);
        } else
            binding.variationHolder.setVisibility(View.GONE);

        binding.addCart.setOnClickListener(v -> {
            CartEntity cartEntity = getCartEntity(firstItem);
            cartViewModel.insert(getCartEntity(firstItem));
            ToastUtils.show("Added to cart");
            dismiss();
        });


        binding.buyNow.setOnClickListener(view1 -> {
            if (preferenceRepository.getToken().equals("")) {
                startActivity(new Intent(getContext(), SignInActivity.class));
                return;
            }

            if (isVisible())
                dismissAllowingStateLoss();

            CartEntity cartEntity = getCartEntity(firstItem);
            Bundle bundle = new Bundle();
            bundle.putSerializable("model", cartEntity);
            if (navController != null)
                navController.navigate(R.id.checkoutFragment, bundle);
            else {
                CheckoutFragment checkoutFragment = new CheckoutFragment();
                checkoutFragment.setArguments(bundle);
                checkoutFragment.show(getParentFragmentManager(), "checkout");
            }
        });

    }


    public CartEntity getCartEntity(ShopItem firstItem) {

        CartEntity cartEntity = new CartEntity();
        cartEntity.setName(firstItem.getShopItemName());
        cartEntity.setImage(firstItem.getShopItemImage());
        cartEntity.setPriceRound(firstItem.getShopItemPrice());
        cartEntity.setDiscountedPrice(firstItem.getShopItemDiscountedPrice());
        cartEntity.setExpressShop(firstItem.isExpress());
        cartEntity.setQuantity(quantityCount);
        cartEntity.setShopSlug(firstItem.getShopSlug());
        cartEntity.setShopName(firstItem.getShopName());
        cartEntity.setSlug(shop_item_slug);
        cartEntity.setProductID(String.valueOf(firstItem.getShopItemId()));
        cartEntity.setTime(Calendar.getInstance().getTimeInMillis());

        StringBuilder variantDetails = new StringBuilder();
        for (AttributesItem entry : firstItem.getAttributes()) {
            String val = entry.getName() + ": " + entry.getValue();
            variantDetails.append(val).append(", ");
        }

        if (variantDetails != null && variantDetails.length() > 0) {
            String val = variantDetails.toString();
            val = val.replaceAll(", $", "");
            cartEntity.setVariantDetails(val);
        }

        return cartEntity;
    }
}
