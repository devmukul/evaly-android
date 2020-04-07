package bd.com.evaly.evalyshop.ui.home.model;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.HomeModelExpressBinding;

@EpoxyModelClass(layout = R.layout.home_model_express)
public abstract class HomeExpressModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    public AppCompatActivity activity;
    @EpoxyAttribute
    public Fragment fragment;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        HomeModelExpressBinding binding = (HomeModelExpressBinding) holder.getDataBinding();
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);

        NavController navController = NavHostFragment.findNavController(fragment);

        binding.btnGrocery.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("slug", "grocery");
            navController.navigate(R.id.evalyExpressFragment, bundle);
        });

        binding.btnPharmacy.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("slug", "pharmacy");
            navController.navigate(R.id.evalyExpressFragment, bundle);
        });

        binding.btnFoods.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("slug", "foods");
            navController.navigate(R.id.evalyExpressFragment, bundle);
        });

    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}
