package bd.com.evaly.evalyshop.ui.shop.quickView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import bd.com.evaly.evalyshop.databinding.FragmentShopQuickCategoryBinding;

public class ShopQuickViewFragment extends Fragment {

    private FragmentShopQuickCategoryBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentShopQuickCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

}
