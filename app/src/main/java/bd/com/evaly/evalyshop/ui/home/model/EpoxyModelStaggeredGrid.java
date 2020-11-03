package bd.com.evaly.evalyshop.ui.home.model;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyModel;


public abstract class EpoxyModelStaggeredGrid<T extends View> extends EpoxyModel<T> {
    @Override
    public void bind(@NonNull T view) {
        super.bind(view);
        StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setFullSpan(true);
        view.setLayoutParams(params);
    }
}