package bd.com.evaly.evalyshop.ui.express.products.model;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import bd.com.evaly.evalyshop.R;

@EpoxyModelClass(layout = R.layout.express_product_title)
public abstract class ExpressTitleModel extends EpoxyModelWithHolder<ExpressTitleModel.Holder> {
    @EpoxyAttribute
    public String title;

    @Override
    public void bind(Holder holder) {

        if (title != null && !title.equals(""))
            ((TextView)holder.itemView.findViewById(R.id.title)).setText(title);

    }

    static class Holder extends EpoxyHolder {

        View itemView;

        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;

            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams();
            params.setFullSpan(true);
        }
    }

}
