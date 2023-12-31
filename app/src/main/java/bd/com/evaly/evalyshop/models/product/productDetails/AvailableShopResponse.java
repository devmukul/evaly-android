package bd.com.evaly.evalyshop.models.product.productDetails;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AvailableShopResponse implements Serializable {

    @SerializedName("price")
    private Double price;

    @SerializedName("discount_value")
    private double discountValue;

    @SerializedName("shop_name")
    private String shopName;

    @SerializedName("shop_image")
    private String shopImage;

    @SerializedName("discount_type")
    private String discountType;

    @SerializedName("in_stock")
    private int inStock;

    @SerializedName("shop_owner")
    private String shopOwner;

    @SerializedName("shop_address")
    private String shopAddress;

    @SerializedName("shop_item_id")
    private int shopItemId;

    @SerializedName("contact_number")
    private String contactNumber;

    @SerializedName("shop_slug")
    private String shopSlug;

    @SerializedName("discounted_price")
    private Double discountedPrice;

    @SerializedName("is_express_shop")
    private boolean expressShop;

    public Double getPrice() {
        if (price == null)
            return 0.0;
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopImage() {
        return shopImage;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }

    public String getShopOwner() {
        return shopOwner;
    }

    public void setShopOwner(String shopOwner) {
        this.shopOwner = shopOwner;
    }

    public String getShopAddress() {
        if (shopAddress == null)
            return "";
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public int getShopItemId() {
        return shopItemId;
    }

    public void setShopItemId(int shopItemId) {
        this.shopItemId = shopItemId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getShopSlug() {
        return shopSlug;
    }

    public void setShopSlug(String shopSlug) {
        this.shopSlug = shopSlug;
    }

    public Double getDiscountedPrice() {
        if (discountedPrice == null)
            return getPrice();
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public boolean isExpressShop() {
        return expressShop;
    }

    public void setExpressShop(boolean expressShop) {
        this.expressShop = expressShop;
    }

    @Override
    public String toString() {
        return
                "AvailableShopModel{" +
                        "price = '" + price + '\'' +
                        ",discount_value = '" + discountValue + '\'' +
                        ",shop_name = '" + shopName + '\'' +
                        ",shop_image = '" + shopImage + '\'' +
                        ",discount_type = '" + discountType + '\'' +
                        ",in_stock = '" + inStock + '\'' +
                        ",shop_owner = '" + shopOwner + '\'' +
                        ",shop_address = '" + shopAddress + '\'' +
                        ",shop_item_id = '" + shopItemId + '\'' +
                        ",contact_number = '" + contactNumber + '\'' +
                        ",shop_slug = '" + shopSlug + '\'' +
                        ",discounted_price = '" + discountedPrice + '\'' +
                        "}";
    }
}