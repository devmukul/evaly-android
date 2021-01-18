package bd.com.evaly.evalyshop.ui.checkout.controller;

import androidx.core.content.ContextCompat;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyController;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.ItemCartShopBinding;
import bd.com.evaly.evalyshop.databinding.ItemCheckoutProductBinding;
import bd.com.evaly.evalyshop.ui.cart.model.CartShopModel_;
import bd.com.evaly.evalyshop.ui.checkout.CheckoutViewModel;
import bd.com.evaly.evalyshop.ui.checkout.model.AttachmentCarouselModel_;
import bd.com.evaly.evalyshop.ui.checkout.model.CheckoutAttachmentModel_;
import bd.com.evaly.evalyshop.ui.checkout.model.CheckoutAttachmentUploadModel_;
import bd.com.evaly.evalyshop.ui.checkout.model.CheckoutProductModel_;
import bd.com.evaly.evalyshop.ui.checkout.model.ImagePickerItemModel_;

public class CheckoutProductController extends EpoxyController {

    private List<CartEntity> list = new ArrayList<>();
    private HashMap<String, Boolean> showAttachmentMap = new HashMap<>();
    private HashMap<String, List<String>> attachmentMap = new HashMap<>();
    private CheckoutViewModel viewModel;
    private String selectedShopSlug = "";

    public CheckoutProductController() {
        setFilterDuplicates(true);
    }

    public void setViewModel(CheckoutViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setList(List<CartEntity> list) {
        this.list = list;
    }

    public void setShowAttachmentMap(HashMap<String, Boolean> showAttachmentMap) {
        this.showAttachmentMap = showAttachmentMap;
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
                    .id("cart_shop_item", item.getProductID() + " " + item.getShopSlug())
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
                    .id("cart_item", item.getProductID() + " " + item.getShopSlug())
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
            boolean attachmentRequired = (showAttachmentMap.get(thisShopSlug) != null && showAttachmentMap.get(thisShopSlug));

            if (attachmentList.size() < 3 && attachmentRequired)
                attachmentModels.add(new ImagePickerItemModel_()
                        .id("image_picker_base", thisShopSlug)
                        .isAdd(true)
                        .showRemove(false)
                        .clickListener((model, parentView, clickedView, position) -> {
                            viewModel.setSelectedShopSlug(thisShopSlug);
                            viewModel.imagePicker.call();
                        }));

            for (String url : attachmentList) {
                attachmentModels.add(new ImagePickerItemModel_()
                        .id("image_picker", addShopHeader + url)
                        .isAdd(false)
                        .showRemove(true)
                        .url(url)
                        .clickListener((model, parentView, clickedView, position) -> {

                        })
                        .deleteClickListener((model, parentView, clickedView, position) -> {
                            if (viewModel.getAttachmentList(thisShopSlug).size() == 3)
                                viewModel.deleteAttachment(thisShopSlug, position);
                            else
                                viewModel.deleteAttachment(thisShopSlug, position - 1);
                        }));
            }

            new CheckoutAttachmentModel_()
                    .id("attachment_title", thisShopSlug)
                    .addIf(endOfGroup && attachmentRequired, this);

            new CheckoutAttachmentUploadModel_()
                    .id("attachment_btn", thisShopSlug)
                    .clickListener((model, parentView, clickedView, position) -> {
                        viewModel.setSelectedShopSlug(thisShopSlug);
                        viewModel.imagePicker.call();
                    })
                    .addIf(endOfGroup && attachmentRequired && attachmentList.size() == 0, this);

            new AttachmentCarouselModel_()
                    .id("carousel_attachment", thisShopSlug)
                    .models(attachmentModels)
                    .padding(Carousel.Padding.dp(0, 10, 0, 5, 12))
                    .addIf(endOfGroup && attachmentRequired && attachmentList.size() > 0, this);

            index++;
        }
    }

    public void setAttachmentMap(HashMap<String, List<String>> attachmentMap) {
        Logger.e(new Gson().toJson(attachmentMap));
        this.attachmentMap = attachmentMap;
    }
}
