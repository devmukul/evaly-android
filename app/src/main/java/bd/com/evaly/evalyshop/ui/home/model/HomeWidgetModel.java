package bd.com.evaly.evalyshop.ui.home.model;

import android.content.Intent;
import android.view.View;

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
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.notification.NotificationCount;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;

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

        binding.btn1Image.setOnClickListener(v -> navController.navigate(R.id.newsfeedFragment));
        binding.btn2Image.setOnClickListener(v -> {
            navController.navigate(R.id.giftCardFragment);
        });

        binding.btn3Image.setOnClickListener(v -> navController.navigate(R.id.campaignFragment));

        binding.btn4Image.setOnClickListener(v -> {
            navController.navigate(R.id.categoryFragment);
        });

        binding.btn5Image.setOnClickListener(v -> {
            if (CredentialManager.getToken().equals(""))
                activity.startActivity(new Intent(activity, SignInActivity.class));
            else
                navController.navigate(R.id.orderListBaseFragment);
        });

        binding.btn1Indicator.setVisibility(View.GONE);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}
