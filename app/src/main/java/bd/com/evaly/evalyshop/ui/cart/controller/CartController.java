package bd.com.evaly.evalyshop.ui.cart.controller;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.ItemCartBinding;
import bd.com.evaly.evalyshop.ui.cart.model.BindCartItemModel;
import bd.com.evaly.evalyshop.ui.cart.model.CartItemModel_;

public class CartController extends EpoxyController {

    private List<CartEntity> list = new ArrayList<>();
    private CartClickListener cartClickListener;

    public CartController() {
        setFilterDuplicates(true);
    }

    public void setCartClickListener(CartClickListener cartClickListener) {
        this.cartClickListener = cartClickListener;
    }

    public void setList(List<CartEntity> list) {
        this.list = list;
    }

    @Override
    protected void buildModels() {
        for (CartEntity item : list) {
            new CartItemModel_()
                    .id("cart_item", item.getSlug() + " " + item.getShopSlug())
                    .model(item)
                    .onBind((model, view, position) -> BindCartItemModel.bind((ItemCartBinding) view.getDataBinding(), model.model()))
                    .clickListener((model, parentView, clickedView, position) -> {
                        cartClickListener.onClick(model.model().getName(), model.model().getSlug());
                    })
                    .shopClickListener((model, parentView, clickedView, position) -> {
                        cartClickListener.onShopClick(model.model().getShopName(), model.model().getShopSlug());
                    })
                    .increaseQuantity((model, parentView, clickedView, position) -> {
                        // call viewmodel
                    })
                    .deceaseQuantity((model, parentView, clickedView, position) -> {
                        // call viewmodel
                    })
                    .addTo(this);
        }
    }

    public interface CartClickListener {
        void onClick(String productName, String productSlug);

        void onShopClick(String shopName, String shopSlug);
    }


}
