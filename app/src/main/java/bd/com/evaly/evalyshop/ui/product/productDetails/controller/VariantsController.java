package bd.com.evaly.evalyshop.ui.product.productDetails.controller;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.ItemVariationImageBinding;
import bd.com.evaly.evalyshop.databinding.ItemVariationSizeBinding;
import bd.com.evaly.evalyshop.models.product.productDetails.AttributeValuesItem;
import bd.com.evaly.evalyshop.models.product.productDetails.AttributesItem;
import bd.com.evaly.evalyshop.ui.product.productDetails.models.VariantCarouselModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.models.VariantImageItemModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.models.VariantItemModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.models.VariantTitleModel_;
import bd.com.evaly.evalyshop.util.BindingUtils;
import bd.com.evaly.evalyshop.util.Utils;

public class VariantsController extends EpoxyController {

    private List<Integer> selectedVariants = new ArrayList<>();
    private HashMap<String, String> selectedVariantsMap = new HashMap<>();
    private List<AttributesItem> list = new ArrayList<>();
    private SelectListener selectListener;

    public void setSelectListener(SelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public interface SelectListener {
        void findVariantByController();
    }

    public VariantsController() {
        setFilterDuplicates(true);
    }

    @Override
    protected void buildModels() {

        for (AttributesItem rootItem : list) {
            new VariantTitleModel_()
                    .id("var_title", rootItem.getAttributeName())
                    .title(rootItem.getAttributeName())
                    .addTo(this);

            List<AttributeValuesItem> variantItems = rootItem.getAttributeData().getValues();
            List<DataBindingEpoxyModel> models = new ArrayList<>();

            for (AttributeValuesItem item : variantItems) {
                if (selectedVariants.contains(item.getKey()))
                    item.setSelected(true);

                if (item.getColor_image() != null)
                    models.add(new VariantImageItemModel_()
                            .id("var_item", item.getKey())
                            .model(item)
                            .isSelected(item.isSelected())
                            .onBind((model, view, position) -> {
                                ItemVariationImageBinding binding = (ItemVariationImageBinding) view.getDataBinding();
                                BindingUtils.markImageVariation(binding.holder, model.isSelected());
                                if (model.isSelected())
                                    selectedVariantsMap.put(rootItem.getAttributeName(), model.model().getValue());
                            })
                            .clickListener((model, parentView, clickedView, position) -> {
                                ItemVariationImageBinding binding = (ItemVariationImageBinding) parentView.getDataBinding();
                                updateSelection(model.model(), variantItems);
                                BindingUtils.markImageVariation(binding.holder, model.model().isSelected());
                            })
                    );
                else
                    models.add(new VariantItemModel_()
                            .id("var_item", item.getKey())
                            .model(item)
                            .isSelected(item.isSelected())
                            .onBind((model, view, position) -> {
                                ItemVariationSizeBinding binding = (ItemVariationSizeBinding) view.getDataBinding();
                                BindingUtils.markVariation(binding.cardSize, model.isSelected());
                                if (model.isSelected())
                                    selectedVariantsMap.put(rootItem.getAttributeName(), model.model().getValue());
                            })
                            .clickListener((model, parentView, clickedView, position) -> {
                                ItemVariationSizeBinding binding = (ItemVariationSizeBinding) parentView.getDataBinding();
                                updateSelection(model.model(), variantItems);
                                BindingUtils.markVariation(binding.cardSize, model.model().isSelected());
                            }));
            }

            new VariantCarouselModel_()
                    .id("var_carousel", rootItem.getAttributeName())
                    .models(models)
                    .padding(new Carousel.Padding((int) Utils.convertDpToPixel(20, AppController.getmContext()), 0, 0, 0, 0))
                    .addTo(this);
        }
    }


    private void updateSelection(AttributeValuesItem model, List<AttributeValuesItem> variantItems) {

        model.setSelected(true);
        selectedVariants.remove((Integer) model.getKey());
        selectedVariants.add(model.getKey());

        for (AttributeValuesItem subItem : variantItems)
            if (subItem.getKey() != model.getKey()) {
                subItem.setSelected(false);
                selectedVariants.remove((Integer) subItem.getKey());
            }

        selectListener.findVariantByController();
        requestModelBuild();
    }

    public void setList(List<AttributesItem> list) {
        this.list = list;
    }

    public List<Integer> getSelectedVariants() {
        return selectedVariants;
    }

    public void setSelectedVariants(List<Integer> selectedVariants) {
        this.selectedVariants = selectedVariants;
    }

    public HashMap<String, String> getSelectedVariantsMap() {
        return selectedVariantsMap;
    }
}
