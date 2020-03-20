package bd.com.evaly.evalyshop.epoxy.models;

import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.epoxy.base.BaseEpoxyHolder;
import butterknife.BindView;

@EpoxyModelClass(layout = R.layout.epoxy_header_item2)
public abstract class Header2Model extends EpoxyModelWithHolder<Header2Model.Holder> {
    @EpoxyAttribute
    String title;

    @Override
    public void bind(Holder holder) {
        holder.header.setText(title);
    }

    static class Holder extends BaseEpoxyHolder {
        @BindView(R.id.text)
        TextView header;
    }
}
