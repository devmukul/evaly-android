package bd.com.evaly.evalyshop.models.product;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.BaseModel;


public class ProductItem extends BaseModel {

    @SerializedName(value = "max_price")
    private String maxPrice;

    @SerializedName(value = "min_price")
    private String minPrice;

    @SerializedName(value = "price")
    private double price;

    @SerializedName("name")
    private String name;

    @SerializedName("image_urls")
    private List<String> imageUrls;

    @SerializedName("image")
    private String singleImage;

    @SerializedName("price_type")
    private String priceType;

    @SerializedName("slug")
    private String slug;

    @SerializedName(value = "min_discounted_price", alternate = "discounted_price")
    private String minDiscountedPrice;

    @SerializedName("shop_slug")
    private String shopSlug;

    private String uniqueId;

    //@SerializedName("in_stock")
    private int inStock;

    public String getShopSlug() {
        return shopSlug;
    }

    public void setShopSlug(String shopSlug) {
        this.shopSlug = shopSlug;
    }

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getImageUrls() {
        if (imageUrls == null)
            return new ArrayList<>();
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getFirstImage() {
        if (singleImage != null && !singleImage.isEmpty())
            return singleImage;
        if (imageUrls == null || imageUrls.size() == 0)
            return "";
        else
            return imageUrls.get(0);
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getMinDiscountedPrice() {
        return minDiscountedPrice;
    }

    public void setMinDiscountedPrice(String minDiscountedPrice) {
        this.minDiscountedPrice = minDiscountedPrice;
    }

    public double getMaxPriceD() {
        try {
            return Math.round(Double.parseDouble(maxPrice));
        } catch (Exception e) {
            if (price == 0)
                return 0.0;
            else
                return price;
        }
    }

    public double getMinPriceD() {
        try {
            return Math.round(Double.parseDouble(minPrice));
        } catch (Exception e) {
            if (price == 0)
                return 0.0;
            else
                return price;
        }
    }

    public double getMinDiscountedPriceD() {
        try {
            return Math.round(Double.parseDouble(minDiscountedPrice));
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public String toString() {
        return
                "ProductItem{" +
                        "max_price = '" + maxPrice + '\'' +
                        ",min_price = '" + minPrice + '\'' +
                        ",name = '" + name + '\'' +
                        ",image_urls = '" + imageUrls + '\'' +
                        ",price_type = '" + priceType + '\'' +
                        ",slug = '" + slug + '\'' +
                        ",min_discounted_price = '" + minDiscountedPrice + '\'' +
                        "}";
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}