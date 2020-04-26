package bd.com.evaly.evalyshop.ui.epoxy;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import bd.com.evaly.evalyshop.R;

@EpoxyModelClass(layout = R.layout.divider_view)
public abstract class EpoxyDividerModel extends EpoxyModelWithHolder<EpoxyDividerModel.Holder> {

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
