package bd.com.evaly.evalyshop.epoxy.models;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import bd.com.evaly.evalyshop.R;
import butterknife.BindView;
import butterknife.ButterKnife;

@EpoxyModelClass(layout = R.layout.epoxy_header_item)
public abstract class HeaderModel extends EpoxyModelWithHolder<HeaderModel.Holder> {
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
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams();
            params.setFullSpan(true);

            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }
}