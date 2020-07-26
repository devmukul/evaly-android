package bd.com.evaly.evalyshop.ui.basic.textBottomSheet;

import android.view.View;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemTextListBinding;

@EpoxyModelClass(layout = R.layout.item_text_list)
public abstract class TextListModel extends EpoxyModelWithHolder<TextListModel.Holder> {

    @EpoxyAttribute
    String text;

    @Override
    public void bind(@NonNull TextListModel.Holder holder) {
        super.bind(holder);
        holder.binding.text.setText(text);
    }

    class Holder extends EpoxyHolder {
        ItemTextListBinding binding;

        @Override
        protected void bindView(@NonNull View itemView) {
            binding = ItemTextListBinding.bind(itemView);
        }
    }

}
