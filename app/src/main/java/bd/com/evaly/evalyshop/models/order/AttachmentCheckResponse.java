package bd.com.evaly.evalyshop.models.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AttachmentCheckResponse {

    @SerializedName("attachment_required")
    private boolean attachmentRequired;

    @SerializedName("shop_name")
    private String shopName;

    @SerializedName("shop_image")
    private String shopImage;

    @SerializedName("items")
    private List<Integer> items;

    @SerializedName("shop_slug")
    private String shopSlug;

    @SerializedName("apply_delivery_charge")
    private boolean applyDeliveryCharge;

    @SerializedName("delivery_charge")
    private double deliveryCharge;

    public boolean isApplyDeliveryCharge() {
        return applyDeliveryCharge;
    }

    public void setApplyDeliveryCharge(boolean applyDeliveryCharge) {
        this.applyDeliveryCharge = applyDeliveryCharge;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public boolean isAttachmentRequired() {
        return attachmentRequired;
    }

    public void setAttachmentRequired(boolean attachmentRequired) {
        this.attachmentRequired = attachmentRequired;
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

    public List<Integer> getItems() {
        return items;
    }

    public void setItems(List<Integer> items) {
        this.items = items;
    }

    public String getShopSlug() {
        return shopSlug;
    }

    public void setShopSlug(String shopSlug) {
        this.shopSlug = shopSlug;
    }

    @Override
    public String toString() {
        return
                "CheckoutItemResponse{" +
                        "attachment_required = '" + attachmentRequired + '\'' +
                        ",shop_name = '" + shopName + '\'' +
                        ",shop_image = '" + shopImage + '\'' +
                        ",items = '" + items + '\'' +
                        ",shop_slug = '" + shopSlug + '\'' +
                        "}";
    }
}