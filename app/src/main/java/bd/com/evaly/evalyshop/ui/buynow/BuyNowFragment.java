package bd.com.evaly.evalyshop.ui.buynow;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.FragmentBuyNowBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.product.productDetails.AvailableShopModel;
import bd.com.evaly.evalyshop.models.shop.shopItem.AttributesItem;
import bd.com.evaly.evalyshop.models.shop.shopItem.ShopItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.buynow.adapter.VariationAdapter;
import bd.com.evaly.evalyshop.ui.checkout.CheckoutFragment;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BuyNowFragment extends BottomSheetDialogFragment implements VariationAdapter.ClickListenerVariation {

    @Inject
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    @Inject
    CartDao cartDao;
    private FragmentBuyNowBinding binding;
    private SkeletonScreen skeleton;
    private Context context;
    private ArrayList<ShopItem> itemsList;
    private String shop_slug = "tvs-bangladesh";
    private String shop_item_slug = "tvs-apache-rtr-160cc-single-disc";
    private int quantityCount = 1;
    private double productPriceInt = 0;
    private VariationAdapter adapterVariation;
    private ViewDialog dialog;
    private CartEntity cartItem;
    private AvailableShopModel shopItem;
    private NavController navController;


    public static BuyNowFragment newInstance(String shopSlug, String productSlug) {
        BuyNowFragment f = new BuyNowFragment();
        Bundle args = new Bundle();
        args.putString("shopSlug", shopSlug);
        args.putString("productSlug", productSlug);
        f.setArguments(args);
        return f;
    }

    public static BuyNowFragment createInstance(CartEntity cartItem, AvailableShopModel shopItemModel) {
        BuyNowFragment f = new BuyNowFragment();
        Bundle args = new Bundle();
        args.putSerializable("cartItem", cartItem);
        args.putSerializable("shopItem", shopItemModel);
        f.setArguments(args);
        return f;
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
        if (args.containsKey("shopSlug"))
            shop_slug = args.getString("shopSlug");
        if (args.containsKey("productSlug"))
            shop_item_slug = args.getString("productSlug");

        if (args.containsKey("shopItem"))
            shopItem = (AvailableShopModel) args.getSerializable("shopItem");
        if (args.containsKey("cartItem"))
            cartItem = (CartEntity) args.getSerializable("cartItem");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBuyNowBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    public void inflateFromModel() {
        skeleton.hide();

        shop_item_slug = cartItem.getSlug();
        shop_slug = shopItem.getShopSlug();
        productPriceInt = shopItem.getPrice();

        if (shopItem.getDiscountedPrice() != null)
            if (shopItem.getDiscountedPrice() > 0) {
                double disPrice = shopItem.getDiscountedPrice();
                binding.price.setText(String.format("%s x 1", Utils.formatPriceSymbol(disPrice)));
                binding.priceTotal.setText(Utils.formatPriceSymbol(disPrice));
                productPriceInt = disPrice;
            }

        binding.productName.setText(cartItem.getName());
        binding.shopName.setText(String.format("Seller: %s", shopItem.getShopName()));
        binding.price.setText(String.format("%s x 1", Utils.formatPriceSymbol(productPriceInt)));
        binding.quantity.setText("1");
        binding.priceTotal.setText(Utils.formatPriceSymbol(productPriceInt));

        Glide.with(this)
                .load(cartItem.getImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(250, 250))
                .into(binding.productImage);

        binding.variationHolder.setVisibility(View.GONE);

        binding.addToCart.setOnClickListener(v -> {
            CartEntity cartEntity = getCartItem();
            Executors.newSingleThreadExecutor().execute(() -> {
                List<CartEntity> dbItem = cartDao.checkExistsEntity(cartEntity.getProductID());
                if (dbItem.size() == 0)
                    cartDao.insert(cartEntity);
                else
                    cartDao.updateQuantity(cartEntity.getProductID(), dbItem.get(0).getQuantity() + quantityCount);
            });
            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        binding.buyNow.setOnClickListener(view1 -> {
            if (CredentialManager.getToken().equals("")) {
                startActivity(new Intent(context, SignInActivity.class));
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
        String price = Utils.formatPrice(shopItem.getPrice());

        if (shopItem.getDiscountedPrice() != null)
            if (shopItem.getDiscountedPrice() > 0)
                price = Utils.formatPrice(shopItem.getDiscountedPrice());

        String sellerJson = new Gson().toJson(shopItem);

        CartEntity cartEntity = new CartEntity();
        cartEntity.setName(cartItem.getName());
        cartEntity.setImage(cartItem.getImage());
        cartEntity.setPriceRound(price);
        cartEntity.setTime(calendar.getTimeInMillis());
        cartEntity.setShopJson(sellerJson);
        cartEntity.setQuantity(quantity);
        cartEntity.setShopSlug(shopItem.getShopSlug());
        cartEntity.setShopName(shopItem.getShopName());
        cartEntity.setSlug(cartItem.getImage());
        cartEntity.setVariantDetails(cartItem.getVariantDetails());
        cartEntity.setProductID(String.valueOf(shopItem.getShopItemId()));

        return cartEntity;
    }

    @SuppressLint("DefaultLocale")
    private void inflateQuantity() {
        binding.quantity.setText(String.format("%d", quantityCount));
        binding.price.setText(String.format("%s x %d", Utils.formatPriceSymbol(productPriceInt), quantityCount));
        binding.priceTotal.setText(Utils.formatPriceSymbol(productPriceInt * quantityCount));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        context = view.getContext();

        if (getActivity() instanceof MainActivity)
            navController = NavHostFragment.findNavController(this);

        skeleton = Skeleton.bind((LinearLayout) view.findViewById(R.id.linearLayout))
                .load(R.layout.skeleton_buy_now_modal)
                .color(R.color.ddd)
                .shimmer(true).show();

        itemsList = new ArrayList<>();
        adapterVariation = new VariationAdapter(itemsList, context, this);

        LinearLayoutManager managerVariation = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerViewVariation.setLayoutManager(managerVariation);
        binding.recyclerViewVariation.setAdapter(adapterVariation);

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


        if (shopItem == null) {
            skeleton.show();
            getProductDetails();
        } else
            inflateFromModel();

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

    public void getProductDetails() {

        ProductApiHelper.getProductVariants(shop_slug, shop_item_slug, new ResponseListenerAuth<CommonDataResponse<List<ShopItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ShopItem>> response, int statusCode) {

                skeleton.hide();

                itemsList.clear();
                itemsList.addAll(response.getData());
                adapterVariation.notifyDataSetChanged();

                if (itemsList.size() > 0) {
                    itemsList.get(0).setSelected(true);
                    loadProductById(0);
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    private void loadProductById(int position) {

        ShopItem firstItem = itemsList.get(position);

        try {
            productPriceInt = (int) Math.round(Double.parseDouble(firstItem.getShopItemPrice()));
        } catch (Exception ignored) {
        }


        if (firstItem.getShopItemDiscountedPrice() != null)
            if (!(firstItem.getShopItemDiscountedPrice().equals("0.0") || firstItem.getShopItemDiscountedPrice().equals("0"))) {
                int disPrice = (int) Math.round(Double.parseDouble(firstItem.getShopItemDiscountedPrice()));
                binding.price.setText(String.format("%s x 1", Utils.formatPriceSymbol(disPrice)));
                binding.priceTotal.setText(Utils.formatPriceSymbol(disPrice));
                productPriceInt = disPrice;
            }

        binding.productName.setText(firstItem.getShopItemName());
        binding.shopName.setText(String.format("Seller: %s", firstItem.getShopName()));
        binding.price.setText(String.format("%s x 1", Utils.formatPriceSymbol(productPriceInt)));
        binding.priceTotal.setText(Utils.formatPriceSymbol(productPriceInt));
        binding.quantity.setText("1");
        binding.priceTotal.setText(Utils.formatPriceSymbol(productPriceInt));

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

        binding.addToCart.setOnClickListener(v -> {
            CartEntity cartEntity = getCartEntity(firstItem);
            Executors.newSingleThreadExecutor().execute(() -> {
                List<CartEntity> dbItem = cartDao.checkExistsEntity(cartEntity.getProductID());
                if (dbItem.size() == 0)
                    cartDao.insert(cartEntity);
                else
                    cartDao.updateQuantity(cartEntity.getProductID(), dbItem.get(0).getQuantity() + quantityCount);
            });
            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
            dismiss();
        });


        binding.buyNow.setOnClickListener(view1 -> {
            if (CredentialManager.getToken().equals("")) {
                startActivity(new Intent(context, SignInActivity.class));
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
        Calendar calendar = Calendar.getInstance();
        String price = firstItem.getShopItemPrice();

        if (firstItem.getShopItemDiscountedPrice() != null)
            if (!(firstItem.getShopItemDiscountedPrice().trim().equals("0") || firstItem.getShopItemDiscountedPrice().trim().equals("0.0")))
                price = firstItem.getShopItemDiscountedPrice();

        String sellerJson = new Gson().toJson(firstItem);

        CartEntity cartEntity = new CartEntity();
        cartEntity.setName(firstItem.getShopItemName());
        cartEntity.setImage(firstItem.getShopItemImage());
        cartEntity.setPriceRound(price);
        cartEntity.setTime(calendar.getTimeInMillis());
        cartEntity.setShopJson(sellerJson);
        cartEntity.setQuantity(quantityCount);
        cartEntity.setShopSlug(firstItem.getShopSlug());
        cartEntity.setShopName(firstItem.getShopName());
        cartEntity.setSlug(shop_item_slug);
        cartEntity.setProductID(String.valueOf(firstItem.getShopItemId()));

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


    private void dismissDialog() {
        if (getActivity() != null) {
            if (isVisible() && isCancelable() && !getActivity().isDestroyed() && !getActivity().isFinishing())
                dismissAllowingStateLoss();
        }
    }

}
