package bd.com.evaly.evalyshop.models.product.productDetails;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProductVariantsItem {

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("category_image")
    private String categoryImage;

    @SerializedName("brand_image")
    private String brandImage;

    @SerializedName("brand_slug")
    private String brandSlug;

    @SerializedName("brand_name")
    private String brandName;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("product_images")
    private List<String> productImages;

    @SerializedName("approved")
    private boolean approved;

    @SerializedName("variant_id")
    private int variantId;

    @SerializedName("max_price")
    private int maxPrice;

    @SerializedName("min_price")
    private int minPrice;

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("attribute_values")
    private List<Integer> attributeValues;

    @SerializedName("color_image")
    private String colorImage;

    @SerializedName("sku")
    private String sku;

    @SerializedName("product_description")
    private String productDescription;

    @SerializedName("category_slug")
    private String categorySlug;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getBrandImage() {
        return brandImage;
    }

    public void setBrandImage(String brandImage) {
        this.brandImage = brandImage;
    }

    public String getBrandSlug() {
        return brandSlug;
    }

    public void setBrandSlug(String brandSlug) {
        this.brandSlug = brandSlug;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<String> getProductImages() {
        if (productImages == null)
            return new ArrayList<>();
        return productImages;
    }

    public void setProductImages(List<String> productImages) {
        this.productImages = productImages;
    }

    public boolean getApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public List<Integer> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(List<Integer> attributeValues) {
        this.attributeValues = attributeValues;
    }

    public String getColorImage() {
        return colorImage;
    }

    public void setColorImage(String colorImage) {
        this.colorImage = colorImage;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    @Override
    public String toString() {
        return
                "{" +
                        "category_name = '" + categoryName + '\'' +
                        ",brand_slug = '" + brandSlug + '\'' +
                        ",brand_name = '" + brandName + '\'' +
                        ",product_name = '" + productName + '\'' +
                        ",product_images = '" + productImages + '\'' +
                        ",approved = '" + approved + '\'' +
                        ",variant_id = '" + variantId + '\'' +
                        ",max_price = '" + maxPrice + '\'' +
                        ",min_price = '" + minPrice + '\'' +
                        ",category_id = '" + categoryId + '\'' +
                        ",attribute_values = '" + attributeValues + '\'' +
                        ",color_image = '" + colorImage + '\'' +
                        ",sku = '" + sku + '\'' +
                        ",product_description = '" + productDescription + '\'' +
                        ",category_slug = '" + categorySlug + '\'' +
                        "}";
    }
}