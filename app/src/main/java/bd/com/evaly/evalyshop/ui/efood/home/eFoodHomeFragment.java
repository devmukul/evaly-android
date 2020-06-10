package bd.com.evaly.evalyshop.ui.efood.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import bd.com.evaly.evalyshop.databinding.EfoodHomepageBinding;
import bd.com.evaly.evalyshop.ui.efood.home.controller.eFoodHomeController;
import bd.com.evaly.evalyshop.ui.home.controller.HomeController;


public class eFoodHomeFragment extends Fragment {

    private EfoodHomepageBinding binding;
    private eFoodHomeController controller;

    public eFoodHomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EfoodHomepageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = new eFoodHomeController();

        binding.recyclerView.setAdapter(controller.getAdapter());

        controller.requestModelBuild();

    }
}