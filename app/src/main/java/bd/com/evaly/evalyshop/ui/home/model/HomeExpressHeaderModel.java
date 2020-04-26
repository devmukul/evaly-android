package bd.com.evaly.evalyshop.ui.home.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.databinding.HomeModelExpressHeaderBinding;
import bd.com.evaly.evalyshop.ui.basic.TextBottomSheetFragment;

@EpoxyModelClass(layout = R.layout.home_model_express_header)
public abstract class HomeExpressHeaderModel extends EpoxyModelWithHolder<HomeExpressHeaderModel.HomeExpressHolder> {

    @EpoxyAttribute
    public AppCompatActivity activity;
    @EpoxyAttribute
    public Fragment fragment;
    @EpoxyAttribute
    AppDatabase appDatabase;

    @Override
    public void bind(@NonNull HomeExpressHolder holder) {
        super.bind(holder);
    }

    @Override
    public void unbind(@NonNull HomeExpressHolder holder) {
        super.unbind(holder);
        holder.itemView = null;
    }

    class HomeExpressHolder extends EpoxyHolder {
        View itemView;

        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;
            HomeModelExpressHeaderBinding binding = HomeModelExpressHeaderBinding.bind(itemView);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(true);

            binding.help.setOnClickListener(v -> {
                String text = "Evaly Express is a special service for daily needs products. With extremely fast delivery system, you will get your ordered items within 48 hours. <br><br>Currently this service is available for:<br><br>  • <b>Grocery</b><br>  • <b>Pharmacy</b><br>  • <b>Foods</b>";
                TextBottomSheetFragment fragment = TextBottomSheetFragment.newInstance(text);
                fragment.show(activity.getSupportFragmentManager(), "terms");
            });

        }
    }

}
