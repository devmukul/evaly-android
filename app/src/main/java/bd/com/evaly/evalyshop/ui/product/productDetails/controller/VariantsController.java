package bd.com.evaly.evalyshop.ui.product.productDetails.controller;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.databinding.ItemVariationImageBinding;
import bd.com.evaly.evalyshop.databinding.ItemVariationSizeBinding;
import bd.com.evaly.evalyshop.models.product.productDetails.AttributeValuesItem;
import bd.com.evaly.evalyshop.models.product.productDetails.AttributesItem;
import bd.com.evaly.evalyshop.ui.product.productDetails.models.VariantCarouselModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.models.VariantImageItemModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.models.VariantItemModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.models.VariantTitleModel_;
import bd.com.evaly.evalyshop.util.BindingUtils;

public class VariantsController extends EpoxyController {

    private List<Integer> selectedVariants = new ArrayList<>();
    private List<AttributesItem> list = new ArrayList<>();

    public VariantsController() {
        setFilterDuplicates(true);
    }

    @Override
    protected void buildModels() {

        for (AttributesItem rootItem : list) {
            new VariantTitleModel_()
                    .id("var_title", rootItem.getAttributeSlug())
                    .title(rootItem.getAttributeName())
                    .addTo(this);

            List<AttributeValuesItem> variantItems = rootItem.getAttributeData().getValues();
            List<DataBindingEpoxyModel> models = new ArrayList<>();

            for (AttributeValuesItem item : variantItems) {
                if (selectedVariants.contains(item.getKey()))
                    item.setSelected(true);

                if (rootItem.getAttributeName().toLowerCase().contains("color"))
                    models.add(new VariantImageItemModel_()
                            .id("var_item", item.getKey())
                            .model(item)
                            .isSelected(item.isSelected())
                            .onBind((model, view, position) -> {
                                ItemVariationImageBinding binding = (ItemVariationImageBinding) view.getDataBinding();
                                BindingUtils.markImageVariation(binding.holder, model.isSelected());
                            })
                            .clickListener((model, parentView, clickedView, position) -> {
                                ItemVariationImageBinding binding = (ItemVariationImageBinding) parentView.getDataBinding();
                                item.setSelected(true);
                                selectedVariants.add(item.getKey());
                                BindingUtils.markImageVariation(binding.holder, item.isSelected());

                                for (AttributeValuesItem subItem : variantItems)
                                    if (subItem.getKey() != model.model().getKey()) {
                                        subItem.setSelected(false);
                                        selectedVariants.remove((Integer) subItem.getKey());
                                    }
                                requestModelBuild();
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
                            })
                            .clickListener((model, parentView, clickedView, position) -> {
                                ItemVariationSizeBinding binding = (ItemVariationSizeBinding) parentView.getDataBinding();
                                item.setSelected(true);
                                selectedVariants.add(item.getKey());
                                BindingUtils.markVariation(binding.cardSize, item.isSelected());

                                for (AttributeValuesItem subItem : variantItems)
                                    if (subItem.getKey() != model.model().getKey()) {
                                        subItem.setSelected(false);
                                        selectedVariants.remove((Integer) subItem.getKey());
                                    }
                                requestModelBuild();
                            }));
            }

            new VariantCarouselModel_()
                    .id("var_carousel", rootItem.getAttributeSlug())
                    .models(models)
                    .addTo(this);
        }

    }

    public void setList(List<AttributesItem> list) {
        this.list = list;
    }

    public void setSelectedVariants(List<Integer> selectedVariants) {
        this.selectedVariants = selectedVariants;
    }
}
