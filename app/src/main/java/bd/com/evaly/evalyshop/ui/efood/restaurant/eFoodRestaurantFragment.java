package bd.com.evaly.evalyshop.ui.efood.restaurant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import bd.com.evaly.evalyshop.databinding.EfoodFragmentRestaurantBinding;
import bd.com.evaly.evalyshop.ui.efood.restaurant.controller.eFoodRestaurantController;

public class eFoodRestaurantFragment extends Fragment {

    private EfoodFragmentRestaurantBinding binding;
    private eFoodRestaurantController controller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EfoodFragmentRestaurantBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = new eFoodRestaurantController();
        binding.rvProducts.setAdapter(controller.getAdapter());

        controller.requestModelBuild();

    }
}
