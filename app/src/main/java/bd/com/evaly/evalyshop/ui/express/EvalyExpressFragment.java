package bd.com.evaly.evalyshop.ui.express;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.FragmentEvalyExpressBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.shop.GroupShopModel;
import bd.com.evaly.evalyshop.ui.basic.TextBottomSheetFragment;
import bd.com.evaly.evalyshop.util.LocationUtils;
import bd.com.evaly.evalyshop.util.Utils;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getMainExecutor;

public class EvalyExpressFragment extends Fragment {

    private FragmentEvalyExpressBinding binding;
    private EvalyExpressAdapter adapter;
    private List<GroupShopModel> itemList = new ArrayList<>();
    private EvalyExpressViewModelFactory factory;
    private EvalyExpressViewModel viewModel;
    private boolean written = false;
    TextWatcher textWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!written)
                return;

            viewModel.clear();
            itemList.clear();

            if (binding.search.getText().toString().trim().equals(""))
                viewModel.setShopSearch(null);
            else
                viewModel.setShopSearch(binding.search.getText().toString().trim());
            viewModel.loadShops();
        }
    };
    private boolean checkNearest = false;
    private String serviceSlug;
    private int visibleItemCount, totalItemCount, pastVisibleItems;
    private ExpressServiceModel model;

    public EvalyExpressFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEvalyExpressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (getArguments() == null)
            return;
        if (getArguments().containsKey("model")) {
            model = (ExpressServiceModel) getArguments().getSerializable("model");
            if (model == null)
                return;
        } else if (getArguments().containsKey("slug")) {
            model = new ExpressServiceModel();
            model.setSlug(getArguments().getString("slug"));
            model.setName(Utils.capitalize(getArguments().getString("slug").replace("-", " ")));
        } else {
            Toast.makeText(getActivity(), "This page is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        serviceSlug = model.getSlug();

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolbar.setTitle(model.getName());
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });

        factory = new EvalyExpressViewModelFactory(serviceSlug);
        viewModel = new ViewModelProvider(this, factory).get(EvalyExpressViewModel.class);

        adapter = new EvalyExpressAdapter(getContext(), itemList, NavHostFragment.findNavController(this));

        written = false;

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
        binding.progressBar.setVisibility(View.VISIBLE);

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (!viewModel.isLoading() && viewModel.isHasNext())
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            if (viewModel.getCurrentPage() > 1)
                                binding.progressBarBottom.setVisibility(View.VISIBLE);
                            else
                                binding.progressBarBottom.setVisibility(View.INVISIBLE);
                            viewModel.loadShops();
                        }
                }
            }
        });

        viewModel.getLiveData().observe(getViewLifecycleOwner(), list -> {

            if (list == null)
                return;

            adapter.addData(list);

            binding.progressBar.setVisibility(View.GONE);
            binding.progressBarBottom.setVisibility(View.INVISIBLE);

            if (list.size() == 0) {
                binding.layoutNot.setVisibility(View.VISIBLE);
            } else {
                binding.layoutNot.setVisibility(View.GONE);
            }

        });

        viewModel.getExpressDetails().observe(getViewLifecycleOwner(), expressServiceDetailsModel -> {

            if (!expressServiceDetailsModel.getName().equals(model.getName()))
                binding.toolbar.setTitle(expressServiceDetailsModel.getName());

            binding.btnTerms.setOnClickListener(v -> {
                TextBottomSheetFragment fragment = TextBottomSheetFragment.newInstance(expressServiceDetailsModel.getDescription().replaceAll("\n", "<br>"));
                fragment.show(getParentFragmentManager(), "terms");
            });
            Glide.with(binding.getRoot())
                    .load(expressServiceDetailsModel.getBannerImage())
                    .placeholder(R.drawable.banner_skeleton_bg)
                    .into(binding.sliderImage);

        });

        binding.districtSelector.setOnClickListener(v -> showLocationSelector());
        binding.districtName.setText(CredentialManager.getArea() == null ? "All District" : CredentialManager.getArea());

        binding.search.addTextChangedListener(textWatcher);

        binding.search.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) written = true;
        });

        if (CredentialManager.getArea() == null)
            checkPermissionAndLoad();

    }

    private void checkPermissionAndLoad() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            checkMyLocation();
        else
            requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1212);
    }

    private void checkMyLocation() {

        LocationUtils locationUtils = new LocationUtils();
        locationUtils.getLocation(getContext(), new LocationUtils.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                if (getContext() == null || location == null)
                    return;
                if (location.getLatitude() == 0 || location.getLongitude() == 0)
                    return;

                CredentialManager.saveLongitude(String.valueOf(location.getLongitude()));
                CredentialManager.saveLatitude(String.valueOf(location.getLatitude()));

                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                if (checkNearest) {
                    getMainExecutor(getContext()).execute(() -> {
                        CredentialManager.saveArea("Nearest");
                        binding.districtName.setText("Nearest");
                        viewModel.clear();
                        viewModel.loadShops();
                    });
                } else {
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        int c = 0;
                        if (addresses.size() > 0) {
                            Address obj = addresses.get(0);
                            String myAddress = obj.toString();
                            String[] districts = AppController.getmContext().getResources().getStringArray(R.array.districtsList);
                            for (String district : districts) {
                                if (myAddress.contains(district)) {
                                    getMainExecutor(getContext()).execute(() -> {
                                        CredentialManager.saveArea(district);
                                        binding.districtName.setText(district);
                                        viewModel.loadShops();
                                    });
                                    c++;
                                    break;
                                }
                            }
                        }
                        if (c == 0)
                            getMainExecutor(getContext()).execute(() -> {
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), "Could't find your district with GPS, please select manually", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                }
                            });
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1212:
                if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    checkMyLocation();

                } else {
                    CredentialManager.saveArea("All Districts");
                    Toast.makeText(getContext(), "Location permission denied, please select your district manually", Toast.LENGTH_LONG).show();
                }
                break;
            case 0:
                break;
        }
    }

    private void showLocationSelector() {


        if (getContext() == null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final String[] districts = {"Nearest Shops", "Select District"};
        builder.setItems(districts, (dialog, which) -> {
            if (districts[which].toLowerCase().contains("nearest")) {
                checkNearest = true;
                checkPermissionAndLoad();
                dialog.dismiss();

            } else {
                dialog.dismiss();
                showDistrictSelector();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void showDistrictSelector() {

        if (getContext() == null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose your district");

        final String[] districts = AppController.getmContext().getResources().getStringArray(R.array.districtsList);
        builder.setItems(districts, (dialog, which) -> {

            binding.progressBar.setVisibility(View.VISIBLE);
            binding.layoutNot.setVisibility(View.GONE);

            viewModel.clear();
            itemList.clear();

            if (adapter != null)
                adapter.clear();

            if (districts[which].toLowerCase().contains("auto")) {
                checkNearest = false;
                checkPermissionAndLoad();
            } else if (districts[which].toLowerCase().contains("nearest")) {
                checkNearest = true;
                checkPermissionAndLoad();
            } else {
                CredentialManager.saveArea(districts[which]);
                viewModel.loadShops();
                binding.districtName.setText(districts[which]);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
