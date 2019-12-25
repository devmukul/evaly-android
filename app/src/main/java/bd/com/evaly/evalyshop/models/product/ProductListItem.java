package bd.com.evaly.evalyshop.models.product;

public class ProductListItem {

    String thumbnailSM,name,slug,categoryName,categorySlug,brandName,brandSlug, sku;
    int priceMax,priceMin, discountedPrice = 0;
    boolean approved;


    public ProductListItem(){

    }

    public ProductListItem(String thumbnailSM, String name, String slug, String categoryName, String categorySlug, String brandName, String brandSlug, int priceMax, int priceMin, boolean approved) {

        this.thumbnailSM = thumbnailSM;
        this.name = name;
        this.slug = slug;
        this.categoryName = categoryName;
        this.categorySlug = categorySlug;
        this.brandName = brandName;
        this.brandSlug = brandSlug;
        this.priceMax = priceMax;
        this.priceMin = priceMin;
        this.approved = approved;
    }


    public int getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(int discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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
