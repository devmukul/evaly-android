package bd.com.evaly.evalyshop.ui.express;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.FragmentEvalyExpressBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.shop.GroupShopModel;

public class EvalyExpressFragment extends Fragment {

    private FragmentEvalyExpressBinding binding;
    private EvalyExpressAdapter adapter;
    private List<GroupShopModel> itemList = new ArrayList<>();
    private EvalyExpressViewModel viewModel;
    private boolean written = false;

    TextWatcher textWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {

        }

        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {


        }

        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {

            if (!written)
                return;

            written = true;
            viewModel.clear();
            itemList.clear();

            if (binding.search.getText().toString().trim().equals(""))
                viewModel.setSearch(null);
            else
                viewModel.setSearch(binding.search.getText().toString().trim());
            viewModel.loadShops();
        }
    };
    private int visibleItemCount, totalItemCount, pastVisibleItems;


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


        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });

        viewModel = new ViewModelProvider(this).get(EvalyExpressViewModel.class);

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


        binding.districtSelector.setOnClickListener(v -> showDistrictSelector());

        binding.districtName.setText(CredentialManager.getArea() == null ? "Dhaka" : CredentialManager.getArea());

        binding.search.addTextChangedListener(textWatcher);


        binding.search.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                written = true;
        });

    }

    private void showDistrictSelector() {

        if (getContext() == null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose your district");

        final String[] districts = AppController.getmContext().getResources().getStringArray(R.array.districtsList);
        builder.setItems(districts, (dialog, which) -> {
            CredentialManager.saveArea(districts[which]);
            itemList.clear();
            viewModel.clear();
            viewModel.loadShops();
            binding.districtName.setText(districts[which]);
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
