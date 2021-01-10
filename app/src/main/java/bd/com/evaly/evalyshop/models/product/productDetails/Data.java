package bd.com.evaly.evalyshop.models.product.productDetails;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Data {

    @SerializedName("specifications")
    private List<ProductSpecificationsItem> productSpecifications;

    @SerializedName("attributes")
    private List<AttributesItem> attributes;

    @SerializedName("product_variants")
    private List<ProductVariantsItem> productVariants;

    public List<ProductSpecificationsItem> getProductSpecifications() {
        if (productSpecifications == null)
            return new ArrayList<>();
        return productSpecifications;
    }

    public void setProductSpecifications(List<ProductSpecificationsItem> productSpecifications) {
        this.productSpecifications = productSpecifications;
    }

    public List<AttributesItem> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributesItem> attributes) {
        this.attributes = attributes;
    }

    public List<ProductVariantsItem> getProductVariants() {
        return productVariants;
    }

    public void setProductVariants(List<ProductVariantsItem> productVariants) {
        this.productVariants = productVariants;
    }

    @Override
    public String toString() {
        return
                "Data{" +
                        "product_specifications = '" + productSpecifications + '\'' +
                        ",attributes = '" + attributes + '\'' +
                        ",product_variants = '" + productVariants + '\'' +
                        "}";
    }
}