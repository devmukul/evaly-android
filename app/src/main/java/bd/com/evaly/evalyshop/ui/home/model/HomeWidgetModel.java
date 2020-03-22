package bd.com.evaly.evalyshop.ui.home.model;

import android.content.Intent;

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
import bd.com.evaly.evalyshop.databinding.HomeModelQuickAccessBinding;
import bd.com.evaly.evalyshop.ui.giftcard.GiftCardActivity;
import bd.com.evaly.evalyshop.ui.newsfeed.NewsfeedActivity;

@EpoxyModelClass(layout = R.layout.home_model_quick_access)
public abstract class HomeWidgetModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    public AppCompatActivity activity;
    @EpoxyAttribute
    public Fragment fragment;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        HomeModelQuickAccessBinding binding = (HomeModelQuickAccessBinding) holder.getDataBinding();
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);

        NavController navController = NavHostFragment.findNavController(fragment);

        binding.btn1Image.setOnClickListener(v -> activity.startActivity(new Intent(activity, NewsfeedActivity.class)));
        binding.btn2Image.setOnClickListener(v -> {
            Intent ni = new Intent(activity, GiftCardActivity.class);
            activity.startActivity(ni);
        });

        binding.btn3Image.setOnClickListener(v -> navController.navigate(R.id.campaignFragment));

        binding.btn4Image.setOnClickListener(v -> navController.navigate(R.id.evalyExpressFragment));
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}
