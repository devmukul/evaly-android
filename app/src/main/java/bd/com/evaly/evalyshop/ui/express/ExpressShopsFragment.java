package bd.com.evaly.evalyshop.ui.express;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.FragmentEvalyExpressBinding;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.basic.TextBottomSheetFragment;
import bd.com.evaly.evalyshop.ui.express.adapter.EvalyExpressAdapter;
import bd.com.evaly.evalyshop.ui.express.adapter.ExpressDistrictAdapter;
import bd.com.evaly.evalyshop.util.LocationUtils;
import bd.com.evaly.evalyshop.util.Utils;
import dagger.hilt.android.AndroidEntryPoint;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getMainExecutor;

@AndroidEntryPoint
public class ExpressShopsFragment extends BaseFragment<FragmentEvalyExpressBinding, ExpressShopsViewModel> {

    @Inject
    PreferenceRepository preferenceRepository;
    private EvalyExpressAdapter adapter;
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
    private GridLayoutManager layoutManager;

    public ExpressShopsFragment() {
        super(ExpressShopsViewModel.class, R.layout.fragment_evaly_express);
    }


    @Override
    protected void initViews() {

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
        binding.districtName.setText(preferenceRepository.getArea() == null ? "All District" : preferenceRepository.getArea());

        binding.search.addTextChangedListener(textWatcher);

        binding.search.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) written = true;
        });

        if (preferenceRepository.getArea() == null)
            checkPermissionAndLoad();

    }

    @Override
    protected void liveEventsObservers() {
        viewModel.getLiveData().observe(getViewLifecycleOwner(), list -> {

            if (list == null)
                return;

            adapter.submitList(list);

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
                TextBottomSheetFragment fragment = TextBottomSheetFragment.newInstance("Terms & Conditions", expressServiceDetailsModel.getDescription().replaceAll("\n", "<br>"));
                fragment.show(getParentFragmentManager(), "terms");
            });

        });
    }

    @Override
    protected void clickListeners() {
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });

        binding.districtSelector.setOnClickListener(v -> showLocationSelector());

    }


    @Override
    protected void setupRecycler() {
        adapter = new EvalyExpressAdapter(getContext(), NavHostFragment.findNavController(this));

        written = false;

        layoutManager = new GridLayoutManager(getContext(), 2);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setHasFixedSize(false);
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
                if (getActivity() == null || getActivity().isFinishing() || getActivity().isDestroyed())
                    return;
                if (getContext() == null || location == null)
                    return;
                if (location.getLatitude() == 0 || location.getLongitude() == 0)
                    return;

                preferenceRepository.saveLongitude(String.valueOf(location.getLongitude()));
                preferenceRepository.saveLatitude(String.valueOf(location.getLatitude()));

                if (getContext() == null)
                    return;
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                if (checkNearest) {
                    getMainExecutor(getContext()).execute(() -> {
                        preferenceRepository.saveArea("Nearby");
                        binding.districtName.setText("Nearby");
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
                            String[] districts = AppController.getContext().getResources().getStringArray(R.array.districtsList);
                            for (String district : districts) {
                                if (myAddress.contains(district)) {
                                    getMainExecutor(getContext()).execute(() -> {
                                        preferenceRepository.saveArea(district);
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
                    preferenceRepository.saveArea("All Districts");
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
        Dialog dialog = new Dialog(getActivity(), R.style.TransparentDialog);
        dialog.setContentView(R.layout.dialog_express_location_filter);

        RecyclerView districtRecyclerView = dialog.findViewById(R.id.recyclerView);

        String[] districtList = AppController.getContext().getResources().getStringArray(R.array.districtsList);
        ArrayList<String> districts = new ArrayList<>(Arrays.asList(districtList));

        ExpressDistrictAdapter adapter = new ExpressDistrictAdapter(districts, object -> {
            hideAndClear();

            preferenceRepository.saveArea(object);
            viewModel.loadShops();
            binding.districtName.setText(object);
            dialog.dismiss();
            binding.search.setText("");
        });

        final TextView notFound = dialog.findViewById(R.id.not);

        EditText search = dialog.findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EditText search = dialog.findViewById(R.id.search);

                ArrayList<String> temp = new ArrayList<>();

                for (int i = 0; i < districtList.length; i++) {
                    if (districtList[i].toLowerCase().contains(search.getText().toString().toLowerCase()))
                        temp.add(districtList[i]);
                }

                adapter.setFilter(temp);

                if (temp.size() == 0)
                    notFound.setVisibility(View.VISIBLE);
                else
                    notFound.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        districtRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        LinearLayout nearby = dialog.findViewById(R.id.nearby);
        nearby.setOnClickListener(v -> {

            hideAndClear();

            binding.search.setText("");
            checkNearest = true;
            checkPermissionAndLoad();
            dialog.dismiss();
        });

        dialog.show();

    }

    private void hideAndClear() {

        if (adapter != null)
            adapter.clear();

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutNot.setVisibility(View.GONE);

        viewModel.clear();
    }


}
