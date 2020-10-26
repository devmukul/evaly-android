package bd.com.evaly.evalyshop.ui.home.model;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.databinding.HomeModelFlashsaleHeaderBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.home_model_flashsale_header)
public abstract class HomeExpressHeaderModel extends EpoxyModelWithHolder<HomeExpressHeaderModel.HomeExpressHolder> {

    @EpoxyAttribute
    public AppCompatActivity activity;
    @EpoxyAttribute
    public Fragment fragment;
    @EpoxyAttribute
    AppDatabase appDatabase;
    @EpoxyAttribute
    String title;
    @EpoxyAttribute
    boolean showMore;
    @EpoxyAttribute
    boolean transparentBackground;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull HomeExpressHolder holder) {
        super.bind(holder);
    }

    @Override
    public void unbind(@NonNull HomeExpressHolder holder) {
        super.unbind(holder);
        holder.itemView = null;
    }

    public class HomeExpressHolder extends EpoxyHolder {
        View itemView;

        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;
            HomeModelFlashsaleHeaderBinding binding = HomeModelFlashsaleHeaderBinding.bind(itemView);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(true);
            if (title != null)
                binding.title.setText(title);
            if (showMore)
                binding.help.setVisibility(View.VISIBLE);
            else
                binding.help.setVisibility(View.GONE);

            if (transparentBackground)
                binding.container.setBackgroundColor(Color.TRANSPARENT);
            else
                binding.container.setBackgroundColor(
                        ContextCompat.getColor(AppController.getmContext(), R.color.white));

            binding.help.setOnClickListener(clickListener);
        }
    }

}
