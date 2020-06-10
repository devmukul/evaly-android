package bd.com.evaly.evalyshop.ui.efood.restaurant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import bd.com.evaly.evalyshop.databinding.EfoodFragmentRestaurantBinding;

public class eFoodRestaurantFragment extends Fragment {

    private EfoodFragmentRestaurantBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EfoodFragmentRestaurantBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

}
