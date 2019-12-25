package bd.com.evaly.evalyshop.models.product;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Products {

   String sku,thumbnailSM,name,slug,description,categoryName,categorySlug,brandName,brandSlug,brandThumbnail;
   ArrayList<String> thumbnail;
   int priceMax,priceMin;
   boolean approved;

    public Products(String sku, ArrayList<String> thumbnail, String thumbnailSM, String name, String slug, String description, String categoryName, String categorySlug, String brandName, String brandSlug, String brandThumbnail, int priceMax, int priceMin, boolean approved) {
        this.sku = sku;
        this.thumbnail = thumbnail;
        this.thumbnailSM = thumbnailSM;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.categoryName = categoryName;
        this.categorySlug = categorySlug;
        this.brandName = brandName;
        this.brandSlug = brandSlug;
        this.brandThumbnail = brandThumbnail;
        this.priceMax = priceMax;
        this.priceMin = priceMin;
        this.approved = approved;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public ArrayList<String> getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ArrayList<String> thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnailSM() {
        return thumbnailSM;
    }

    public void setThumbnailSM(String thumbnailSM) {
        this.thumbnailSM = thumbnailSM;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandSlug() {
        return brandSlug;
    }

    public void setBrandSlug(String brandSlug) {
        this.brandSlug = brandSlug;
    }

    public String getBrandThumbnail() {
        return brandThumbnail;
    }

    public void setBrandThumbnail(String brandThumbnail) {
        this.brandThumbnail = brandThumbnail;
    }

    public int getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(int priceMax) {
        this.priceMax = priceMax;
    }

    public int getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(int priceMin) {
        this.priceMin = priceMin;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
