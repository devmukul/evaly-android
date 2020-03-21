package bd.com.evaly.evalyshop.epoxy.models;

import android.view.View;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import bd.com.evaly.evalyshop.R;
import butterknife.BindView;
import butterknife.ButterKnife;

@EpoxyModelClass(layout = R.layout.epxy_grid_item)
public abstract class GridItemModel extends EpoxyModelWithHolder<GridItemModel.Holder> {
    @EpoxyAttribute
    String title;

    @Override
    public void bind(Holder holder) {
        holder.header.setText(title);
    }

    static class Holder extends EpoxyHolder {

        View itemView;
        @BindView(R.id.text)
        TextView header;

        @Override
        protected void bindView(View itemView) {
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }
}
