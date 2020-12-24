package bd.com.evaly.evalyshop.ui.cart.controller;

import androidx.core.content.ContextCompat;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.ItemCartProductBinding;
import bd.com.evaly.evalyshop.databinding.ItemCartShopBinding;
import bd.com.evaly.evalyshop.ui.cart.CartViewModel;
import bd.com.evaly.evalyshop.ui.cart.model.CartProductBinder;
import bd.com.evaly.evalyshop.ui.cart.model.CartProductModel_;
import bd.com.evaly.evalyshop.ui.cart.model.CartShopModel_;

public class CartController extends EpoxyController {

    private List<CartEntity> list = new ArrayList<>();
    private CartClickListener cartClickListener;
    private CartViewModel viewModel;

    public CartController() {
        setFilterDuplicates(true);
    }

    public void setViewModel(CartViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setCartClickListener(CartClickListener cartClickListener) {
        this.cartClickListener = cartClickListener;
    }

    public void setList(List<CartEntity> list) {
        this.list = list;
    }

    @Override
    protected void buildModels() {

        int index = 0;
        for (CartEntity item : list) {

            boolean addShopHeader;
            boolean endOfGroup;

            String thisShopSlug = list.get(index).getShopSlug();
            if (index == 0)
                addShopHeader = true;
            else
                addShopHeader = !list.get(index - 1).getShopSlug().equals(thisShopSlug);

            if (index == (list.size() - 1))
                endOfGroup = true;
            else
                endOfGroup = !list.get(index + 1).getShopSlug().equals(thisShopSlug);

            new CartShopModel_()
                    .id("cart_shop_item", item.getSlug() + " " + item.getShopSlug())
                    .model(item)
                    .onBind((model, view, position) -> {
                        ItemCartShopBinding binding = (ItemCartShopBinding) view.getDataBinding();
                        String shopName = model.model().getShopName();
                        if (shopName != null && !shopName.equals(""))
                            binding.shop.setText(shopName);
                        else
                            binding.shop.setText("Unknown Seller");
                    })
                    .addIf(addShopHeader, this);

            new CartProductModel_()
                    .id("cart_item", item.getSlug() + " " + item.getShopSlug())
                    .model(item)
                    .onBind((model, view, position) -> {
                        ItemCartProductBinding binding = (ItemCartProductBinding) view.getDataBinding();
                        CartProductBinder.bind(binding, model.model());
                        if (endOfGroup)
                            binding.container.setBackground(ContextCompat.getDrawable(binding.container.getContext(), R.drawable.bg_white_round_bottom));
                        else
                            binding.container.setBackground(ContextCompat.getDrawable(binding.container.getContext(), R.drawable.bg_white_square));
                    })
                    .checkedListener((checkBox, isChecked, entity) -> {
                        viewModel.selectBySlug(entity.getProductID(), isChecked);
                    })
                    .clickListener((model, parentView, clickedView, position) -> {
                        cartClickListener.onClick(model.model().getName(), model.model().getSlug());
                    })
                    .shopClickListener((model, parentView, clickedView, position) -> {
                        cartClickListener.onShopClick(model.model().getShopName(), model.model().getShopSlug());
                    })
                    .increaseQuantity((model, parentView, clickedView, position) -> {
                        viewModel.increaseQuantity(model.model().getProductID());
                    })
                    .deceaseQuantity((model, parentView, clickedView, position) -> {
                        viewModel.decreaseQuantity(model.model().getProductID());
                    })
                    .addTo(this);

            index++;
        }
    }

    public interface CartClickListener {
        void onClick(String productName, String productSlug);

        void onShopClick(String shopName, String shopSlug);
    }


}
