package bd.com.evaly.evalyshop.ui.checkout.controller;

import androidx.core.content.ContextCompat;

import com.airbnb.epoxy.CarouselModel_;
import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.ItemCartShopBinding;
import bd.com.evaly.evalyshop.databinding.ItemCheckoutProductBinding;
import bd.com.evaly.evalyshop.ui.cart.model.CartShopModel_;
import bd.com.evaly.evalyshop.ui.checkout.model.CheckoutProductModel_;

public class CheckoutProductController extends EpoxyController {

    private List<CartEntity> list = new ArrayList<>();
    private HashMap<String, List<String>> attachmentMap = new HashMap<>();

    public CheckoutProductController() {
        setFilterDuplicates(true);
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
                    .noMargin(true)
                    .addIf(addShopHeader, this);

            new CheckoutProductModel_()
                    .id("cart_item", item.getSlug() + " " + item.getShopSlug())
                    .model(item)
                    .onBind((model, view, position) -> {
                        ItemCheckoutProductBinding binding = (ItemCheckoutProductBinding) view.getDataBinding();
                        if (endOfGroup)
                            binding.container.setBackground(ContextCompat.getDrawable(binding.container.getContext(), R.drawable.bg_white_round_bottom));
                        else
                            binding.container.setBackground(ContextCompat.getDrawable(binding.container.getContext(), R.drawable.bg_white_square));
                    })
                    .addTo(this);

            List<String> attachmentList = new ArrayList<>();

            if (attachmentMap.containsKey(thisShopSlug) && attachmentMap.get(thisShopSlug) != null)
                attachmentList = attachmentMap.get(thisShopSlug);

            List<DataBindingEpoxyModel> attachmentModels = new ArrayList<>();

            if (attachmentList.size() < 3)
                attachmentModels.add(new ImagePickerItemModel_()
                        .id("image_picker_base", thisShopSlug)
                        .isAdd(true)
                        .clickListener((model, parentView, clickedView, position) -> {

                        }));

            for (String url : attachmentList) {
                attachmentModels.add(new ImagePickerItemModel_()
                        .id("image_picker", addShopHeader, url)
                        .isAdd(false)
                        .url(url)
                        .clickListener((model, parentView, clickedView, position) -> {

                        })
                        .deleteClickListener((model, parentView, clickedView, position) -> {
//                            if (viewModel.getImageUrls().size() == 5)
//                                viewModel.delete(position);
//                            else
//                                viewModel.delete(position - 1);
                        }));
            }

            new CarouselModel_()
                    .id("carousel_attachment", thisShopSlug)
                    .addIf(!addShopHeader, this);

            index++;
        }
    }

    public void setAttachmentMap(HashMap<String, List<String>> attachmentMap) {
        this.attachmentMap = attachmentMap;
    }
}
