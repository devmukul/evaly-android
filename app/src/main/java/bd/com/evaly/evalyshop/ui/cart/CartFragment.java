package bd.com.evaly.evalyshop.ui.cart;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.FragmentCartBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.cart.controller.CartController;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.smoothCheckBox.SmoothCheckBox;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CartFragment extends Fragment implements CartController.CartClickListener {

    @Inject
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FragmentCartBinding binding;
    private LinearLayoutManager manager;
    private CartController controller;
    private CartViewModel viewModel;
    private NavController navController;

    private SmoothCheckBox.OnCheckedChangeListener allCheckedListener = (compoundButton, isChecked) -> {
        viewModel.selectAll(isChecked);
    };

    public CartFragment() {

    }

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof MainActivity)
            navController = NavHostFragment.findNavController(this);
        setupToolbar();
        setupRecycler();
        liveEvents();
        clickListeners();
    }

    private void clickListeners() {
        binding.checkoutBtn.setOnClickListener(v -> {
            checkoutClicked();
        });
        binding.checkBox.setOnCheckedChangeListener(allCheckedListener);
    }

    private void liveEvents() {
        viewModel.liveList.observe(getViewLifecycleOwner(), cartEntities -> {
            controller.setList(cartEntities);
            controller.requestModelBuild();
            boolean allSelected = cartEntities.size() > 0;
            double totalPrice = 0;
            for (CartEntity entity : cartEntities) {
                if (!entity.isSelected()) {
                    allSelected = false;
                } else
                    totalPrice += entity.getPriceDouble() * entity.getQuantity();
            }
            binding.checkBox.setOnCheckedChangeListener(null);
            binding.checkBox.setChecked(allSelected);
            binding.checkBox.setOnCheckedChangeListener(allCheckedListener);
            binding.totalPrice.setText(Utils.formatPriceSymbol(totalPrice));
        });
    }

    private void setupRecycler() {
        if (controller == null)
            controller = new CartController();
        controller.setCartClickListener(this);
        controller.setViewModel(viewModel);
        manager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(controller.getAdapter());
    }

    private void checkoutClicked() {
        if (CredentialManager.getToken().equals("")) {
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
            if (count > 0)
                navController.navigate(R.id.checkoutFragment);
            else
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
                    if (viewModel.liveList.getValue().size() == 0) {
                        ToastUtils.show("No item is available in cart to delete");
                    } else {
                        new AlertDialog.Builder(getContext())
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
