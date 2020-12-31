package bd.com.evaly.evalyshop.ui.browseProduct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;

import bd.com.evaly.evalyshop.databinding.FragmentProductBrowseBinding;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProductBrowseFragment extends Fragment {

    private FragmentProductBrowseBinding binding;
    private BrowseProductViewModel viewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BrowseProductViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductBrowseBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTabs();
        clickListeners();
        liveEvents();
    }

    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.setSelectedType(tab.getText().toString());
                viewModel.loadFromApi();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void clickListeners() {

    }

    private void liveEvents() {


    }
}
