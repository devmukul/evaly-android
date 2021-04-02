package bd.com.evaly.evalyshop.ui.epoxy;

import android.graphics.Color;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.bumptech.glide.Glide;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.util.Utils;


public class NoItemModel extends EpoxyModel<ConstraintLayout> {

    @EpoxyAttribute
    int image;

    @EpoxyAttribute
    String text;

    @EpoxyAttribute
    int width;

    @EpoxyAttribute
    String imageTint = null;

    public NoItemModel() {
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.epoxy_no_item_model;
    }

    @Override
    public void bind(@NonNull ConstraintLayout view) {
        super.bind(view);

        if (view.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            params.setFullSpan(true);
        }

        TextView textView = view.findViewById(R.id.text);
        ImageView imageView = view.findViewById(R.id.image);

        if (imageTint != null)
            imageView.setColorFilter(Color.parseColor(imageTint));

        Glide.with(view)
                .asDrawable()
                .load(image)
                .into(imageView);
        textView.setText(Html.fromHtml(text));

        if (width > 0) {
            imageView.getLayoutParams().width = (int) Utils.convertDpToPixel((float) width, imageView.getContext());
        }

    }
}
