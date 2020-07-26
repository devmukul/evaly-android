package bd.com.evaly.evalyshop.models.product.productDetails;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AvailableShopModel implements Serializable {

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

    public void setPrice(double price) {
        this.price = price;
    }

    public Double getPrice() {
        if (price == null)
            return 0.0;
        return price;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }

    public String getShopImage() {
        return shopImage;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }

    public int getInStock() {
        return inStock;
    }

    public void setShopOwner(String shopOwner) {
        this.shopOwner = shopOwner;
    }

    public String getShopOwner() {
        return shopOwner;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopItemId(int shopItemId) {
        this.shopItemId = shopItemId;
    }

    public int getShopItemId() {
        return shopItemId;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactNumber() {
        return contactNumber;
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

    public Double getDiscountedPrice() {
        if (discountedPrice == null)
            return 0.0;
        return discountedPrice;
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