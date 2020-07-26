package bd.com.evaly.evalyshop.models.product;

import java.util.ArrayList;

public class ProductVariants {
    int variantID,minPrice,maxPrice,attribute;
    String productName,description,brandName,colorImage;
    ArrayList<String> images;

    public ProductVariants() {
    }

    public ProductVariants(int variantID, int minPrice, int maxPrice, int attribute, String productName, String description, String brandName, String colorImage, ArrayList<String> images) {
        this.variantID = variantID;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.attribute = attribute;
        this.productName = productName;
        this.description = description;
        this.brandName = brandName;
        this.colorImage = colorImage;
        this.images = images;
    }

    public int getVariantID() {
        return variantID;
    }

    public void setVariantID(int variantID) {
        this.variantID = variantID;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getColorImage() {
        return colorImage;
    }

    public void setColorImage(String colorImage) {
        this.colorImage = colorImage;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
