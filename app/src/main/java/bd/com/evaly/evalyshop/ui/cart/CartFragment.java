package bd.com.evaly.evalyshop.ui.cart;


import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.FragmentCartBinding;
import bd.com.evaly.evalyshop.ui.auth.login.SignInActivity;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.cart.controller.CartController;
import bd.com.evaly.evalyshop.ui.checkout.CheckoutFragment;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.smoothCheckBox.SmoothCheckBox;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CartFragment extends BaseFragment<FragmentCartBinding, CartViewModel> implements CartController.CartClickListener {

    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    private LinearLayoutManager manager;
    private CartController controller;

    private SmoothCheckBox.OnCheckedChangeListener allCheckedListener = (compoundButton, isChecked) -> {
        viewModel.selectAll(isChecked);
    };

    public CartFragment() {
        super(CartViewModel.class, R.layout.fragment_cart);
    }

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    protected void initViews() {
        setupToolbar();
    }

    @Override
    protected void clickListeners() {
        binding.checkoutBtn.setOnClickListener(v -> {
            checkoutClicked();
        });
        binding.checkBox.setOnCheckedChangeListener(allCheckedListener);
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.liveList.observe(getViewLifecycleOwner(), cartEntities -> {
            controller.setList(cartEntities);
            controller.requestModelBuild();
            boolean allSelected = cartEntities.size() > 0;
            double totalPrice = 0;
            for (CartEntity entity : cartEntities) {
                if (!entity.isSelected()) {
                    allSelected = false;
                } else
                    totalPrice += entity.getDiscountedPriceD() * entity.getQuantity();
            }
            binding.checkBox.setOnCheckedChangeListener(null);
            binding.checkBox.setChecked(allSelected);
            binding.checkBox.setOnCheckedChangeListener(allCheckedListener);
            binding.totalPrice.setText(Utils.formatPriceSymbol(totalPrice));
        });
    }

    @Override
    protected void setupRecycler() {
        if (controller == null)
            controller = new CartController();
        controller.setCartClickListener(this);
        controller.setViewModel(viewModel);
        manager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(controller.getAdapter());
    }

    private void checkoutClicked() {
        if (preferenceRepository.getToken().equals("")) {
            ToastUtils.show("You need to login first.");
            startActivity(new Intent(requireActivity(), SignInActivity.class));
        } else if (viewModel.liveList.getValue() == null || viewModel.liveList.getValue().size() == 0) {
            ToastUtils.show("No product is added to the cart");
        } else {
            int count = 0;
            List<CartEntity> list = viewModel.liveList.getValue();
            for (CartEntity item : list) {
                if (item.isSelected())
                    count++;
            }
            if (count > 0) {
                if (getActivity() instanceof MainActivity)
                    navController.navigate(R.id.checkoutFragment);
                else {
                    CheckoutFragment fragment = new CheckoutFragment();
                    fragment.show(getParentFragmentManager(), "cart");
                }
            } else
                ToastUtils.show("Please select items before checkout");
        }
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });
        binding.toolbar.inflateMenu(R.menu.delete_btn);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case android.R.id.home:
                    return true;
                case R.id.action_delete:
                    if (viewModel.getItemCount() == 0)
                        ToastUtils.show("No item is available in cart to delete");
                    else if (viewModel.getSelectedItemCount() == 0)
                        ToastUtils.show("Please select item to delete");
                    else {
                        new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme)
                                .setMessage("Are you sure you want to delete the selected products from the cart?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                                    viewModel.deleteSelected();
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onClick(String productName, String productSlug) {
        Intent intent = new Intent(getContext(), ViewProductActivity.class);
        intent.putExtra("product_name", productName);
        intent.putExtra("slug", productSlug);
        intent.putExtra("product_slug", productSlug);
        getContext().startActivity(intent);
    }

    @Override
    public void onShopClick(String shopName, String shopSlug) {
        Bundle bundle = new Bundle();
        bundle.putString("shop_name", shopName);
        bundle.putString("shop_slug", shopSlug);
        navController.navigate(R.id.shopFragment, bundle);
    }

}
