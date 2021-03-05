package bd.com.evaly.evalyshop.models.search.product.response;

import com.google.gson.annotations.SerializedName;

import bd.com.evaly.evalyshop.models.BaseModel;

public class ProductsItem extends BaseModel {

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("product_image")
    private String productImage;

    @SerializedName("brand_slug")
    private String brandSlug;

    @SerializedName("brand_name")
    private String brandName;

    @SerializedName("shop_name")
    private String shopName;

    @SerializedName("version")
    private int version;

    @SerializedName("shop_item_id")
    private int shopItemId;

    @SerializedName("shop_slug")
    private String shopSlug;

    @SerializedName("discounted_price")
    private double discountedPrice;

    @SerializedName("price")
    private double price;

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private int id;

    @SerializedName("slug")
    private String slug;

    @SerializedName("category_slug")
    private String categorySlug;

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setBrandSlug(String brandSlug) {
        this.brandSlug = brandSlug;
    }

    public String getBrandSlug() {
        return brandSlug;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setShopItemId(int shopItemId) {
        this.shopItemId = shopItemId;
    }

    public int getShopItemId() {
        return shopItemId;
    }

    public void setShopSlug(String shopSlug) {
        this.shopSlug = shopSlug;
    }

    public String getShopSlug() {
        return shopSlug;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    @Override
    public String toString() {
        return
                "ProductsItem{" +
                        "category_name = '" + categoryName + '\'' +
                        ",product_image = '" + productImage + '\'' +
                        ",brand_slug = '" + brandSlug + '\'' +
                        ",brand_name = '" + brandName + '\'' +
                        ",shop_name = '" + shopName + '\'' +
                        ",version = '" + version + '\'' +
                        ",shop_item_id = '" + shopItemId + '\'' +
                        ",shop_slug = '" + shopSlug + '\'' +
                        ",discounted_price = '" + discountedPrice + '\'' +
                        ",price = '" + price + '\'' +
                        ",name = '" + name + '\'' +
                        ",id = '" + id + '\'' +
                        ",slug = '" + slug + '\'' +
                        ",category_slug = '" + categorySlug + '\'' +
                        "}";
    }
}