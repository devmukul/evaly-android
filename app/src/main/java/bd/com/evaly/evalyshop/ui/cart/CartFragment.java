package bd.com.evaly.evalyshop.ui.cart;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.FragmentCartBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.cart.controller.CartController;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CartFragment extends Fragment implements CartController.CartClickListener {

    @Inject
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FragmentCartBinding binding;
    private LinearLayoutManager manager;
    private Context context;
    private ViewDialog dialog;
    private CompoundButton.OnCheckedChangeListener selectAllListener;
    private AppDatabase appDatabase;
    private CartDao cartDao;
    private int paymentMethod = 2;
    private double totalPriceDouble = 0;
    private NavController navController;
    private String deliveryChargeText = null;
    private String deliveryChargeApplicable = null;
    private CartController controller;
    private CartViewModel viewModel;

    public CartFragment() {
        // Required empty public constructor
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
        context = getContext();
        dialog = new ViewDialog(getActivity());
        appDatabase = AppDatabase.getInstance(getContext());
        cartDao = appDatabase.cartDao();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupRecycler();
        liveEvents();


        binding.checkoutBtn.setOnClickListener(v -> {
            checkoutClicked();
        });

        selectAllListener = (buttonView, isChecked) -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                List<CartEntity> listAdapter = cartDao.getAll();
                for (int i = 0; i < listAdapter.size(); i++) {
                    cartDao.markSelected(listAdapter.get(i).getProductID(), isChecked);
                }
            });
        };

        binding.checkBox.setOnCheckedChangeListener(selectAllListener);
    }

    private void liveEvents() {
        viewModel.liveList.observe(getViewLifecycleOwner(), cartEntities -> {
            controller.setList(cartEntities);
            controller.requestModelBuild();
        });
    }

    private void setupRecycler() {
        if (controller == null)
            controller = new CartController();
        controller.setCartClickListener(this);
        controller.setViewModel(viewModel);
        manager = new LinearLayoutManager(context);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(controller.getAdapter());
    }

    private void checkoutClicked() {

        if (CredentialManager.getToken().equals("")) {
            Toast.makeText(context, "You need to login first.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireActivity(), SignInActivity.class));
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
                        Toast.makeText(context, "No item is available in cart to delete", Toast.LENGTH_SHORT).show();
                    } else {
                        new AlertDialog.Builder(context)
                                .setMessage("Are you sure you want to delete the selected products from the cart?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {

                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        List<CartEntity> listAdapter = viewModel.liveList.getValue();
                                        for (int i = 0; i < listAdapter.size(); i++) {
                                            if (listAdapter.get(i).isSelected())
                                                cartDao.deleteBySlug(listAdapter.get(i).getProductID());
                                        }
                                    });

                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                    return true;
            }

            return false;
        });
    }


    public void uncheckSelectAllBtn(boolean isChecked) {

        if (!isChecked) {
            binding.checkBox.setOnCheckedChangeListener(null);
            binding.checkBox.setChecked(false);
            binding.checkBox.setOnCheckedChangeListener(selectAllListener);
        } else {

            boolean isAllSelected = true;

            List<CartEntity> list = viewModel.liveList.getValue();
            for (int i = 0; i < list.size(); i++) {
                if (!list.get(i).isSelected()) {
                    isAllSelected = false;
                    break;
                }
            }
            binding.checkBox.setOnCheckedChangeListener(null);
            binding.checkBox.setChecked(isAllSelected);
            binding.checkBox.setOnCheckedChangeListener(selectAllListener);
        }
    }

    @Override
    public void onClick(String productName, String productSlug) {
        Intent intent = new Intent(context, ViewProductActivity.class);
        intent.putExtra("product_name", productName);
        intent.putExtra("product_slug", productSlug);
        context.startActivity(intent);
    }

    @Override
    public void onShopClick(String shopName, String shopSlug) {
        Bundle bundle = new Bundle();
        bundle.putString("shop_name", shopName);
        bundle.putString("shop_slug", shopSlug);
        navController.navigate(R.id.shopFragment, bundle);
    }

}
