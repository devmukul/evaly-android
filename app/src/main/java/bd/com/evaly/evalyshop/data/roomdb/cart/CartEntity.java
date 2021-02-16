package bd.com.evaly.evalyshop.data.roomdb.cart;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "cart_table")
public class CartEntity implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "shop_item_id")
    @SerializedName("shop_item_id")
    private String productID;

    @ColumnInfo(name = "product_slug")
    @SerializedName("product_slug")
    private String slug;

    @ColumnInfo(name = "product_name")
    @SerializedName("product_name")
    private String name;

    @ColumnInfo(name = "product_image")
    @SerializedName("product_image")
    private String image;

    @ColumnInfo(name = "price")
    @SerializedName("price")
    private String price;

    @ColumnInfo(name = "discounted_price")
    @SerializedName("discounted_price")
    private String discountedPrice;

    @ColumnInfo(name = "shop_image")
    @SerializedName("shop_image")
    private String shopImage;

    @ColumnInfo(name = "time")
    private long time;

    @ColumnInfo(name = "quantity")
    @SerializedName("quantity")
    private int quantity;

    @ColumnInfo(name = "shop_name")
    @SerializedName("shop_name")
    private String shopName;

    @ColumnInfo(name = "shop_slug")
    @SerializedName("shop_slug")
    private String shopSlug;

    @ColumnInfo(name = "shop_json")
    private String shopJson;

    @ColumnInfo(name = "is_selected")
    private boolean selected = true;

    @ColumnInfo(name = "variant_details")
    private String variantDetails;

    private boolean showShopTitle;

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getShopImage() {
        return shopImage;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }

    public String getVariantDetails() {
        return variantDetails;
    }

    public void setVariantDetails(String variantDetails) {
        this.variantDetails = variantDetails;
    }

    public boolean isShowShopTitle() {
        return showShopTitle;
    }

    public void setShowShopTitle(boolean showShopTitle) {
        this.showShopTitle = showShopTitle;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getPriceInt() {
        try {
            return Integer.parseInt(price);
        } catch (Exception e) {
            return 0;
        }
    }

    public double getPriceDouble() {
        try {
            return Double.parseDouble(price);
        } catch (Exception e) {
            return 0;
        }
    }

    public void setPriceRound(String p) {
        p = String.valueOf((int) Math.round(Double.parseDouble(p)));
        this.price = p;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getShopSlug() {
        return shopSlug;
    }

    public void setShopSlug(String shopSlug) {
        this.shopSlug = shopSlug;
    }

    @NotNull
    public String getProductID() {
        return productID;
    }

    public void setProductID(@NotNull String productID) {
        this.productID = productID;
    }

    public String getShopJson() {
        return shopJson;
    }

    public void setShopJson(String shopJson) {
        this.shopJson = shopJson;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
