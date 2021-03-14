package bd.com.evaly.evalyshop.ui.home.model.topProducts;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import bd.com.evaly.evalyshop.R;

@EpoxyModelClass(layout = R.layout.divider_view_white)
public abstract class TopProductsDividerModel extends EpoxyModelWithHolder<TopProductsDividerModel.Holder> {

    @Override
    public void bind(@NonNull Holder holder) {
        super.bind(holder);
    }

    @Override
    public void unbind(@NonNull Holder holder) {
        super.unbind(holder);
        holder.itemView = null;
    }

    class Holder extends EpoxyHolder {
        View itemView;

        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams();
            params.setFullSpan(true);
        }
    }

}
