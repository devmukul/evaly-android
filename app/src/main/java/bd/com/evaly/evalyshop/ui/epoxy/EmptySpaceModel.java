package bd.com.evaly.evalyshop.ui.epoxy;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.util.Utils;


public class EmptySpaceModel extends EpoxyModel<LinearLayout> {

    @EpoxyAttribute
    int height;

    public EmptySpaceModel() {
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.epoxy_empty_space;
    }

    @Override
    public void bind(@NonNull LinearLayout view) {
        super.bind(view);
        if (view.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            params.setFullSpan(true);
        }
        view.setMinimumHeight((int) Utils.convertDpToPixel((float) height, view.getContext()));

    }
}
