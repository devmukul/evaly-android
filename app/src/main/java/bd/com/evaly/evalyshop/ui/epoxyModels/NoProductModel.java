package bd.com.evaly.evalyshop.ui.epoxyModels;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.ModelProp;
import com.bumptech.glide.Glide;

import bd.com.evaly.evalyshop.R;

public class NoProductModel extends EpoxyModel<ConstraintLayout> {

    @ModelProp
    int image;

    @ModelProp
    String text;

    public NoProductModel() {
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.epoxy_no_product_model;
    }

    @Override
    public void bind(@NonNull ConstraintLayout view) {
        super.bind(view);

        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        params.setFullSpan(true);

        TextView textView = view.findViewById(R.id.text);
        ImageView imageView = view.findViewById(R.id.image);

        Glide.with(view)
                .load(image)
                .into(imageView);
        textView.setText(text);
    }
}
